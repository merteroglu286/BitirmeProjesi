package com.example.bitirmeprojesi.Dashboard

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.example.bitirmeprojesi.Activities.NoticesActivity
import com.example.bitirmeprojesi.Constants.AppConstants
import com.example.bitirmeprojesi.R
import com.example.bitirmeprojesi.ViewModels.BlackListViewModel
import com.example.bitirmeprojesi.ViewModels.NoticeViewModel
import com.example.bitirmeprojesi.ViewModels.ProfileViewModel
import com.example.bitirmeprojesi.databinding.BildiriDialogBinding
import com.example.bitirmeprojesi.databinding.FragmentMapsBinding

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.bildiri_dialog.*
import kotlin.math.roundToInt

class MapsFragment : Fragment(),OnMapReadyCallback,GoogleMap.OnMarkerClickListener,
    GoogleMap.OnCameraIdleListener{

    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!
    private lateinit var mMap : GoogleMap
    private lateinit var locationManager : LocationManager
    private lateinit var locationListener : LocationListener
    private lateinit var guncelKonum : LatLng
    private lateinit var dialog:  AlertDialog
    private lateinit var dialogBinding : BildiriDialogBinding
    private lateinit var noticeViewModel : NoticeViewModel
    private lateinit var profileViewModels: ProfileViewModel
    private lateinit var blackListViewModel: BlackListViewModel

    private lateinit var username:String
    private lateinit var userImage:String
    private lateinit var userID:String
    private  var noticeImageUri: Uri? = null
    private lateinit var noticeImageUrl:String

    private var storageReference: StorageReference? = null
    private var databaseReference: DatabaseReference? = null

    private var noticeDegree : String = "green"
    private var zoomLevel = 0f
    private var markerSize = 100

    val blackList = ArrayList<String>()
    private val markers = mutableListOf<Marker>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val fab = activity?.findViewById<FloatingActionButton>(R.id.bottomBarFabMap)
        fab!!.isVisible = false
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync {
                googleMap -> mMap = googleMap
                onMapReady(googleMap)

            storageReference = FirebaseStorage.getInstance().reference
            blackListViewModel = ViewModelProviders.of(this@MapsFragment).get(BlackListViewModel::class.java)
            blackListViewModel.getDataFromAPI()
            getBlackList()

            binding.mapSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
                androidx.appcompat.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    if (query != null) {
                        // Arama sonucunu işleme koymak için gereken işlemler burada yazılır
                        val geoCoder = Geocoder(context)
                        val addressList = geoCoder.getFromLocationName(query, 2)
                        if (addressList != null && addressList.isNotEmpty()) {
                            val address = addressList[0]
                            val latLng = LatLng(address.latitude, address.longitude)
                            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 14f)
                            googleMap.animateCamera(cameraUpdate)
                        }
                    }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    /*  // Arama metni değiştirildiğinde yapılacak işlemler burada yazılır
                   if (!newText.isNullOrEmpty()) {
                       // Arama metni boş değilse, arama sonuçlarını gösterin
                       val searchResults = mutableListOf<String>()
                       for (address in addressList) {
                           if (address.getAddressLine(0).contains(newText, ignoreCase = true)) {
                               searchResults.add(address.getAddressLine(0))
                           }
                       }
                       showSearchResults(searchResults)
                   } else {
                       // Arama metni boş veya null ise, tüm arama sonuçlarını gösterin
                       val searchResults = mutableListOf<String>()
                       for (address in addressList) {
                           searchResults.add(address.getAddressLine(0))
                       }
                       showSearchResults(searchResults)
                   }


                   return true

                  */
                    return false
                }
            })

        }


        binding.btnToNoticesFragment.setOnClickListener {
            val intent = Intent(context, NoticesActivity::class.java)
            startActivity(intent)
        }

    }


    override fun onMapReady(p0: GoogleMap) {
        mMap = p0

        try {
            var success = mMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(requireContext(),
                    R.raw.map_style)
            )
            if (!success){
                Log.e("TagMaps","Map stili yuklenirken hata olustu")
            }
        }catch (e: Exception){
            Log.e("TagMaps","Duzgun yuklendi")
        }

        userLiveData()
        noticeViewModel = ViewModelProviders.of(this@MapsFragment).get(NoticeViewModel::class.java)
        noticeViewModel.getDataFromFirebase()

        locationLiveData(mMap)

        locationManager = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationListener = object : LocationListener{
            @SuppressLint("UseCompatLoadingForDrawables")
            override fun onLocationChanged(konum: Location) {
                // lokasyon,konum degisince yapilacak islemer

                guncelKonum = LatLng(konum.latitude,konum.longitude)

                binding.fabAddLocation.setOnClickListener{

                    Toast.makeText(context,"tıklandı",Toast.LENGTH_SHORT).show()
                    val alertDialog = AlertDialog.Builder(context)
                    dialogBinding = BildiriDialogBinding.inflate(layoutInflater)
                    alertDialog.setView(dialogBinding.root)
                    dialog = alertDialog.create()
                    dialog.show()
                    noticeDegree = "green"
                    dialog.radioGroup.setOnCheckedChangeListener{ group , checkedId ->
                        if (checkedId == R.id.btnGreen){
                            dialogBinding.dialogContext.background = resources.getDrawable(R.drawable.border_dialog_green)
                            noticeDegree = "green"

                        }
                        if (checkedId == R.id.btnYellow){
                            dialogBinding.dialogContext.background = resources.getDrawable(R.drawable.border_dialog_yellow)
                            noticeDegree = "yellow"
                        }
                        if (checkedId == R.id.btnRed){
                            dialogBinding.dialogContext.background = resources.getDrawable(R.drawable.border_dialog_red)
                            noticeDegree = "red"
                        }

                    }

                    dialogBinding.messageEdittext.addTextChangedListener(object :
                        TextWatcher {
                        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                            val inputText = s.toString().toLowerCase() // Girilen metni küçük harflere çevir
                            val words = inputText.split(" ")
                            val containsBlacklistWord = words.any { word -> blackList.contains(word) }// Girilen metin siyah listedeki kelimelerden herhangi birini içeriyor mu?

                            if (containsBlacklistWord) {
                                dialogBinding.alertTxt.text = "Mesajınız uygunsuz ifadeler içermektedir."
                                dialogBinding.messageEdittext.background = resources.getDrawable(R.drawable.edittext_alert_border)
                                dialogBinding.alertTxt.visibility = View.VISIBLE
                            }else{
                                dialogBinding.alertTxt.text = "Lütfen bir mesaj giriniz."
                                dialogBinding.messageEdittext.background = resources.getDrawable(R.drawable.bildiri_edittext_bg)
                                dialogBinding.alertTxt.visibility = View.GONE
                            }
                        }

                        override fun afterTextChanged(s: Editable?) {}
                    })

                    dialogBinding.imgPickImage.setOnClickListener{
                        if (checkStoragePermission())
                            pickImage()
                        else storageRequestPermission()
                    }



                    dialogBinding.bildiriOlusturBtn.setOnClickListener {
                        if (dialogBinding.messageEdittext.text.toString().isEmpty()){
                            dialogBinding.messageEdittext.background = resources.getDrawable(R.drawable.edittext_alert_border)
                            dialogBinding.alertTxt.visibility = View.VISIBLE
                        }else{
                            /*
                            val words = dialogBinding.messageEdittext.text.split(" ")
                            if (words.any { word -> blackList.contains(word) }){
                                dialogBinding.alertTxt.text = "Mesajınız uygunsuz ifadeler içermektedir."
                                dialogBinding.messageEdittext.background = resources.getDrawable(R.drawable.edittext_alert_border)
                                dialogBinding.alertTxt.visibility = View.VISIBLE
                            }else{

                                dialogBinding.alertTxt.text = "Lütfen bir mesaj giriniz."

                            }

                             */

                            val timestamp = System.currentTimeMillis().toString()

                            if (noticeImageUri != null){
                                storageReference!!.child(userID + "/SharedPhotos/${timestamp}").putFile(noticeImageUri!!)
                                    .addOnSuccessListener {
                                        val task = it.storage.downloadUrl
                                        task.addOnCompleteListener { uri ->
                                            noticeImageUrl = uri.result.toString()
                                            val map = mapOf(
                                                "username" to username,
                                                "userID" to userID,
                                                "latitude" to konum.latitude.toString(),
                                                "longitude" to konum.longitude.toString(),
                                                "noticeID" to userID+timestamp,
                                                "noticeMessage" to dialogBinding.messageEdittext.text.toString(),
                                                "userImage" to userImage,
                                                "noticeDegree" to noticeDegree,
                                                "noticeTime" to timestamp,
                                                "noticeImage" to noticeImageUrl,
                                            )
                                            FirebaseDatabase.getInstance().getReference("Notices").push().setValue(map)
                                        }
                                    }
                            }else{
                                val map = mapOf(
                                    "username" to username,
                                    "userID" to userID,
                                    "latitude" to konum.latitude.toString(),
                                    "longitude" to konum.longitude.toString(),
                                    "noticeID" to userID+timestamp,
                                    "noticeMessage" to dialogBinding.messageEdittext.text.toString(),
                                    "userImage" to userImage,
                                    "noticeDegree" to noticeDegree,
                                    "noticeTime" to timestamp
                                )

                                FirebaseDatabase.getInstance().getReference("Notices").push().setValue(map)
                            }


                            dialog.dismiss()

                        }
                    }
                }




/*
                binding.fabAddLocation.setOnClickListener {

                    val map = mapOf(
                        "latitude" to konum.latitude.toString(),
                        "longitude" to konum.longitude.toString(),
                        "uid" to FirebaseAuth.getInstance().uid!!.toString(),
                    )
                    FirebaseDatabase.getInstance().getReference("Location")!!.child(FirebaseAuth.getInstance().uid!!.toString(),).updateChildren(map)
                    //timeLocation.start()
                }


 */

                var baslangicKonum = mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(guncelKonum,15f))

                baslangicKonum.apply {
                    baslangicKonum = mMap.moveCamera(CameraUpdateFactory.zoomTo(15f))
                }


                mMap.setOnMarkerClickListener(this@MapsFragment)

                //mMap.uiSettings.isZoomGesturesEnabled = false

            }

            override fun onProviderDisabled(@NonNull provider: String) {

            }

            override fun onProviderEnabled(@NonNull provider: String) {

            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                super.onStatusChanged(provider, status, extras)
            }

        }

        // set listeners for camera movements
        mMap.setOnCameraIdleListener(this)

        if (ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                AppConstants.LOCATION_PERMISSION)
        }else{
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,1,1f,locationListener)
        }

    }

    private fun updateCounter(zoom: Int) {
        markerSize = when (zoom) {
            1 -> 1
            2 -> 1
            3 -> 1
            4 -> 30
            5 -> 35
            6 -> 40
            7 -> 45
            8 -> 50
            9 -> 55
            10 -> 60
            11 -> 65
            12 -> 70
            13 -> 75
            14 -> 80
            15 -> 85
            16 -> 90
            17 -> 95
            else -> 100
        }
/*
        binding.sayac.text = when (zoom) {
            1 -> "1"
            2 -> "2"
            3 -> "3"
            4 -> "4"
            5 -> "5"
            6 -> "6"
            7 -> "7"
            8 -> "8"
            9 -> "9"
            10 -> "10"
            11 -> "11"
            12 -> "12"
            13 -> "13"
            14 -> "14"
            15 -> "15"
            16 -> "16"
            17 -> "17"
            else -> "0"
        }

 */
    }

    fun locationLiveData(mMap : GoogleMap){
        noticeViewModel.noticesLiveData.observe(viewLifecycleOwner, Observer { notices ->
            notices?.let{
                if (notices.isNotEmpty()){
                    for (notice in notices){
                        var markerBitmap: Bitmap
                        // var color = BitmapDescriptorFactory.HUE_GREEN
                        if (notice.noticeDegree == "green"){
                           // color = BitmapDescriptorFactory.HUE_GREEN
                            markerBitmap = BitmapFactory.decodeResource(resources, R.drawable.marker_green)
                            val resizedMarkerBitmap = Bitmap.createScaledBitmap(markerBitmap, markerSize, markerSize, false)
                            val markerOptions = MarkerOptions()
                                .position(LatLng(ParseDouble(notice.latitude),ParseDouble(notice.longitude)))
                                .title(notice.username)
                                .icon(BitmapDescriptorFactory.fromBitmap(resizedMarkerBitmap))
                            val marker = mMap.addMarker(markerOptions)

                            marker?.tag = "green"

                            marker?.let { it1 -> markers.add(it1) }
                        }
                        if (notice.noticeDegree == "yellow"){
                           // color = BitmapDescriptorFactory.HUE_YELLOW
                            markerBitmap = BitmapFactory.decodeResource(resources, R.drawable.marker_yellow)
                            val resizedMarkerBitmap = Bitmap.createScaledBitmap(markerBitmap, markerSize, markerSize, false)
                            val markerOptions = MarkerOptions()
                                .position(LatLng(ParseDouble(notice.latitude),ParseDouble(notice.longitude)))
                                .title(notice.username)
                                .icon(BitmapDescriptorFactory.fromBitmap(resizedMarkerBitmap))
                            val marker = mMap.addMarker(markerOptions)

                            marker?.tag = "yellow"

                            marker?.let { it1 -> markers.add(it1) }
                        }
                        if (notice.noticeDegree == "red"){
                           // color = BitmapDescriptorFactory.HUE_RED
                            markerBitmap = BitmapFactory.decodeResource(resources, R.drawable.marker_red)
                            val resizedMarkerBitmap = Bitmap.createScaledBitmap(markerBitmap, markerSize, markerSize, false)
                            val markerOptions = MarkerOptions()
                                .position(LatLng(ParseDouble(notice.latitude),ParseDouble(notice.longitude)))
                                .title(notice.username)
                                .icon(BitmapDescriptorFactory.fromBitmap(resizedMarkerBitmap))
                            val marker = mMap.addMarker(markerOptions)

                            marker?.tag = "red"

                            marker?.let { it1 -> markers.add(it1) }
                        }

                        //updateCounter(mMap.cameraPosition.zoom.roundToInt())


                    }

                }

            }
        })

        noticeViewModel.noticeError.observe(viewLifecycleOwner, Observer { error->

            error?.let{
                if (it){
                    Toast.makeText(context,"hata çıktı",Toast.LENGTH_SHORT).show()
                }
            }

        })
    }

    fun userLiveData(){
        profileViewModels = ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity()!!.application).create(
            ProfileViewModel::class.java)

        profileViewModels.getUser().observe(viewLifecycleOwner, Observer {  userModel ->
            username = userModel.username
            userImage = userModel.image
            userID = userModel.uid
        })
    }

    fun getBlackList(){
        blackListViewModel.blackList.observe(viewLifecycleOwner, Observer { list->

            list?.let{
                blackList.addAll(list)
                /*
                for (list in blackList){
                    Log.i("blacklist",list)
                }
                 */
            }

        })
    }
    override fun onMarkerClick(p0: Marker): Boolean {
        return false
    }

    fun ParseDouble(strNumber: String?): Double {
        return if (strNumber != null && strNumber.length > 0) {
            try {
                return strNumber.toDouble()
            } catch (e: java.lang.Exception) {
                (-1).toDouble() // or some value to mark this field is wrong. or make a function validates field first ...
            }
        } else {
            return 0.0
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
                else Toast.makeText(requireContext(), "Storage permission denied", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                val result = CropImage.getActivityResult(data)
                if (resultCode == Activity.RESULT_OK) {
                    noticeImageUri = result.uri
                    dialogBinding.noticeImage.setImageURI(noticeImageUri)
                    dialogBinding.noticeImage.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun pickImage() {
        context?.let {
            CropImage.activity()
                .setCropMenuCropButtonTitle(resources.getString(R.string.crop_image_save_ok))
                .setCropShape(CropImageView.CropShape.RECTANGLE)
                .setAspectRatio(4, 3)
                .start(it, this)
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        val fab = activity?.findViewById<FloatingActionButton>(R.id.bottomBarFabMap)
        fab!!.isVisible = true
    }

    override fun onCameraIdle() {
        updateCounter(mMap.cameraPosition.zoom.roundToInt())
        for (marker in markers) {
            if (marker.tag == "green"){
                val markerBitmap = BitmapFactory.decodeResource(resources, R.drawable.marker_green)
                val resizedMarkerBitmap = Bitmap.createScaledBitmap(markerBitmap, markerSize, markerSize, false)
                marker.setIcon(BitmapDescriptorFactory.fromBitmap(resizedMarkerBitmap))
            }
            else if(marker.tag == "yellow"){
                val markerBitmap = BitmapFactory.decodeResource(resources, R.drawable.marker_yellow)
                val resizedMarkerBitmap = Bitmap.createScaledBitmap(markerBitmap, markerSize, markerSize, false)
                marker.setIcon(BitmapDescriptorFactory.fromBitmap(resizedMarkerBitmap))
            }else{
                val markerBitmap = BitmapFactory.decodeResource(resources, R.drawable.marker_red)
                val resizedMarkerBitmap = Bitmap.createScaledBitmap(markerBitmap, markerSize, markerSize, false)
                marker.setIcon(BitmapDescriptorFactory.fromBitmap(resizedMarkerBitmap))
            }

        }
    }
}