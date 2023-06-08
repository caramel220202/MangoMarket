package com.example.fastcampusdeepen.SecondHandDeal.chatList

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fastcampusdeepen.SecondHandDeal.home.ArticleKey.Companion.DB_CHATS
import com.example.fastcampusdeepen.databinding.ActivityChattingRoomBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ChattingRoomActivity : AppCompatActivity() {
    private val binding: ActivityChattingRoomBinding by lazy {
        ActivityChattingRoomBinding.inflate(layoutInflater)
    }
    private val chattingRoomAdapter: ChattingRoomAdapter by lazy {
        com.example.fastcampusdeepen.SecondHandDeal.chatList.ChattingRoomAdapter()
    }
    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }
    private val chattingList = mutableListOf<ChattingModel>()
    private lateinit var chatDB: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val chatKey = intent.getLongExtra("chatKey", -1)
        chatDB = Firebase.database.reference.child(DB_CHATS).child("$chatKey")
        binding.chattingRoomRecyclerview.adapter = chattingRoomAdapter
        binding.chattingRoomRecyclerview.layoutManager = LinearLayoutManager(this)
        chattingList.clear()
        binding.sendBtn.setOnClickListener {
            if (auth.currentUser != null) {
                val chattingModel = ChattingModel(
                    chatSendId = auth.currentUser!!.uid,
                    message = binding.chatSendMessageEditText.text.toString(),
                    time = System.currentTimeMillis()
                )
                chatDB.push().setValue(chattingModel)
                binding.chatSendMessageEditText.text.clear()


            }

        }
        binding.backBtn.setOnClickListener {
            finish()
        }
        chatDB.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chattingModel = snapshot.getValue(ChattingModel::class.java)
                chattingModel ?: return
                chattingList.add(chattingModel)
                chattingRoomAdapter.submitList(chattingList)
                chattingRoomAdapter.notifyDataSetChanged()
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onChildRemoved(snapshot: DataSnapshot) {}

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}