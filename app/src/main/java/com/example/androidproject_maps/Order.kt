package com.example.androidproject_maps

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.size
import kotlinx.android.synthetic.main.order_item.view.*
import kotlinx.android.synthetic.main.order_list.*




class Order : AppCompatActivity() {
    private lateinit var menuArr : ArrayList<MenuFood>
    private lateinit var shopKey : String
    private lateinit var uid : String
    private lateinit var shopName : String
    private lateinit var shopRating : String
    private lateinit var bank : String
    private lateinit var bankID : String
    private lateinit var address : String
    private lateinit var accountHolder : String
    private lateinit var customer_phoneNum : String
    var pay : Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.order_list)
        //intent 받는 부분
        menuArr = intent.getSerializableExtra("MenuArr") as ArrayList<MenuFood>
        shopKey = intent.extras.getString("ShopKey")
        uid = intent.extras.getString("uid")
        shopName = intent.extras.getString("ShopName")
        shopRating = intent.extras.getString("ShopRating")
        bank = intent.extras.getString("Bank")
        bankID = intent.extras.getString("BankID")
        address = intent.extras.getString("Address")
        accountHolder = intent.extras.getString("AccountHolder")
        customer_phoneNum = intent.extras.getString("CustomerPhoneNum")


        var amounts : Array<Int> = Array(menuArr.size,{0})
        val adapter = OrderListAdapter(this,menuArr,amounts)
        orderlist.adapter = adapter
        pay = 0



        paybutton.setOnClickListener{

            for(i in menuArr.indices ){
                pay = pay + menuArr.get(i).price.toInt()*amounts[i]
            }
            if(pay != 0) {
                paytoast()

                var customerRequest : String
                //요청사항 저장
                if(TextUtils.isEmpty(customer_request_View.text)){
                    customerRequest = "요청사항 없음"
                }
                else{
                    customerRequest = customer_request_View.text.toString()
                }
                //주문한 매뉴, 수량 저장
                var ordermenuList = arrayListOf<MenuFood>()
                for (i in 0..orderlist.size-1) {
                    val view = orderlist.getChildAt(i)
                    var num = view.order_amounts.text.toString()
                    if(num.equals("0")){
                        //이 매뉴는 주문안한거니까 그냥 건너뛰기
                    }
                    else {
                        var price = view.foodpriceTxt.text.toString()
                        var name = view.foodnameTxt.text.toString()
                        var menufood = MenuFood(name, price, "")
                        menufood.amounts = num
                        ordermenuList.add(menufood)
                    }
                }


                val paymentintent = Intent(this, PaymentActivity::class.java)
                paymentintent.putExtra("MenuArr",menuArr)
                paymentintent.putExtra("ShopName",shopName)
                paymentintent.putExtra("OrderMenuList",ordermenuList)
                paymentintent.putExtra("CustomerRequest",customerRequest)
                paymentintent.putExtra("ShopKey", shopKey)
                paymentintent.putExtra("Pay", pay)
                paymentintent.putExtra("Uid",uid)
                paymentintent.putExtra("ShopRating",shopRating)
                paymentintent.putExtra("Address",address)
                paymentintent.putExtra("Bank",bank)
                paymentintent.putExtra("BankID",bankID)
                paymentintent.putExtra("AccountHolder",accountHolder)
                paymentintent.putExtra("CustomerPhoneNum",customer_phoneNum)
                finish()
                startActivity(paymentintent)
            }
            else{//pay=0일경우 즉, 아무것도 주문 안했는데 결제창으로 넘어가려고 한경우
                orderDialogShow()
            }

        }

    }

    private fun paytoast(){


        Toast.makeText(this,"결제금액 :${pay}원", Toast.LENGTH_SHORT).show()
        Handler().postDelayed({},4000)
    }

    override fun onBackPressed() {
        val shopinfoIntent = Intent(this@Order,ShopInfoActivity::class.java)
        shopinfoIntent.putExtra("ShopKey",shopKey)
        shopinfoIntent.putExtra("ShopName",shopName)
        shopinfoIntent.putExtra("MenuArr", menuArr)
        shopinfoIntent.putExtra("ShopRating",shopRating.toFloat())
        shopinfoIntent.putExtra("Address",address)
        shopinfoIntent.putExtra("Bank",bank)
        shopinfoIntent.putExtra("BankID",bankID)
        shopinfoIntent.putExtra("AccountHolder",accountHolder)
        finish()
        startActivity(shopinfoIntent)
    }
    private fun orderDialogShow(){
        var builder = AlertDialog.Builder(this@Order)
        builder.setTitle("주문 확인")
        builder.setMessage("주문하실 매뉴의 수량을 선택해주세요!!")
        builder.setPositiveButton(
            "예"
        ) { dialog, which ->

        }
        builder.show()
    }

}

