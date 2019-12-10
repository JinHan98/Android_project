package com.example.androidproject_maps

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_review__read.*
import kotlinx.android.synthetic.main.activity_review__write.shop_name

class Review_ReadActivity : AppCompatActivity() {
    private lateinit var shopName : String
    private lateinit var uid : String
    private lateinit var orderKey : String
    private lateinit var database : DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review__read)
        shopName = intent.extras.getString("ShopName")
        uid = intent.extras.getString("Uid")
        orderKey = intent.extras.getString("OrderKey")
        shop_name.text = shopName

        load_data()
    }
    private fun load_data(){
        database = FirebaseDatabase.getInstance().getReference("Customers/"+uid+"/reviews/")
        val valeventlistener = object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                for (snapshot: DataSnapshot in p0.children) {
                    if(snapshot.key.equals(orderKey)) {
                        val value = snapshot.getValue(Review::class.java)
                        if (value != null) {
                            //레이팅
                            rating_review.rating = value.rating
                            //이미지
                            val photoUrl = value.photourl
                            val storage = FirebaseStorage.getInstance().getReference(photoUrl)
                            /*메모리에 다운로드, 앱이 꺼지면 날라감*/
                            var ONE_MEGABYTE : Long = 1024*1024
                            storage.getBytes(ONE_MEGABYTE).addOnCompleteListener{
                                if(it.isSuccessful) {
                                }
                                review_image.setImageBitmap(BitmapFactory.decodeByteArray(it.result!!,0,it.result!!.size))
                            }
                            //리뷰내용
                            review_text.text = value.review_text
                        }
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }
        database.addListenerForSingleValueEvent(valeventlistener)
    }

}
