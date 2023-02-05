package com.example.bitirmeprojesi.Dashboard

import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.viewpager2.widget.ViewPager2
import com.example.bitirmeprojesi.R

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_dashboard.*

class MapsFragment : Fragment() {
    private val callback = OnMapReadyCallback {
    }

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
        return inflater.inflate(R.layout.fragment_maps, container, false)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync { googleMap ->
            val sydney = LatLng(41.009900147284725, 28.964107022393915)
            googleMap.addMarker(MarkerOptions().position(sydney).title("Marker in İstanbul"))
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        val fab = activity?.findViewById<FloatingActionButton>(R.id.bottomBarFabMap)
        fab!!.isVisible = true
    }
}