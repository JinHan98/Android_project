package com.example.androidproject_maps

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_payment.*
import java.sql.Time
import java.text.SimpleDateFormat

private lateinit var database: DatabaseReference
private lateinit var order_key : String
class PaymentActivity : AppCompatActivity() {
    private lateinit var  order_way : String
    private lateinit var  pay : String
    private lateinit var  shopKey: String
    private lateinit var uid : String
    private lateinit var orderMenuList : ArrayList<MenuFood>
    private lateinit var customerRequest: String
    private lateinit var orderTime : String
    private lateinit var backDialog : AlertDialog
    private lateinit var shopName : String
    private lateinit var menuArr : ArrayList<MenuFood>
    private lateinit var shopRating : String
    private lateinit var bank : String
    private lateinit var bankID : String
    private lateinit var address : String
    private lateinit var accountHolder : String
    private lateinit var orderDay : String
    private lateinit var customer_phoneNum : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)
        pay = intent.extras.getInt("Pay").toString()
        shopKey = intent.extras.getString("ShopKey")
        uid = intent.extras.getString("Uid")
        orderMenuList = intent.getSerializableExtra("OrderMenuList") as ArrayList<MenuFood>
        customerRequest = intent.extras.getString("CustomerRequest")
        shopName = intent.extras.getString("ShopName")
        menuArr = intent.getSerializableExtra("MenuArr") as ArrayList<MenuFood>//백버튼 눌렀을떄 shopinfoActivity로 돌아가기위해
        shopRating = intent.extras.getString("ShopRating")
        bank = intent.extras.getString("Bank")
        bankID = intent.extras.getString("BankID")
        address = intent.extras.getString("Address")
        accountHolder = intent.extras.getString("AccountHolder")
        customer_phoneNum = intent.extras.getString("CustomerPhoneNum")

        addressText.text = address


        payText.text = pay + " 원"
        accountbt.setOnClickListener{
            order_way = "계좌이체"
            payDialogShow()
        }
        paybt.setOnClickListener{
            order_way = "직접결제"
            payDialogShow()
        }
    }
    private fun payDialogShow(){
        var builder = AlertDialog.Builder(this@PaymentActivity)
        builder.setTitle("주문 확인")
        builder.setMessage(order_way+"로 "+pay+"원을 결제하시겠습니까?")
        builder.setPositiveButton(
            "예"
        ) { dialog, which ->
            //주문 시간 구하기
            var now = System.currentTimeMillis()
            var timenow = Time(now)
            var ordertimeDataFomat = SimpleDateFormat("hh:mm:ss a")
            var orderdayDataFomat = SimpleDateFormat("yyyy년 MM월 dd일")
            orderTime = ordertimeDataFomat.format(timenow)
            orderDay = orderdayDataFomat.format(timenow)
            //DB에 올리기
            database = FirebaseDatabase.getInstance().getReference("/shops/"+shopKey+"/")
            writeNewOrderInfo(pay, customer_phoneNum, customerRequest,orderTime,uid,false,shopName,orderDay,shopKey)
            var order_key_shop = order_key
            database = FirebaseDatabase.getInstance().getReference("/Customers/"+uid+"/")//손님 DB에도 올려야함
            writeNewOrderInfo(pay, customer_phoneNum, customerRequest,orderTime,uid,false,shopName,orderDay,shopKey)
            for(orderMenu in orderMenuList){//주문한 매뉴를 DB에 올리기
                database = FirebaseDatabase.getInstance().getReference("/shops/"+shopKey+"/")
                writeNewOrderMenu(orderMenu.name,orderMenu.amounts, order_key_shop)
                database = FirebaseDatabase.getInstance().getReference("/Customers/"+uid+"/")//손님 DB에도 올려야함
                writeNewOrderMenu(orderMenu.name,orderMenu.amounts, order_key)
                database = FirebaseDatabase.getInstance().getReference("/Customers/")//손님 레이팅 업데이트
                val valeventlistener = object : ValueEventListener {
                    override fun onDataChange(p0: DataSnapshot) {
                        for(snapshot : DataSnapshot in p0.children) {
                            val value = snapshot.getValue(Customer::class.java)
                            if(value!=null){
                                val valueKey = snapshot.key.toString()
                                if(valueKey.equals(uid)){
                                    //고객의 총 주문 가격 업데이트
                                    val totalpay = value.totalpay
                                    var db = FirebaseDatabase.getInstance().getReference("/Customers/" + uid + "/totalpay/")
                                    val newtotalpay = totalpay.toInt() + pay.toInt()
                                    db.setValue(newtotalpay.toString())
                                    //등급 업데이트
                                    if(newtotalpay>2000000){
                                        var db = FirebaseDatabase.getInstance().getReference("/Customers/" + uid + "/ratingurl/")
                                        db.setValue("/images/ClientLevel/vip.PNG")
                                    }
                                    else if(newtotalpay>1000000){
                                        var db = FirebaseDatabase.getInstance().getReference("/Customers/" + uid + "/ratingurl/")
                                        db.setValue("/images/ClientLevel/gold.PNG")
                                    }
                                    else{
                                        var db = FirebaseDatabase.getInstance().getReference("/Customers/" + uid + "/ratingurl/")
                                        db.setValue("/images/ClientLevel/silver.PNG")
                                    }
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
            val finishintent = Intent(this, OrderFinishActivity::class.java)
            finishintent.putExtra("Bank",bank)
            finishintent.putExtra("BankID",bankID)
            finishintent.putExtra("AccountHolder",accountHolder)
            finishintent.putExtra("Pay",pay)
            finishintent.putExtra("ShopKey",shopKey)
            finishintent.putExtra("ShopName",shopName)
            finishintent.putExtra("MenuArr",menuArr)
            finishintent.putExtra("ShopRating",shopRating)
            finishintent.putExtra("Address",address)
            finish()
            startActivity(finishintent)
        }
        builder.setNegativeButton(
            "아니오"
        ){ dialog, which ->

        }
        builder.show()
    }

    override fun onBackPressed() {
        var backDialogBuilder = AlertDialog.Builder(this@PaymentActivity)
        backDialog = backDialogBuilder.create()
        backDialogBuilder.setTitle("주문 취소")
        backDialogBuilder.setMessage("주문을 취소하시겠습니까??")
        backDialogBuilder.setPositiveButton(
            "예"
        ) { dialog, which ->
            onPause()
            backDialog.dismiss()
            finish()
            var intent = Intent(this@PaymentActivity, ShopInfoActivity::class.java)
            intent.putExtra("ShopKey",shopKey)
            intent.putExtra("ShopName",shopName)
            intent.putExtra("MenuArr",menuArr)
            intent.putExtra("ShopRating",shopRating.toFloat())
            intent.putExtra("Address",address)
            intent.putExtra("Bank",bank)
            intent.putExtra("BankID",bankID)
            intent.putExtra("AccountHolder",accountHolder)
            finish()
            startActivity(intent)

        }
        backDialogBuilder.setNegativeButton(
            "아니오"
        ){ dialog, which ->
            backDialog.cancel()
        }
        backDialogBuilder.show()
    }


}
@IgnoreExtraProperties
data class OrderInfo (
    var pay : String = "",
    var phoneNum : String = "",
    var customerRequest : String = "",
    var ordertime : String = "",
    var status : Int = 0,//0 대기중(수락대기중) 1 처리중(요리중) 2 완료 (음식이 나감)
    var customerUid : String ="",
    var iswritereview : Boolean = false,
    var shopname : String = "",
    var mykey : String = "",//리뷰쓸때 리뷰의 key로 이용.
    var orderday : String ="",
    var shopKey : String = "",
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
            "customerUid" to customerUid,
            "iswritereview" to iswritereview,
            "shopname" to shopname,
            "mykey" to mykey,
            "orderday" to orderday,
            "shopKey" to shopKey,
            "stars" to stars
        )
    }
}
private fun writeNewOrderInfo(pay: String,phoneNum: String,customerRequest: String,ordertime: String,customerUid: String,iswritereview: Boolean,shopname: String,orderday: String,shopKey: String) {
    // Create new post at /user-posts/$userid/$postid and at
    // /posts/$postid simultaneously
    val key = database.child("orders").push().key
    order_key = key.toString()
    if (key == null) {
        Log.w(ContentValues.TAG, "Couldn't get push key for orders")
        return
    }

    val order = OrderInfo(pay, phoneNum, customerRequest,ordertime,0,customerUid,iswritereview,shopname,key,orderday,shopKey)
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
