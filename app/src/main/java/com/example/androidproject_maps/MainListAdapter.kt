package com.example.androidproject_maps

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

class MainListAdapter (val context: Context, val MenuFoodList: ArrayList<MenuFood>): BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = LayoutInflater.from(context).inflate(R.layout.item,null)
        val foodPhoto = view.findViewById<ImageView>(R.id.foodPhotoImg)
        val foodName = view.findViewById<TextView>(R.id.foodNameTv)
        val foodPrice = view.findViewById<TextView>(R.id.foodPriceTv)

        val menuFood = MenuFoodList[position]
        foodPhoto.setImageURI(Uri.parse(menuFood.uri))
        foodName.text = menuFood.name
        foodPrice.text = menuFood.price

        return view


    }

    override fun getItem(position: Int): Any {
        return MenuFoodList[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return MenuFoodList.size
    }
}