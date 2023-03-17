package com.example.bitirmeprojesi.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bitirmeprojesi.Adapters.FollowRequestsAdapter
import com.example.bitirmeprojesi.Adapters.NoticeAdapter
import com.example.bitirmeprojesi.R
import com.example.bitirmeprojesi.ViewModels.FollowRequestsWiewModel
import com.example.bitirmeprojesi.ViewModels.NoticeViewModel
import kotlinx.android.synthetic.main.activity_follow_requests.*
import kotlinx.android.synthetic.main.activity_notices.*

class FollowRequestsActivity : AppCompatActivity() {

    private lateinit var followRequestsWiewModel : FollowRequestsWiewModel
    private  var adapter = FollowRequestsAdapter(arrayListOf(),this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_follow_requests)
        followRequestsWiewModel = ViewModelProviders.of(this).get(FollowRequestsWiewModel::class.java)
        followRequestsWiewModel.getDataFromFirebase()

        recyclerViewFollowRequests.layoutManager = LinearLayoutManager(this)
        recyclerViewFollowRequests.adapter = adapter

        observeLiveData()
    }


    fun observeLiveData(){
        followRequestsWiewModel.requestsLiveData.observe(this, Observer { requests ->
            requests?.let{
                adapter.updateNoticesList(requests)
            }
        })

        followRequestsWiewModel.requestsError.observe(this, Observer { error->
            error?.let{
                if (it){
                    Toast.makeText(this,"hata çıktı", Toast.LENGTH_SHORT).show()
                }
            }

        })
    }
}