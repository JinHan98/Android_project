package com.example.androidproject_maps

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.Exclude
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.android.synthetic.main.order_list.*

private lateinit var database: DatabaseReference
private lateinit var order_key : String
class Order : AppCompatActivity() {

    var pay : Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.order_list)
        val menuArr = intent.getSerializableExtra("MenuArr") as ArrayList<MenuFood>

        database = FirebaseDatabase.getInstance().reference


        val adapter = OrderListAdapter(this,menuArr)

        orderlist.adapter = adapter



        paybutton.setOnClickListener{

            for(i in menuArr.indices ){
                val view = orderlist.getChildAt(i)
                var editText : EditText = view.findViewById(R.id.editText)
                var num : Int = editText.text.toString().toInt()
                pay = pay + menuArr.get(i).price.toInt()*num
            }
            paytoast()
            writeNewOrderInfo(pay.toString())
            for(i in menuArr.indices){
                val view = orderlist.getChildAt(i)
                var editText : EditText = view.findViewById(R.id.editText)
                var num = editText.text.toString()
                writeNewOrderMenu(menuArr.get(i).name,num, order_key)
            }
        }

    }

    private fun paytoast(){


        Toast.makeText(this,"결제금액 :${pay}원", Toast.LENGTH_SHORT).show()
    }
}
@IgnoreExtraProperties
data class OrderInfo (
    var pay : String? = "",

    var starCount: Int = 0,
    var stars: MutableMap<String, Boolean> = HashMap()
) {

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "pay" to pay ,

            "starCount" to starCount,
            "stars" to stars
        )
    }
}
private fun writeNewOrderInfo(pay: String) {
    // Create new post at /user-posts/$userid/$postid and at
    // /posts/$postid simultaneously
    val key = database.child("orders").push().key
    order_key = key.toString()
    if (key == null) {
        Log.w(ContentValues.TAG, "Couldn't get push key for orders")
        return
    }

    val order = OrderInfo(pay)
    val orderValues = order.toMap()

    val childUpdates = HashMap<String, Any>()
    childUpdates["/orders/$key"] = orderValues


    database.updateChildren(childUpdates)
}
@IgnoreExtraProperties
data class OrderMenu(
    var food_name: String? = "",
    var amounts: String? = "",
    var starCount: Int = 0,
    var stars: MutableMap<String, Boolean> = HashMap()
) {

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "food_name" to food_name ,
            "amounts" to amounts,
            "starCount" to starCount,
            "stars" to stars
        )
    }
}
private fun writeNewOrderMenu(food_name: String, amounts: String, orderKey : String) {
    // Create new post at /user-posts/$userid/$postid and at
    // /posts/$postid simultaneously
    val key = database.child("orders/ordermenus").push().key

    if (key == null) {
        Log.w(ContentValues.TAG, "Couldn't get push key for ordermenus")
        return
    }

    val orderMenu = OrderMenu(food_name,amounts)
    val orderMenuValues = orderMenu.toMap()

    val childUpdates = HashMap<String, Any>()
    childUpdates["/orders/"+orderKey+"/ordermenus/$key"] = orderMenuValues


    database.updateChildren(childUpdates)
}
