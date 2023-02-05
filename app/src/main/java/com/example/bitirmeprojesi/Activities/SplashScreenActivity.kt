package com.example.bitirmeprojesi.Activities

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.os.Handler
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bitirmeprojesi.databinding.ActivitySplashScreenBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging


class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding
    private var firebaseAuth: FirebaseAuth? = null

    var rotateAnimation: Animation? = null
    var imageView: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()

        imageView = binding.logo

        rotateAnimation()

        Handler().postDelayed({

            if (firebaseAuth?.currentUser == null) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                FirebaseMessaging.getInstance().token
                    .addOnCompleteListener(OnCompleteListener {
                        if (it.isSuccessful) {
                            val token = it.result!!
                            val databaseReference =
                                FirebaseDatabase.getInstance().getReference("Users")
                                    .child(firebaseAuth!!.currentUser!!.uid)
                            val map: MutableMap<String, Any> = HashMap()
                            map["token"] = token
                            databaseReference.updateChildren(map)
                        }
                    })
                val intent = Intent(this, DashboardActivity::class.java)
                startActivity(intent)
                finish()
            }

        }, 1500)
    }

    private fun rotateAnimation() {
        rotateAnimation = AnimationUtils.loadAnimation(this, com.example.bitirmeprojesi.R.anim.logo_anim)
        imageView!!.startAnimation(rotateAnimation)
    }
}