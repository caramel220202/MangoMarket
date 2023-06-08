package com.example.fastcampusdeepen.SecondHandDeal.chatList

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fastcampusdeepen.R
import com.example.fastcampusdeepen.SecondHandDeal.home.ArticleKey.Companion.CHILD_CHAT
import com.example.fastcampusdeepen.SecondHandDeal.home.ArticleKey.Companion.DB_USERS
import com.example.fastcampusdeepen.databinding.FragmentChatlistBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ChatListFragment : Fragment(R.layout.fragment_chatlist) {
    private var binding: FragmentChatlistBinding? = null
    private lateinit var chatListAdapter: ChatListAdapter
    private val chatRoomList = mutableListOf<ChatListModel>()
    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val chatListBinding = FragmentChatlistBinding.bind(view)
        binding = chatListBinding
        chatListAdapter = ChatListAdapter(onItemClicked = { chatListModel ->
            val intent = Intent(requireContext(), ChattingRoomActivity::class.java)
            intent.putExtra("chatKey", chatListModel.key)
            startActivity(intent)
        })

        chatRoomList.clear()

        chatListBinding.chatListRecyclerview.adapter = chatListAdapter
        chatListBinding.chatListRecyclerview.layoutManager = LinearLayoutManager(context)

        if (auth.currentUser == null) {
            return
        }

        val chatDB = Firebase.database.reference.child(DB_USERS).child(auth!!.currentUser!!.uid)
            .child(CHILD_CHAT)

        chatDB.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    val model = it.getValue(ChatListModel::class.java)
                    model ?: return
                    chatRoomList.add(model)
                    chatListAdapter.submitList(chatRoomList.reversed())
                    chatListAdapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    override fun onResume() {
        super.onResume()
        chatListAdapter.notifyDataSetChanged()
    }
}