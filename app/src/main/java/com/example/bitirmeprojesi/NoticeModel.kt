package com.example.bitirmeprojesi

data class NoticeModel(
    val username : String?,
    val userImage:String?,
    val userID : String?,
    val latitude: String?,
    val longitude : String?,
    val noticeMessage : String?,
    val noticeImage : String?,
    val noticeDegree : String?
) {
    constructor() : this("","", "", "", "", "", "", "")
}

