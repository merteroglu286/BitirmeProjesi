package com.example.bitirmeprojesi.Fragments.GetUserData

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import com.example.bitirmeprojesi.Activities.DashboardActivity
import com.example.bitirmeprojesi.Constants.AppConstants
import com.example.bitirmeprojesi.R
import com.example.bitirmeprojesi.databinding.FragmentKullaniciDogumTarihiBinding
import com.example.bitirmeprojesi.databinding.FragmentKullaniciFotografBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView

class KullaniciFotografFragment : Fragment() {
    private var _binding: FragmentKullaniciFotografBinding? = null
    private val binding get() = _binding!!

    private var databaseReference: DatabaseReference? = null
    private var firebaseAuth: FirebaseAuth? = null
    private var storageReference: StorageReference? = null

    private lateinit var uid: String
    private var image: Uri? = null
    private lateinit var imageUrl: String

    private lateinit var kullaniciAdi : String
    private lateinit var dogumTarihi: String
    private lateinit var cinsiyet : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentKullaniciFotografBinding.inflate(inflater, container, false)

        firebaseAuth = FirebaseAuth.getInstance()
        storageReference = FirebaseStorage.getInstance().reference

        databaseReference = FirebaseDatabase.getInstance().getReference("Users")


        binding.imgPickImage.setOnClickListener {
            if (checkStoragePermission())
                pickImage()
            else storageRequestPermission()
        }

        binding.btnDataDone.setOnClickListener {

            storageReference!!.child(firebaseAuth!!.uid + AppConstants.PATH).putFile(image!!)
                .addOnSuccessListener {
                    val task = it.storage.downloadUrl
                    task.addOnCompleteListener { uri ->
                        imageUrl = uri.result.toString()
                        uid = firebaseAuth!!.uid!!.toString()
                        val map = mapOf(
                            "username" to kullaniciAdi,
                            "gender" to cinsiyet,
                            "birthday" to dogumTarihi,
                            "image" to imageUrl,
                            "uid" to uid,
                            "kayitliMi" to true
                        )
                        databaseReference!!.child(firebaseAuth!!.uid!!).updateChildren(map)

                        activity?.let{it->
                            val intent = Intent (it, DashboardActivity::class.java)
                            it.startActivity(intent)
                        }
                    }
                }
            loading(true)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            kullaniciAdi = KullaniciFotografFragmentArgs.fromBundle(it).kullaniciAdi
            dogumTarihi = KullaniciFotografFragmentArgs.fromBundle(it).dogumTarihi
            cinsiyet = KullaniciFotografFragmentArgs.fromBundle(it).cinsiyet
        }

        binding.btnBack.setOnClickListener{
            Navigation.findNavController(it).navigateUp()
        }
    }

    private fun checkStoragePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun storageRequestPermission() = ActivityCompat.requestPermissions(
        requireActivity(),
        arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ), 1000
    )

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1000 ->
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    pickImage()
                else Toast.makeText(context, "Storage permission denied", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {

            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                val result = CropImage.getActivityResult(data)
                if (resultCode == Activity.RESULT_OK) {
                    image = result.uri
                    binding.imgUser.setImageURI(image)
                }
            }
        }
    }

    private fun pickImage() {
        context?.let {
            CropImage.activity()
                .setCropMenuCropButtonTitle(resources.getString(R.string.crop_image_save_ok))
                .setCropShape(CropImageView.CropShape.OVAL)
                .start(it, this)
        }
    }
    private fun loading(isLoading:Boolean){

        if(isLoading){
            binding.progressBar.visibility = View.VISIBLE
        }else{
            binding.progressBar.visibility = View.GONE
        }
    }
}