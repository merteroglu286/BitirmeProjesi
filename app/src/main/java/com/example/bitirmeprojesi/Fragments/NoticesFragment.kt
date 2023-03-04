package com.example.bitirmeprojesi.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bitirmeprojesi.Adapters.NoticeAdapter
import com.example.bitirmeprojesi.R
import com.example.bitirmeprojesi.ViewModels.NoticeViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.fragment_notices.*


class NoticesFragment : Fragment() {

    private lateinit var noticesViewModel : NoticeViewModel
    private  var noticeAdapter = NoticeAdapter(arrayListOf(),requireContext())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notices, container, false)
/*
        noticesViewModel = ViewModelProviders.of(this).get(NoticeViewModel::class.java)
        noticesViewModel.getDataFromFirebase()

        noticesList.layoutManager = LinearLayoutManager(context)
        noticesList.adapter = noticeAdapter

        observeLiveData()


 */
    }

    fun observeLiveData(){
        noticesViewModel.noticesLiveData.observe(viewLifecycleOwner, Observer { notices ->
            notices?.let{
                noticeAdapter.updateNoticesList(notices)

            }
        })

        noticesViewModel.noticeError.observe(viewLifecycleOwner, Observer { error->

            error?.let{
                if (it){
                    Toast.makeText(context,"hata çıktı", Toast.LENGTH_SHORT).show()
                }
            }

        })
    }
}