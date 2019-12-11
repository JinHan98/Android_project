package com.example.androidproject_maps

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import com.google.firebase.storage.FirebaseStorage

class ShopviewlistAdapter (val context: Context, val shoplist: ArrayList<Shopinfo>): BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = LayoutInflater.from(context).inflate(R.layout.activity_maps_listview_item,null)
        val shopPhoto = view.findViewById<ImageView>(R.id.foodPhotoImg)
        val shopName = view.findViewById<TextView>(R.id.foodNameTv)
        val shopRate = view.findViewById<RatingBar>(R.id.maps_ratingBar)
        shopName.text = shoplist.get(position).name
        val photourl = shoplist[position].photoUrl
        var storageShopImgRef = FirebaseStorage.getInstance().getReference(photourl)
        /*메모리에 다운로드 앱이 꺼지면 날라감*/

        var ONE_MEGABYTE : Long = 1024*1024
        storageShopImgRef.getBytes(ONE_MEGABYTE).addOnCompleteListener{
            if(it.isSuccessful) {
                shopPhoto.setImageBitmap(BitmapFactory.decodeByteArray(it.result,0,it.result!!.size))
            }
        }
        shopRate.rating = shoplist.get(position).shopRate

        return view


    }

    override fun getItem(position: Int): Any {
        return shoplist[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return shoplist.size
    }
}