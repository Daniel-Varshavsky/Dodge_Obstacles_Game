package com.example.dodge_obstacles_game.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.dodge_obstacles_game.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapFragment : Fragment(), OnMapReadyCallback {

    private var googleMap: GoogleMap? = null
    private var pendingLocation: LatLng? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_map, container, false)

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map_container)
                    as SupportMapFragment

        mapFragment.getMapAsync(this)

        return view
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap?.uiSettings?.isZoomControlsEnabled = true

        // If zoom() was called before map was ready
        pendingLocation?.let {
            showLocation(it)
            pendingLocation = null
        }
    }

    /**
     * Called from HighScoreFragment
     */
    fun zoom(lat: Double, lon: Double) {
        val location = LatLng(lat, lon)

        if (googleMap == null) {
            pendingLocation = location
        } else {
            showLocation(location)
        }
    }

    private fun showLocation(location: LatLng) {
        googleMap?.clear()

        googleMap?.addMarker(
            MarkerOptions()
                .position(location)
                .title("High Score Location")
        )

        googleMap?.animateCamera(
            CameraUpdateFactory.newLatLngZoom(location, 15f)
        )
    }
}
