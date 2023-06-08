package com.example.fastcampusdeepen.SecondHandDeal.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fastcampusdeepen.R
import com.example.fastcampusdeepen.SecondHandDeal.ArticleAddActivity
import com.example.fastcampusdeepen.SecondHandDeal.SecondHandDeal
import com.example.fastcampusdeepen.SecondHandDeal.chatList.ChatListModel
import com.example.fastcampusdeepen.SecondHandDeal.home.ArticleKey.Companion.CHILD_CHAT
import com.example.fastcampusdeepen.SecondHandDeal.home.ArticleKey.Companion.DB_ARTICLES
import com.example.fastcampusdeepen.SecondHandDeal.home.ArticleKey.Companion.DB_USERS
import com.example.fastcampusdeepen.databinding.FragmentHomeBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment(R.layout.fragment_home) {

    private var binding: FragmentHomeBinding? = null
    private lateinit var articleDB: DatabaseReference
    private lateinit var userDB: DatabaseReference
    private lateinit var adapter: ArticleAdapter
    private val articleList = mutableListOf<ArticleModel>()
    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }
    private val listener = object : ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

            val articleModel = snapshot.getValue(ArticleModel::class.java)
            articleModel ?: return
            articleList.add(articleModel)
            adapter.submitList(articleList.reversed())
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

        override fun onChildRemoved(snapshot: DataSnapshot) {}

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

        override fun onCancelled(error: DatabaseError) {}
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val homeFragmentBinding = FragmentHomeBinding.bind(view)
        binding = homeFragmentBinding
        articleList.clear()
        articleDB = Firebase.database.reference.child(DB_ARTICLES)
        userDB = Firebase.database.reference.child(DB_USERS)

        adapter = ArticleAdapter(onItemClicked = { articleModel ->
            if (auth.currentUser != null) {

                if (auth.currentUser?.uid != articleModel.sellerId) {
                    val chatRoom = ChatListModel(
                        buyerId = auth.currentUser?.uid,
                        sellerId = articleModel.sellerId,
                        itemTile = articleModel.title,
                        key = System.currentTimeMillis()
                    )
                    userDB.child(auth?.currentUser?.uid!!)
                        .child(CHILD_CHAT)
                        .push()
                        .setValue(chatRoom)

                    userDB.child(articleModel.sellerId)
                        .child(CHILD_CHAT)
                        .push()
                        .setValue(chatRoom)

                    //todo 되는가 확인
                    Snackbar.make(homeFragmentBinding.root, "채팅방이 생성되었습니다. 채팅탭으로 이동해주세요.", Snackbar.LENGTH_LONG).show()

                } else {
                    Snackbar.make(homeFragmentBinding.root, "내가 올린 아이템입니다.", Snackbar.LENGTH_LONG).show()
                }
            } else {
                Snackbar.make(homeFragmentBinding.root, "로그인 후 사용해주세요.", Snackbar.LENGTH_LONG).show()
            }

        })

        homeFragmentBinding.articleRecyclerview.layoutManager = LinearLayoutManager(context)
        homeFragmentBinding.articleRecyclerview.adapter = adapter


        articleDB.addChildEventListener(listener)

        homeFragmentBinding.addFloatingBtn.setOnClickListener {

            if (auth.currentUser == null) {
                Snackbar.make(homeFragmentBinding.root, "로그인 후 사용해주세요.", Snackbar.LENGTH_LONG)
                    .show()
                return@setOnClickListener
            } else {
                startActivity(Intent(requireContext(), ArticleAddActivity::class.java))
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        articleDB.removeEventListener(listener)
    }

    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()
    }
}