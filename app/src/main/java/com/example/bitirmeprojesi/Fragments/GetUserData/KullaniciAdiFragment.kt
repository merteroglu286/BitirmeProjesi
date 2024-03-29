package com.example.bitirmeprojesi.Fragments.GetUserData

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.navigation.Navigation
import com.example.bitirmeprojesi.R
import com.example.bitirmeprojesi.databinding.FragmentKullaniciAdiBinding
import com.example.bitirmeprojesi.databinding.FragmentKullaniciBilgilendirmeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class KullaniciAdiFragment : Fragment() {

    private var _binding: FragmentKullaniciAdiBinding? = null
    private val binding get() = _binding!!

    private var databaseReference: DatabaseReference? = null
    private var firebaseAuth: FirebaseAuth? = null

    private lateinit var uid: String
    private lateinit var username: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentKullaniciAdiBinding.inflate(inflater, container, false)

        firebaseAuth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference("Users")


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnNext.setOnClickListener{
            uid = firebaseAuth!!.uid!!.toString()
            username = binding.edtName.text.toString()
            val action = KullaniciAdiFragmentDirections.actionKullaniciAdiFragmentToKullaniciDogumTarihiFragment(username)
            Navigation.findNavController(it).navigate(action)
        }
        binding.btnBack.setOnClickListener{
            Navigation.findNavController(it).navigateUp()
        }


    }
}