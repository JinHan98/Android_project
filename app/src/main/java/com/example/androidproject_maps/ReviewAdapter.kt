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

class ReviewAdapter (val context: Context, val ReviewList: ArrayList<Review>): BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = LayoutInflater.from(context).inflate(R.layout.activity_review_item,null)
        val name = view.findViewById<TextView>(R.id.review_name)
        val rating = view.findViewById<RatingBar>(R.id.ratingBar)
        val review_text = view.findViewById<TextView>(R.id.review_text)
        val reverse_idx = ReviewList.size -1 - position//가장 최근것이 맨위로 올라가기 위해서
        val review_day = view.findViewById<TextView>(R.id.review_timeView)
        review_day.text = ReviewList.get(reverse_idx).time
        name.text = ReviewList.get(reverse_idx).client_name
        rating.rating = ReviewList.get(reverse_idx).rating
        review_text.text = ReviewList.get(reverse_idx).review_text

        var client_level_photo = view.findViewById<ImageView>(R.id.client_level)
        var storageShopImgRef = FirebaseStorage.getInstance().getReference(ReviewList.get(reverse_idx).client_rating_url)
        /*메모리에 다운로드 앱이 꺼지면 날라감*/
        var ONE_MEGABYTE : Long = 1024*1024
        storageShopImgRef.getBytes(ONE_MEGABYTE).addOnCompleteListener{
            if(it.isSuccessful) {
            }
            client_level_photo.setImageBitmap(BitmapFactory.decodeByteArray(it.result!!,0,it.result!!.size))
        }
        var review_photo = view.findViewById<ImageView>(R.id.review_image)
        var storageShopImgRef2 = FirebaseStorage.getInstance().getReference(ReviewList.get(reverse_idx).photourl)
        /*메모리에 다운로드 앱이 꺼지면 날라감*/
        storageShopImgRef2.getBytes(ONE_MEGABYTE).addOnCompleteListener{
            if(it.isSuccessful) {
            }
            review_photo.setImageBitmap(BitmapFactory.decodeByteArray(it.result!!,0,it.result!!.size))
        }


        return view


    }

    override fun getItem(position: Int): Any {
        return ReviewList[ReviewList.size-1-position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return ReviewList.size
    }
}