package com.example.fastcampusdeepen.SecondHandDeal.chatList

import android.icu.text.Transliterator
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.fastcampusdeepen.databinding.ChatItemMychatBinding
import com.example.fastcampusdeepen.databinding.ChatItemOtherchatBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*


class ChattingRoomAdapter() : ListAdapter<ChattingModel, RecyclerView.ViewHolder>(diffUtil) {
    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }

    inner class MyChatViewHolder(private val binding: ChatItemMychatBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(chat: ChattingModel) {
            binding.userId.text = chat.chatSendId
            binding.userChatText.text = chat.message
            isRecyclable.not()
            val dateFormat = SimpleDateFormat("HH:mm", Locale.KOREA)
            val date = Date(chat.time)
            binding.sendTime.text = dateFormat.format(date)
        }
    }

    inner class OtherChatViewHolder(private val binding: ChatItemOtherchatBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(chat: ChattingModel) {
            binding.userChatText.text = chat.message
            binding.userId.text = chat.chatSendId
            isRecyclable.not()
            val dateFormat = SimpleDateFormat("HH:mm", Locale.KOREA)
            val date = Date(chat.time)
            binding.sendTime.text = dateFormat.format(date)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (auth.currentUser?.uid != currentList[position].chatSendId) 2 else 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            1 ->  {
                return MyChatViewHolder(
                    ChatItemMychatBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            else ->  {
                 return OtherChatViewHolder(
                    ChatItemOtherchatBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        Log.d("typee",getItemViewType(position).toString())
        when (getItemViewType(position)) {
            1 -> (holder as MyChatViewHolder).bind(currentList[position])
            else -> {
                (holder as OtherChatViewHolder).bind(currentList[position])
            }
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<ChattingModel>() {
            override fun areItemsTheSame(oldItem: ChattingModel, newItem: ChattingModel): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ChattingModel,
                newItem: ChattingModel
            ): Boolean {
                return oldItem.message == newItem.message
            }
        }
    }
}