package com.example.bitirmeprojesi

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.bitirmeprojesi.ViewModels.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth

class MyApplication : Application(), LifecycleObserver {
    private lateinit var profileViewModels: ProfileViewModel
    private var firebaseAuth: FirebaseAuth? = null

    override fun onCreate() {
        super.onCreate()
        firebaseAuth = FirebaseAuth.getInstance()
        if (firebaseAuth?.currentUser != null){
            profileViewModels = ViewModelProvider.AndroidViewModelFactory.getInstance(this).create(
                ProfileViewModel::class.java)
        }
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onAppForegrounded() {
        // code to be executed when the app is brought to the foreground
        if (firebaseAuth?.currentUser != null){
            profileViewModels.updateStatus("online")
        }

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onAppBackgrounded() {
        // code to be executed when the app is moved to the background
        if (firebaseAuth?.currentUser != null){
            profileViewModels.updateStatus("offline")
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onAppDestroyed() {
        // code to be executed when the app is fully closed
        if (firebaseAuth?.currentUser != null){
            profileViewModels.updateStatus("offline")
        }
    }
}
