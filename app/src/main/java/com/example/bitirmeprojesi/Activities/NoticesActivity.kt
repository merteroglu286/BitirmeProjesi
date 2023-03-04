package com.example.bitirmeprojesi.Activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bitirmeprojesi.Adapters.NoticeAdapter
import com.example.bitirmeprojesi.R
import com.example.bitirmeprojesi.ViewModels.NoticeViewModel
import kotlinx.android.synthetic.main.activity_notices.*

class NoticesActivity : AppCompatActivity() {

    private lateinit var noticesViewModel : NoticeViewModel
    private  var noticeAdapter = NoticeAdapter(arrayListOf(),this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notices)
        noticesViewModel = ViewModelProviders.of(this).get(NoticeViewModel::class.java)
        noticesViewModel.getDataFromFirebase()

        noticesList.layoutManager = LinearLayoutManager(this)
        noticesList.adapter = noticeAdapter

        observeLiveData()
    }


    fun observeLiveData(){
        noticesViewModel.noticesLiveData.observe(this, Observer { notices ->
            notices?.let{
                noticeAdapter.updateNoticesList(notices)
            }
        })

        noticesViewModel.noticeError.observe(this, Observer { error->
            error?.let{
                if (it){
                    Toast.makeText(this,"hata çıktı", Toast.LENGTH_SHORT).show()
                }
            }

        })
    }
}