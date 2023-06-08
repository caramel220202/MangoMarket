package com.example.fastcampusdeepen.SecondHandDeal.myPage

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.fastcampusdeepen.R
import com.example.fastcampusdeepen.SecondHandDeal.SecondHandDeal
import com.example.fastcampusdeepen.databinding.FragmentHomeBinding
import com.example.fastcampusdeepen.databinding.FragmentMypageBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.w3c.dom.Attr

class MyPageFragment : Fragment(R.layout.fragment_mypage) {
    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }
    private var binding: FragmentMypageBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val myPageBinding = FragmentMypageBinding.bind(view)
        binding = myPageBinding

        initSignBtn()
        initJoinBtn()
    }

    override fun onStart() {
        super.onStart()

        if (auth.currentUser == null) {

            binding?.let { binding ->
                binding?.let { binding ->
                    binding.emailEdit.text.clear()
                    binding.passwordEdit.text.clear()
                    binding.loginBtn.text = "로그인"
                    binding.joinBtn.isEnabled = true
                    binding.loginBtn.isEnabled = true
                    binding.joinBtn.isVisible = true
                    binding.emailEdit.isEnabled = true
                    binding.passwordEdit.isEnabled = true
                }
            }
        } else {
            binding?.let { binding ->
                binding.emailEdit.setText(auth?.currentUser?.email)
                binding.passwordEdit.setText(auth?.currentUser?.uid)
                binding.loginBtn.text = "로그아웃"
                binding.joinBtn.isVisible = false
                binding.loginBtn.isEnabled = true
                binding.emailEdit.isEnabled = false
                binding.passwordEdit.isEnabled = false
            }

        }
    }

    private fun initSignBtn() {
        binding?.let { binding ->
            binding.loginBtn.setOnClickListener {
                val email = binding.emailEdit.text.toString()
                val password = binding.passwordEdit.text.toString()
                if (email.isEmpty() && password.isEmpty()) {
                    Snackbar.make(binding.root, "이메일과 패스워드를 입력해주세요.", Snackbar.LENGTH_SHORT).show()
                } else if (password.length < 6) {
                    Toast.makeText(requireContext(), "비밀번호 6자리 이상 사용해주세요.", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    if (auth.currentUser == null) {
                        auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener {
                                if (it.isSuccessful) {
                                    binding.emailEdit.isEnabled = false
                                    binding.passwordEdit.isEnabled = false
                                    binding.loginBtn.text = "로그아웃"
                                    binding.joinBtn.isVisible = false
                                    binding.emailEdit.setText(auth?.currentUser?.email)
                                    binding.passwordEdit.setText(auth?.currentUser?.uid)
                                    Snackbar.make(binding.root, "로그인 성공", Snackbar.LENGTH_LONG)
                                        .show()
                                    auth?.currentUser?.reload()
                                } else {
                                    Snackbar.make(
                                        binding.root,
                                        "로그인 실패 다시 시도해주세요.",
                                        Snackbar.LENGTH_LONG
                                    ).show()
                                }
                            }
                    } else {
                        auth.signOut()
                        binding.emailEdit.text.clear()
                        binding.passwordEdit.text.clear()
                        binding.loginBtn.text = "로그인"
                        binding.joinBtn.isVisible = true
                        binding.joinBtn.isEnabled = true
                        binding.emailEdit.isEnabled = true
                        binding.passwordEdit.isEnabled = true

                    }

                }
            }
        }
    }

    private fun initJoinBtn() {
        binding?.let { binding ->

            val email = binding.emailEdit.text.toString()
            val password = binding.passwordEdit.text.toString()

            if (email.isNotEmpty()
                && password.length >= 6
            ) {
                binding.joinBtn.isEnabled = true
                binding.loginBtn.isEnabled = true

                binding.joinBtn.setOnClickListener {
                    auth.createUserWithEmailAndPassword(email, password)
                    Snackbar.make(binding.root, "회원가입 성공", Snackbar.LENGTH_LONG).show()
                }
            }


        }
    }

}