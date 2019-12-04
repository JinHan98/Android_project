package com.example.androidproject_maps

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_mypage.*

class MypageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mypage)

        reviewbt.setOnClickListener{
            val reviewintent = Intent(this, ReviewActivity::class.java)
            startActivity(reviewintent)
        }

        myinfobt.setOnClickListener{
            val myinfointent = Intent(this, MyinfoActivity::class.java)
            startActivity(myinfointent)
        }

        logoutbt.setOnClickListener{
            Toast.makeText(this,"업데이트 예정입니다.", Toast.LENGTH_SHORT).show()
        }


    }



}
