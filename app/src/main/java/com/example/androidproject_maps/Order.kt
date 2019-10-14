package com.example.androidproject_maps

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_order.*

class Order : AppCompatActivity() {
    var a : Int = 0
    var b : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)

        val menulist = arrayOf("해물파스타", "알리오올리오")

        val list : ListView = findViewById(R.id.orderlistview)

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, menulist)

        list.adapter = adapter

        var quantitylist = arrayOf(a,b)

        var list1 : ListView = findViewById(R.id.quantitylistview)

        var adapter1 = ArrayAdapter(this, android.R.layout.simple_list_item_1, quantitylist)

        list1.adapter = adapter1

        up1.setOnClickListener{
            up1()
        }

        up2.setOnClickListener{
            up2()
        }

        down1.setOnClickListener{
            down1()
        }

        down2.setOnClickListener{
            down2()
        }

        paybutton.setOnClickListener{
            paytoast()
        }


    }
    private fun up1(){
        a++

        var quantitylist = arrayOf(a,b)

        var list1 : ListView = findViewById(R.id.quantitylistview)

        var adapter1 = ArrayAdapter(this, android.R.layout.simple_list_item_1, quantitylist)

        list1.adapter = adapter1
    }
    private fun up2(){
        b++

        var quantitylist = arrayOf(a,b)

        var list1 : ListView = findViewById(R.id.quantitylistview)

        var adapter1 = ArrayAdapter(this, android.R.layout.simple_list_item_1, quantitylist)

        list1.adapter = adapter1
    }
    private fun down1(){
        if(a==0){
            a = 0
        }else {
            a--
        }

        var quantitylist = arrayOf(a,b)

        var list1 : ListView = findViewById(R.id.quantitylistview)

        var adapter1 = ArrayAdapter(this, android.R.layout.simple_list_item_1, quantitylist)

        list1.adapter = adapter1
    }
    private fun down2(){
        if(b==0){
            b = 0
        }else{
            b--
        }

        var quantitylist = arrayOf(a,b)

        var list1 : ListView = findViewById(R.id.quantitylistview)

        var adapter1 = ArrayAdapter(this, android.R.layout.simple_list_item_1, quantitylist)

        list1.adapter = adapter1
    }
    private fun paytoast(){
        var pay : Int = 0
        pay = a*10000 + b*12000
        Toast.makeText(this,"결제금액 :${pay}원", Toast.LENGTH_SHORT).show()
    }
}
