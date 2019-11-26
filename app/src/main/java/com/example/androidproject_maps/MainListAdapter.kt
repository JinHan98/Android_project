package com.example.androidproject_maps

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.storage.FirebaseStorage

class MainListAdapter (val context: Context, val MenuFoodList: ArrayList<MenuFood>,val shopKey : String): BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = LayoutInflater.from(context).inflate(R.layout.item,null)
        val foodPhoto = view.findViewById<ImageView>(R.id.foodPhotoImg)
        val foodName = view.findViewById<TextView>(R.id.foodNameTv)
        val foodPrice = view.findViewById<TextView>(R.id.foodPriceTv)

        val menuFood = MenuFoodList[position]
        val photourl = MenuFoodList[position].photourl


        var storageShopImgRef = FirebaseStorage.getInstance().getReference(photourl)
        /*메모리에 다운로드 앱이 꺼지면 날라감*/

        var ONE_MEGABYTE : Long = 1024*1024
        storageShopImgRef?.getBytes(ONE_MEGABYTE).addOnCompleteListener{
            if(it.isSuccessful) {
            }
            foodPhoto.setImageBitmap(BitmapFactory.decodeByteArray(it.result!!,0,it.result!!.size))
        }

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