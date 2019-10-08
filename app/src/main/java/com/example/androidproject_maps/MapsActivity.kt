package com.example.androidproject_maps

import android.content.pm.PackageManager
import android.graphics.Point
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private lateinit var map: GoogleMap
    private lateinit var lastLocation: Location
    private lateinit var fusedLocationclient:FusedLocationProviderClient
    private var MapNumber = 1

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE =1
    }
    private fun setUpMap(){
        if(ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE)
            return
        }
        map.isMyLocationEnabled =true

        fusedLocationclient.lastLocation.addOnSuccessListener(this) {location ->
            if (location != null){
                lastLocation  = location
                val currentLatLng = LatLng(location.latitude,location.longitude)
                //placeMarkerOnMap(currentLatLng)
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng,16f))
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationclient = LocationServices.getFusedLocationProviderClient(this)
    }
    /*private fun placeMarkerOnMap(location: LatLng){
        val markerOptions = MarkerOptions().position(location)
        val titleStr = getAddress(location)
        markerOptions.title(titleStr)
        map.addMarker(markerOptions)
    }*/
    private fun getAddress(LatLng: LatLng): String{
        val geocoder = Geocoder(this)
        val addresses : List<Address>?
        val address : Address?
        var addressText =""
        try {
            addresses = geocoder.getFromLocation(LatLng.latitude, LatLng.longitude, 1)
            if (null != addresses && !addresses.isEmpty()) {
                address = addresses[0]
                for (i in 0 until address.maxAddressLineIndex) {
                    addressText += if (i == 0) address.getAddressLine(i) else
                        "\n" + address.getAddressLine(i)
                }
            }
        } catch (e: IOException){
            Log.e("MapsActivity", e.localizedMessage)
        }
        return addressText
    }
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        map.getUiSettings().setZoomControlsEnabled(true)
        map.setOnMapClickListener (object : GoogleMap.OnMapClickListener{
            override fun onMapClick(latLng : LatLng) {
                var screenPt: Point = map.projection.toScreenLocation(latLng)
                var mOption = MarkerOptions().position(map.projection.fromScreenLocation(screenPt))
                mOption.title("장소${MapNumber}")
                MapNumber++
                map.addMarker(mOption)
            }
        })
        setUpMap()
        map.isMyLocationEnabled =true
        fusedLocationclient.lastLocation.addOnSuccessListener(this) { location ->
            if (location != null){
                lastLocation = location
                val currentLatLng = LatLng(location.latitude,location.longitude)
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng,
                    16f))
            }
        }

    }

    override fun onMarkerClick(p0: Marker?) = false
}
