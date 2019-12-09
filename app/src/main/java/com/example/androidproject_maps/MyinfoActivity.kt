package com.example.androidproject_maps

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_myinfo.*

class MyinfoActivity : AppCompatActivity() {
    private lateinit var uid : String
    private lateinit var database: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_myinfo)
        uid = intent.extras.getString("Uid")



        /* 고객 등급 이미지 세팅*/
        database = FirebaseDatabase.getInstance().getReference("Customers/")
        val valeventlistener = object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                for (snapshot: DataSnapshot in p0.children) {
                    val value = snapshot.getValue(Customer::class.java)
                    if (value != null) {
                        val valueKey = snapshot.key.toString()
                        if(valueKey.equals(uid)){
                            nickname.text = value.nickname
                            phonenum.text = value.phonenum
                            amount.text = value.totalpay
                            var photourl = value.ratingurl
                            val storage = FirebaseStorage.getInstance().getReference(photourl)
                            var ONE_MEGABYTE : Long = 1024*1024
                            storage.getBytes(ONE_MEGABYTE).addOnCompleteListener{
                                if(it.isSuccessful){
                                    client_level_image.setImageBitmap(BitmapFactory.decodeByteArray(it.result,0,it.result!!.size))
                                }
                            }
                        }
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        }
        database.addListenerForSingleValueEvent(valeventlistener)
    }
}
@IgnoreExtraProperties
data class Customer (
    var nickname : String = "",
    var phonenum : String = "",
    var totalpay : String = "0",
    var ratingurl : String = "",
    var stars: MutableMap<String, Boolean> = HashMap()
) {

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "nickname" to nickname ,
            "phonenum" to phonenum,
            "totalpay" to totalpay,
            "stars" to stars
        )
    }
}