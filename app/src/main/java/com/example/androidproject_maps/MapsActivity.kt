package com.example.androidproject_maps

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.get
import androidx.core.view.size
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_maps.*
import kotlinx.android.synthetic.main.activity_maps_listview_item.view.*

private lateinit var database: DatabaseReference//database reference
class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private lateinit var map: GoogleMap
    private lateinit var lastLocation: Location
    private lateinit var fusedLocationclient:FusedLocationProviderClient
    var mCurrentLocation = Location("Current Location")
    val DISTANCE = 1000.00//1km  이내에 상점들 리스트뷰에 띄울것

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



        //Database
        var latitude : Double =0.0
        var longitude : Double =0.0
        var shopKey : String?
        var shopName : String
        var food_name : String
        var price : String
        var shopinfoArr : ArrayList<Shopinfo> = arrayListOf()
        var targetNum  = 0
        database = FirebaseDatabase.getInstance().getReference("shops/")
        
        val valeventlistener = object : ValueEventListener {
            //구글 맵 밑에 리스트 뷰
            var shoplist_listview = arrayListOf<Shopinfo>()
            var shopLocation = Location("Shop Location")
            override fun onDataChange(p0: DataSnapshot) {
                shopinfoArr.clear()
                for(snapshot : DataSnapshot in p0.children) {
                    val value: Shop? = snapshot.getValue(Shop::class.java)
                    if (value != null) {
                        shopName = value?.shop_name.toString()
                        shopKey = snapshot.key //key값 받아내기  중요


                        var shopinfo =
                            Shopinfo(shopName, shopKey!!)//DB에 shop 추가할때 무조건 key값이 생기니까 널일수없다
                        shopinfo.shopKey = shopKey.toString()
                        shopinfo.photoUrl = value.photourl
                        shopinfo.shopRate = value.rating!!

                        latitude = value.latitude!!.toDouble()
                        shopLocation.latitude = latitude
                        shopinfo.latitude=latitude


                        longitude = value.longitude!!.toDouble()
                        shopLocation.longitude = longitude
                        shopinfo.longitude=longitude

                        var distance = mCurrentLocation.distanceTo(shopLocation)
                        if (distance < DISTANCE) {//현재 위치와 1km 이내에 있는 shop이라면
                            shoplist_listview.add(shopinfo)
                        }


                        for (snapshot2: DataSnapshot in snapshot.children) {
                            if (snapshot2.key.toString().equals("menus")) {
                                for (snapshot3: DataSnapshot in snapshot2.children) {
                                    val menus: Foodmenu? = snapshot3.getValue(Foodmenu::class.java)

                                    food_name = menus?.food_name.toString()
                                    price = menus?.price.toString()

                                    shopinfo.menuArr.add(
                                        MenuFood(
                                            food_name,
                                            price,
                                            menus?.photourl!!
                                        )
                                    )
                                }
                            }
                        }

                        val mOption = MarkerOptions().position(LatLng(latitude, longitude))
                        mOption.title(shopName)
                        shopinfoArr.add(shopinfo)
                        map.addMarker(mOption)

                        map.setOnMarkerClickListener(object : GoogleMap.OnMarkerClickListener {
                            override fun onMarkerClick(p0: Marker?): Boolean {
                                for (shop in shopinfoArr) {
                                    if (p0!!.title.equals(shop.name)) {
                                        targetNum = shopinfoArr.indexOf(shop)
                                    }
                                }
                                ShopButton.setOnClickListener {

                                    val intent =
                                        Intent(applicationContext, ShopInfoActivity::class.java)
                                    intent.putExtra("ShopKey", shopinfoArr.get(targetNum).shopKey)
                                    intent.putExtra("ShopName", shopinfoArr.get(targetNum).name)
                                    intent.putExtra("MenuArr", shopinfoArr.get(targetNum).menuArr)
                                    intent.putExtra("ShopRating",shopinfoArr.get(targetNum).shopRate)
                                    startActivity(intent)

                                }

                                if (ShopButton.visibility == GONE) {
                                    ShopButton.visibility = VISIBLE
                                } else {
                                    ShopButton.visibility == GONE
                                }

                                return false
                            }
                        })
                        map.setOnMapClickListener(object : GoogleMap.OnMapClickListener {
                            override fun onMapClick(p0: LatLng?) {
                                if (ShopButton.visibility == VISIBLE) {
                                    ShopButton.visibility = GONE
                                }
                            }
                        })
                    }


                }
                //근처 1km 상점 리스트뷰에 뜨게하기
                var shopviewlistAdapter = ShopviewlistAdapter(this@MapsActivity,shoplist_listview)
                shopviewlist.adapter = shopviewlistAdapter
                shopviewlist.setOnItemClickListener { parent, view, position, id ->
                    var latLng =LatLng(shoplist_listview[position].latitude,shoplist_listview[position].longitude)
                    map.animateCamera(CameraUpdateFactory.newLatLng(latLng))
                    var targetShop : String
                    for (shop in shopinfoArr) {
                        if (shoplist_listview[position].name.equals(shop.name)) {
                            targetNum = shopinfoArr.indexOf(shop)
                            targetShop = shop.name
                            for ( i in 0 .. shopviewlist.size-1) {
                                if (shopviewlist[i].foodNameTv.text.equals(targetShop)) {
                                    shopviewlist[i].setBackgroundColor(Color.rgb(255, 255, 153))
                                } else {
                                    shopviewlist[i].setBackgroundColor(Color.WHITE)
                                }
                            }
                        }
                    }

                    ShopButton.setOnClickListener {

                        val intent =
                            Intent(applicationContext, ShopInfoActivity::class.java)
                        intent.putExtra("ShopKey", shopinfoArr.get(targetNum).shopKey)
                        intent.putExtra("ShopName", shopinfoArr.get(targetNum).name)
                        intent.putExtra("MenuArr", shopinfoArr.get(targetNum).menuArr)
                        intent.putExtra("ShopRating",shopinfoArr.get(targetNum).shopRate)
                        startActivity(intent)

                    }

                    if (ShopButton.visibility == GONE) {
                        ShopButton.visibility = VISIBLE
                    } else {
                        ShopButton.visibility == GONE
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }


        database.addValueEventListener(valeventlistener)
    }
    /*private fun placeMarkerOnMap(location: LatLng){
        val markerOptions = MarkerOptions().position(location)
        val titleStr = getAddress(location)
        markerOptions.title(titleStr)
        map.addMarker(markerOptions)
    }*/
    /*private fun getAddress(LatLng: LatLng): String{
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
    }*/
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        map.getUiSettings().setZoomControlsEnabled(true)
        /*map.setOnMapClickListener (object : GoogleMap.OnMapClickListener{
            override fun onMapClick(latLng : LatLng) {
                var screenPt: Point = map.projection.toScreenLocation(latLng)
                var mOption = MarkerOptions().position(map.projection.fromScreenLocation(screenPt))
                mOption.title("장소${MapNumber}")
                MapNumber++
                map.addMarker(mOption)
            }
        })*/
        setUpMap()
        map.isMyLocationEnabled =true
        fusedLocationclient.lastLocation.addOnSuccessListener(this) { location ->
            if (location != null){
                lastLocation = location
                val currentLatLng = LatLng(location.latitude,location.longitude)//이게 실시간현재 위치
                mCurrentLocation.latitude = currentLatLng.latitude
                mCurrentLocation.longitude = currentLatLng.longitude
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng,
                    16f))
            }
        }

    }

    override fun onMarkerClick(p0: Marker?) = false
}
