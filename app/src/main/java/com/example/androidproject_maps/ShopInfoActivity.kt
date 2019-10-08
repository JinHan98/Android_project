package com.example.androidproject_maps

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_shop_info.*

class ShopInfoActivity :AppCompatActivity() {
    var foodmenuList = arrayListOf<MenuFood>(
        MenuFood("해물파스타", "10000", "seapasta"),
        MenuFood("알리오올리오","12000","alio")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop_info)

        val foodMenuAdapter = MainListAdapter(this, foodmenuList)
        mainListView.adapter = foodMenuAdapter
        map.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }
    }
}