package com.example.fastcampusdeepen.SecondHandDeal.chatList

class ChattingModel(
    val chatSendId:String,
    val message:String,
    val time : Long
) {
    constructor():this("","",0)
}