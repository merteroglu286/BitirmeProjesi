package com.example.bitirmeprojesi.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.bitirmeprojesi.Repository.AppRepo
import com.example.bitirmeprojesi.UserModel

class ProfileViewModel:ViewModel() {
    private var appRepo = AppRepo.StaticFun.getInstance()

    fun getUser(): LiveData<UserModel> {
        return appRepo.getUser()
    }

    fun getHisUser(uid:String): LiveData<UserModel> {
        return appRepo.getHisUser(uid)
    }

    fun updateStatus(status: String) {
        appRepo.updateStatus(status)
    }

    fun updateName(userName: String?) {
        appRepo.updateName(userName!!)
    }

    fun updateImage(imagePath: String) {
        appRepo.updateImage(imagePath)
    }

}