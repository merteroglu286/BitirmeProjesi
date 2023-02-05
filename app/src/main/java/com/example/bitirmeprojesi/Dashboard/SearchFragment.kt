package com.example.bitirmeprojesi.Dashboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bitirmeprojesi.Adapters.SearchHistoryAdapter
import com.example.bitirmeprojesi.SearchHistoryModel
import com.example.bitirmeprojesi.UserModel
import com.example.bitirmeprojesi.databinding.FragmentSearchBinding
import com.example.socialmap.Adapter.ContactAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class SearchFragment : Fragment() {
/*
    private lateinit var binding: FragmentSearchBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var userRecyclerView: RecyclerView
    private lateinit var userArrayList: ArrayList<UserModel>
    private var contactAdapter: ContactAdapter? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSearchBinding.inflate(inflater,container,false)
        userRecyclerView = binding.recyclerViewContact
        userRecyclerView.layoutManager = LinearLayoutManager(context)
        userRecyclerView.setHasFixedSize(true)
        userArrayList = arrayListOf<UserModel>()
        getUserData()

        binding.contactSearchView.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                if (contactAdapter != null) {
                    contactAdapter!!.filter.filter(newText)
                    binding.recyclerViewContact.isVisible = newText != ""

                }
                else {
                    Toast.makeText(context, "calısmıyor", Toast.LENGTH_SHORT).show()
                }
                return false
            }

        })


        return binding.root
    }

    private fun getUserData() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Users")

        databaseReference.addValueEventListener(object : ValueEventListener {


            override fun onDataChange(snapshot: DataSnapshot) {

                if(snapshot.exists()){
                    userArrayList.clear()

                    for(userSnapShot in snapshot.children){

                        val user = userSnapShot.getValue(UserModel::class.java)
                        userArrayList.add(user!!)

                    }
                    contactAdapter = ContactAdapter(userArrayList,binding.contactSearchView)
                    userRecyclerView.adapter = contactAdapter
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

 */

    private lateinit var binding: FragmentSearchBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var userRecyclerView: RecyclerView
    private lateinit var searchRecyclerView: RecyclerView
    private lateinit var userArrayList: ArrayList<UserModel>
    private lateinit var searchArrayList: ArrayList<SearchHistoryModel>
    private var contactAdapter: ContactAdapter? = null
    private var searchAdapter: SearchHistoryAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentSearchBinding.inflate(inflater,container,false)
        userRecyclerView = binding.recyclerViewContact
        userRecyclerView.layoutManager = LinearLayoutManager(context)
        userRecyclerView.setHasFixedSize(true)

        searchRecyclerView = binding.recyclerViewSearchHistory
        searchRecyclerView.layoutManager = LinearLayoutManager(context)
        searchRecyclerView.setHasFixedSize(true)

        userArrayList = arrayListOf<UserModel>()
        searchArrayList = arrayListOf<SearchHistoryModel>()
        getUserData()
        getUserSearchHistory()


        //binding.contactSearchView.setOnQueryTextFocusChangeListener(filterTextWatcher)

        binding.contactSearchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                if (contactAdapter != null) {
                    contactAdapter!!.filter.filter(newText)
                    binding.recyclerViewContact.isVisible = newText != ""
                    if (newText == ""){
                        binding.recyclerViewSearchHistory.visibility = View.VISIBLE
                        binding.recyclerViewContact.visibility = View.GONE

                    }else{
                        binding.recyclerViewSearchHistory.visibility = View.GONE
                        binding.recyclerViewContact.visibility = View.VISIBLE
                    }

                }
                else {
                    Toast.makeText(context, "calısmıyor", Toast.LENGTH_SHORT).show()
                }
                return false
            }

        })
        return binding.root
    }

    private fun getUserData() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Users")

        databaseReference.addValueEventListener(object : ValueEventListener{


            override fun onDataChange(snapshot: DataSnapshot) {
                userArrayList.clear()
                if(snapshot.exists()){
                    for(userSnapShot in snapshot.children){

                        val user = userSnapShot.getValue(UserModel::class.java)
                        userArrayList.add(user!!)

                    }
                    contactAdapter = ContactAdapter(userArrayList,binding.contactSearchView)
                    userRecyclerView.adapter = contactAdapter
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun getUserSearchHistory(){
        databaseReference = FirebaseDatabase.getInstance().getReference("SearchHistory").child(FirebaseAuth.getInstance().currentUser!!.uid)

        databaseReference.addValueEventListener(object : ValueEventListener{


            override fun onDataChange(snapshot: DataSnapshot) {
                searchArrayList.clear()
                if(snapshot.exists()){
                    for(userSnapShot in snapshot.children){

                        val user = userSnapShot.getValue(SearchHistoryModel::class.java)
                        searchArrayList.add(user!!)

                    }
                    if(searchArrayList.isNotEmpty()){
                        binding.recyclerViewSearchHistory.visibility = View.VISIBLE
                    }
                    searchAdapter = SearchHistoryAdapter(searchArrayList)
                    searchRecyclerView.adapter = searchAdapter
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}