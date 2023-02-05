package com.example.bitirmeprojesi.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.example.bitirmeprojesi.MessagingActivity
import com.example.bitirmeprojesi.R
import com.example.bitirmeprojesi.UserModel
import com.example.bitirmeprojesi.ViewModels.ProfileViewModel
import com.example.bitirmeprojesi.databinding.ActivityUserInfoBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class UserInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserInfoBinding
    private lateinit var profileViewModels: ProfileViewModel
    private var hisId: String? = null
    private var hisImage: String? = null
    private var hisName: String? = null
    private var hisToken: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        profileViewModels = ViewModelProvider.AndroidViewModelFactory.getInstance(application).create(
            ProfileViewModel::class.java)


        hisId = intent.getStringExtra("hisId")
        hisImage = intent.getStringExtra("hisImage")
        hisName = intent.getStringExtra("hisName")
        getUserData(hisId)

        Glide.with(this).load(hisImage).into(binding.imgProfile)
        binding.userName.text = hisName.toString()


        binding.btnMessage.setOnClickListener {
            val intent = Intent(it.context, MessagingActivity::class.java)
            intent.putExtra("hisId",hisId.toString())
            intent.putExtra("hisImage",hisImage.toString())
            intent.putExtra("hisName",hisName.toString())
            it.context.startActivity(intent)
        }

        binding.imgProfile.setOnClickListener {
            val intent = Intent(it.context, FullscreenPhotoActivity::class.java)
            intent.putExtra("img",hisImage)
            it.context.startActivity(intent)
        }

        binding.btnFollowed.setOnClickListener {
            binding.linearButtons.visibility = View.VISIBLE
            binding.btnFollowed.visibility = View.INVISIBLE

        }


    }

    private fun getUserData(userId: String?) {

        val databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId!!)
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val userModel = snapshot.getValue(UserModel::class.java)
                    binding.userName.text = userModel!!.username
                    hisToken = userModel.token
                    //Picasso.get().load(userModel!!.image).into(activityUserInfoBinding.imgProfile)
                    Glide.with(applicationContext).load(userModel!!.image).into(binding.imgProfile)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

}