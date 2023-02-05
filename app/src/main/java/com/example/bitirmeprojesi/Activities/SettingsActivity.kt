package com.example.bitirmeprojesi.Activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.bitirmeprojesi.Constants.AppConstants
import com.example.bitirmeprojesi.Permissions.AppPermission
import com.example.bitirmeprojesi.R
import com.example.bitirmeprojesi.ViewModels.ProfileViewModel
import com.example.bitirmeprojesi.databinding.ActivitySettingsBinding
import com.example.bitirmeprojesi.databinding.ActivityUserInfoBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import de.hdodenhof.circleimageview.CircleImageView

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var profileViewModels: ProfileViewModel
    private lateinit var appPermission: AppPermission

    private var databaseReference: DatabaseReference? = null
    private var firebaseAuth: FirebaseAuth? = null
    private var storageReference: StorageReference? = null

    private lateinit var uid: String
    private var image: Uri? = null
    private lateinit var imageUrl: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appPermission = AppPermission()
        profileViewModels = ViewModelProvider.AndroidViewModelFactory.getInstance(application).create(
            ProfileViewModel::class.java)

        firebaseAuth = FirebaseAuth.getInstance()
        storageReference = FirebaseStorage.getInstance().reference

        databaseReference = FirebaseDatabase.getInstance().getReference("Users")
        profileViewModels.getUser().observe(this, Observer {  userModel ->
            binding.userModel = userModel
        })
        binding.logOut.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        binding.imgPickImage.setOnClickListener{
            /*if(appPermission.isStorageOk(this)){
                pickImage()
            }

             */
            openGallery()
        }



/*
        binding.imgPickImage.setOnClickListener {
            if (checkStoragePermission()){
                pickImage()
                storageReference!!.child(firebaseAuth!!.uid + AppConstants.PATH).putFile(image!!)
                    .addOnSuccessListener {
                        val task = it.storage.downloadUrl
                        task.addOnCompleteListener { uri ->
                            imageUrl = uri.result.toString()
                            uid = firebaseAuth!!.uid!!.toString()
                            val map = mapOf(
                                "image" to imageUrl,
                            )
                            databaseReference!!.child(firebaseAuth!!.uid!!).updateChildren(map)
                        }
                    }
            }
            else storageRequestPermission()
        }

 */

        binding.profileDuzenleKaydet.setOnClickListener {
            var kullaniciAdi: String = binding.editUserName.text.toString()
            var cinsiyet: String = binding.editUserGender.text.toString()
            var dogumTarihi: String = binding.editUserBirthday.text.toString()

            val map = mapOf(
                "username" to kullaniciAdi,
                "gender" to cinsiyet,
                "birthday" to dogumTarihi,
            )
            databaseReference!!.child(firebaseAuth!!.uid!!).updateChildren(map)
            finish()
        }

    }


    private fun checkStoragePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun storageRequestPermission() = ActivityCompat.requestPermissions(
        this,
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
                else Toast.makeText(this, "Storage permission denied", Toast.LENGTH_SHORT).show()
        }
    }

/*
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


 */
    private fun pickImage() {
        this.let {
            CropImage.activity()
                .setCropShape(CropImageView.CropShape.OVAL)
                .start(it)
        }
    }

    private val REQUEST_CODE_GALLERY = 100
    private lateinit var imageView: CircleImageView

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE_GALLERY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_GALLERY) {
            val imageUri = data?.data
            binding.imgUser.setImageURI(imageUri)
            storageReference?.child(firebaseAuth!!.uid + AppConstants.PATH)
                ?.putFile(imageUri!!)
                ?.addOnSuccessListener { taskSnapshot ->
                    taskSnapshot.storage.downloadUrl.addOnSuccessListener {
                        imageUrl = it.toString()
                        uid = firebaseAuth?.uid!!
                        val map = mapOf("image" to imageUrl)
                        databaseReference?.child(uid)?.updateChildren(map)
                    }
                }
        }
    }


}