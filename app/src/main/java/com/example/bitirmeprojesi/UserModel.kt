package com.example.bitirmeprojesi

import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView
data class UserModel(
    var username: String = "",
    var token: String = "",
    val status: String = "",
    val image: String = "",
    var phoneNumber: String = "",
    val uid: String = "",
    val gender: String = "",
    val birthday : String = "",
    val kayitliMi : Boolean = false,
    val followers : String = "", // takipçi
    val following : String = ""  // takip edilen
){


    companion object{
        @JvmStatic
        @BindingAdapter("imageUrl")
        fun loadImage(view: CircleImageView, imageUrl:String?){
            imageUrl?.let {
                Glide.with(view.context).load(imageUrl).into(view)
            }
        }
    }

}

