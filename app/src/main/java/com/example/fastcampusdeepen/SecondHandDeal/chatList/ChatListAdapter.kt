package com.example.fastcampusdeepen.SecondHandDeal.chatList

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.fastcampusdeepen.databinding.ChatListItemBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class ChatListAdapter(val onItemClicked: (ChatListModel) -> Unit) :
    ListAdapter<ChatListModel, ChatListAdapter.ViewHolder>(diffUtil) {
    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }

    inner class ViewHolder(private val binding: ChatListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(chatListModel: ChatListModel) {
            binding.title.text = chatListModel.itemTile
            binding.sellerId.text = chatListModel.sellerId
            binding.root.setOnClickListener {
                onItemClicked(chatListModel)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ChatListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<ChatListModel>() {
            override fun areItemsTheSame(oldItem: ChatListModel, newItem: ChatListModel): Boolean {
                return oldItem.sellerId == newItem.sellerId
            }

            override fun areContentsTheSame(
                oldItem: ChatListModel,
                newItem: ChatListModel
            ): Boolean {
                return oldItem.itemTile == newItem.itemTile
            }
        }
    }
}