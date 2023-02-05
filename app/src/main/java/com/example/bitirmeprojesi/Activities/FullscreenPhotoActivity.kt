package com.example.bitirmeprojesi.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bumptech.glide.Glide
import com.example.bitirmeprojesi.R
import com.example.bitirmeprojesi.databinding.ActivityFullscreenPhotoBinding

class FullscreenPhotoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFullscreenPhotoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFullscreenPhotoBinding.inflate(layoutInflater)

        val img = intent.getStringExtra("img")
        Log.i("zxc",img.toString())
        Glide.with(this).load(img.toString()).into(binding.photoView)
        setContentView(binding.root)
    }
}