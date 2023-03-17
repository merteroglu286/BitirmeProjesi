package com.example.bitirmeprojesi

data class FollowRequestModel(
    val senderId : String = "",
    val receiverId : String = "",
    val senderImage : String = "",
    val senderName : String = "",
    val date : String = "",
    var onaylandiMi : Boolean = false
)
