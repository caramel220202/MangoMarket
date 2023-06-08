package com.example.fastcampusdeepen.SecondHandDeal

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.example.fastcampusdeepen.SecondHandDeal.home.ArticleKey.Companion.DB_ARTICLES
import com.example.fastcampusdeepen.SecondHandDeal.home.ArticleModel
import com.example.fastcampusdeepen.databinding.ActivityArticleAddBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.text.DecimalFormat

class ArticleAddActivity : AppCompatActivity() {
    private val addBinding: ActivityArticleAddBinding by lazy {
        ActivityArticleAddBinding.inflate(layoutInflater)
    }
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }
    private val articleDB: DatabaseReference by lazy {
        Firebase.database.reference.child(DB_ARTICLES)
    }
    private val storage: FirebaseStorage by lazy {
        Firebase.storage
    }
    private var selectedUri: Uri? = null
    private var inputText = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(addBinding.root)

        initResultLauncher()
        initAddImageBtn()
        getCurrentUserID()
        initPriceEditTextviewFormat()
    }

    private fun initPriceEditTextviewFormat() {
        addBinding.priceEditTextview.addTextChangedListener {
            val price = it.toString()
            Log.d("testt", price)

            if (!TextUtils.isEmpty(price)
                && !TextUtils.equals(it.toString(), inputText)
            ) {
                val decimal = DecimalFormat("###,###")

                inputText = decimal.format(price.replace(",", "").toLong())
                addBinding.priceEditTextview.setText(inputText)
                addBinding.priceEditTextview.setSelection(inputText.length)
            }
        }


    }

    private fun initResultLauncher() {
        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == RESULT_OK && it.data != null) {
                    selectedUri = it.data?.data
                    addBinding.addImage.setImageURI(selectedUri)
                    if (selectedUri != null) {
                        val fileUri = selectedUri
                        initUploadBtn(fileUri!!)
                    }
                } else return@registerForActivityResult
            }
    }

    private fun initAddImageBtn() {
        addBinding.addBtn.setOnClickListener {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                )
                        == PackageManager.PERMISSION_GRANTED -> {
                    navigatePhoto()
                }
                shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                -> {
                    popupMessage()
                }

                else -> requestPermissions(
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    1010
                )
            }
            return@setOnClickListener
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1010 -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                navigatePhoto()
            } else {
                Snackbar.make(addBinding.root, "권한을 거부하셨습니다.", Snackbar.LENGTH_LONG).show()
            }
            else -> Snackbar.make(addBinding.root, "이미지를 불러오지 못했습니다.", Snackbar.LENGTH_LONG).show()
        }
    }

    private fun popupMessage() {
        AlertDialog.Builder(this)
            .setTitle("권한이 필요합니다.")
            .setMessage("사진첩을 이용하기위해 권한이 필요합니다.")
            .setPositiveButton("동의하기") { _, _ ->
                navigatePhoto()
            }
            .setNegativeButton("거부하기") { _, _ ->
                Snackbar.make(addBinding.root, "이미지를 불러오지 못했습니다. 권한을 동의해주세요.", Snackbar.LENGTH_LONG)
                    .show()
            }
            .create()
            .show()
    }

    private fun navigatePhoto() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        activityResultLauncher.launch(intent)
    }

    private fun initUploadBtn(uri: Uri) {
        addBinding.upLoadBtn.setOnClickListener {
            val title = addBinding.titleEditTextview.text.toString()
            val price = addBinding.priceEditTextview.text.toString()
            val sellerId = auth.currentUser?.uid.orEmpty()
            val fileUri = uri

            showProgressBar()

            if (uri != null) {
                uploadPhoto(fileUri, successHandler = { fileUri ->
                    uploadArticle(sellerId, title, price, fileUri.toUri())
                },
                    errorHandler = {
                        Toast.makeText(this@ArticleAddActivity, "사진 업로드 실패", Toast.LENGTH_SHORT)
                            .show()
                        hideProgressBar()
                    })
            } else {
                uploadArticle(sellerId, title, price, "".toUri())
            }
            finish()
        }

    }

    private fun uploadArticle(sellerId: String, title: String, price: String, uri: Uri) {
        val model =
            ArticleModel(sellerId, title, System.currentTimeMillis(), price, uri.toString())
        articleDB.push().setValue(model)

        hideProgressBar()

        finish()

    }

    private fun uploadPhoto(uri: Uri, successHandler: (String) -> Unit, errorHandler: () -> Unit) {
        val fileName = "${System.currentTimeMillis()}.png"
        storage.reference.child("article/photo").child(fileName)
            .putFile(uri)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    storage.reference.child("article/photo").child(fileName)
                        .downloadUrl
                        .addOnSuccessListener { uri ->
                            successHandler(uri.toString())
                        }
                        .addOnFailureListener {
                            errorHandler()
                        }
                } else {
                    errorHandler()
                }
            }
    }

    private fun showProgressBar() {
        addBinding.articleUploadProgressBar.isVisible = true

    }

    private fun hideProgressBar() {
        addBinding.articleUploadProgressBar.isVisible = false
    }

    private fun getCurrentUserID(): String {
        if (auth.currentUser == null) {
            Snackbar.make(addBinding.root, "로그인이 되어있지 않습니다. 로그인후 사용해주세요.", Snackbar.LENGTH_SHORT)
                .show()
            finish()
        }
        return auth.currentUser?.uid.orEmpty()
    }
}