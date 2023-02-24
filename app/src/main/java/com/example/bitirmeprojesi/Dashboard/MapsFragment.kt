package com.example.bitirmeprojesi.Dashboard

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.bitirmeprojesi.Constants.AppConstants
import com.example.bitirmeprojesi.R
import com.example.bitirmeprojesi.databinding.BildiriDialogBinding
import com.example.bitirmeprojesi.databinding.FragmentMapsBinding

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_dashboard.*

class MapsFragment : Fragment(),OnMapReadyCallback,GoogleMap.OnMarkerClickListener {

    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!
    private lateinit var mMap : GoogleMap
    private lateinit var locationManager : LocationManager
    private lateinit var locationListener : LocationListener
    private lateinit var guncelKonum : LatLng
    private lateinit var dialog:  AlertDialog
    private lateinit var dialogBinding : BildiriDialogBinding
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
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        val fab = activity?.findViewById<FloatingActionButton>(R.id.bottomBarFabMap)
        fab!!.isVisible = true
    }

    override fun onMapReady(p0: GoogleMap) {
        mMap = p0


        locationManager = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationListener = object : LocationListener{
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

                    dialogBinding.bildiriOlusturBtn.setOnClickListener {
                        val map = mapOf(
                            "latitude" to konum.latitude.toString(),
                            "longitude" to konum.longitude.toString(),
                            "uid" to FirebaseAuth.getInstance().uid!!.toString(),
                        )
                        FirebaseDatabase.getInstance().getReference("Location")!!.child(FirebaseAuth.getInstance().uid!!.toString(),).updateChildren(map)
                        dialog.dismiss()
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

        if (ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                AppConstants.LOCATION_PERMISSION)
        }else{
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,1,1f,locationListener)
        }

    }

    override fun onMarkerClick(p0: Marker): Boolean {
        TODO("Not yet implemented")
    }
}