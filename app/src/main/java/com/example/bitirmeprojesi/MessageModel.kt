package com.example.bitirmeprojesi

class MessageModel (
    var senderId: String = "",
    var receiverId: String = "",
    var message: String = "",
    var date: String = System.currentTimeMillis().toString(),
    var type : String = ""
)