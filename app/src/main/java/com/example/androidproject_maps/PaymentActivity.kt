package com.example.androidproject_maps

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_payment.*

class PaymentActivity : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)
        val pay = intent.extras.getInt("Pay")
        val shopKey = intent.extras.getString("ShopKey")
        val orderKey = intent.extras.getString("OrderKey")
        val uid = intent.extras.getString("Uid")
        database = FirebaseDatabase.getInstance().getReference("Customers/"+uid+"/phoneNum")
        payText.text = pay.toString() + " 원"
        accountbt.setOnClickListener{
        }
        paybt.setOnClickListener{

        }
    }

}
