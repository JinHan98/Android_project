package com.example.androidproject_maps

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.Exclude
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.android.synthetic.main.order_item.*
import kotlinx.android.synthetic.main.order_list.*
import java.sql.Time
import java.text.SimpleDateFormat

private lateinit var database: DatabaseReference
private lateinit var order_key : String
class Order : AppCompatActivity() {
    private lateinit var menuArr : ArrayList<MenuFood>
    private lateinit var shopKey : String
    private lateinit var uid : String
    private lateinit var shopName : String
    var pay : Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.order_list)
        //intent 받는 부분
        menuArr = intent.getSerializableExtra("MenuArr") as ArrayList<MenuFood>
        shopKey = intent.extras.getString("ShopKey")
        uid = intent.extras.getString("uid")
        shopName = intent.extras.getString("ShopName")

        database = FirebaseDatabase.getInstance().getReference("shops/"+shopKey+"/")
        var amounts : Array<Int> = Array(menuArr.size,{0})
        val adapter = OrderListAdapter(this,menuArr,amounts)
        //고객 폰 넘버 받아야하고
        //요청 사항 받아야함
        orderlist.adapter = adapter





        paybutton.setOnClickListener{

            for(i in menuArr.indices ){
                pay = pay + menuArr.get(i).price.toInt()*amounts[i]
            }
            if(pay != 0) {
                paytoast()
                //주문 시간 구하기
                var now = System.currentTimeMillis()
                var timenow = Time(now)
                var ordertimeDataFomat = SimpleDateFormat("hh:mm:ss a");
                var orderTime = ordertimeDataFomat.format(timenow)
                //db에 주문내역 올리기
                writeNewOrderInfo(pay.toString(), "0", customer_request_View.text.toString(), orderTime)
                for (i in menuArr.indices) {
                    val view = orderlist.getChildAt(i)
                    var num = order_amounts.text.toString()
                    writeNewOrderMenu(menuArr.get(i).name, num, order_key)
                }


                val paymentintent = Intent(this, PaymentActivity::class.java)
                paymentintent.putExtra("ShopKey", shopKey)
                paymentintent.putExtra("Pay", pay)
                paymentintent.putExtra("Uid",uid)
                paymentintent.putExtra("OrderKey", order_key)
                startActivity(paymentintent)
            }
            else{
                Toast.makeText(this,"주문하실 매뉴의 수량을 선택하여 주세요", Toast.LENGTH_SHORT).show()
                Handler().postDelayed({},8000)
            }

        }

    }

    private fun paytoast(){


        Toast.makeText(this,"결제금액 :${pay}원", Toast.LENGTH_SHORT).show()
        Handler().postDelayed({},4000)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val shopinfoIntent = Intent(this@Order,ShopInfoActivity::class.java)
        shopinfoIntent.putExtra("ShopKey",shopKey)
        shopinfoIntent.putExtra("ShopName",shopName)
        shopinfoIntent.putExtra("MenuArr", menuArr)
    }

}
@IgnoreExtraProperties
data class OrderInfo (
    var pay : String? = "",
    var phoneNum : String? = "",
    var customerRequest : String? = "",
    var ordertime : String? = "",
    var status : Int = 0,//0 대기중(수락대기중) 1 처리중(요리중) 2 완료 (음식이 나감)
    var starCount: Int = 0,
    var stars: MutableMap<String, Boolean> = HashMap()
) {

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "pay" to pay ,
            "phoneNum" to phoneNum,
            "customerRequest" to customerRequest,
            "ordertime" to ordertime,
            "status" to status,
            "starCount" to starCount,
            "stars" to stars
        )
    }
}
private fun writeNewOrderInfo(pay: String,phoneNum: String,customerRequest: String?,ordertime: String) {
    // Create new post at /user-posts/$userid/$postid and at
    // /posts/$postid simultaneously
    val key = database.child("orders").push().key
    order_key = key.toString()
    if (key == null) {
        Log.w(ContentValues.TAG, "Couldn't get push key for orders")
        return
    }

    var customerRequestNull : String
    if(customerRequest == null){
        customerRequestNull = "요청사항 없음"
        val order = OrderInfo(pay,phoneNum,customerRequestNull,ordertime)
        val orderValues = order.toMap()

        val childUpdates = HashMap<String, Any>()
        childUpdates["/orders/$key"] = orderValues
        database.updateChildren(childUpdates)

    }
    else {
        val order = OrderInfo(pay, phoneNum, customerRequest,ordertime)
        val orderValues = order.toMap()

        val childUpdates = HashMap<String, Any>()
        childUpdates["/orders/$key"] = orderValues
        database.updateChildren(childUpdates)
    }

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
