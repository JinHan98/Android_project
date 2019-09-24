package com.example.androidshopinfo


import android.support.v7.app.AppCompatActivity
import android.os.Bundle

import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    var foodmenuList = arrayListOf<MenuFood>(
        MenuFood("해물파스타", "10000", "seapasta"),
        MenuFood("알리오올리오","12000","alio")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val foodMenuAdapter = MainListAdapter(this, foodmenuList)
        mainListView.adapter = foodMenuAdapter
    }
}
