package com.example.fastcampusdeepen.SecondHandDeal.chatList

class ChatListModel(
    val buyerId : String?,
    val sellerId : String,
    val itemTile : String,
    val key : Long
) {
    constructor():this("","","",0)
}