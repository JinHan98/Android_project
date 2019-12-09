package com.example.androidproject_maps

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_order_finish.*

class OrderFinishActivity : AppCompatActivity() {
    private lateinit var bank : String
    private lateinit var bankID : String
    private lateinit var accountHolder : String
    private lateinit var pay : String
    private lateinit var backDialog : AlertDialog
    private lateinit var shopKey : String
    private lateinit var shopName : String
    private lateinit var menuArr : ArrayList<MenuFood>
    private lateinit var shopRating : String
    private lateinit var address : String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_finish)
        bank = intent.extras.getString("Bank")
        bankID = intent.extras.getString("BankID")
        accountHolder = intent.extras.getString("AccountHolder")
        pay = intent.extras.getString("Pay")
        shopKey = intent.extras.getString("ShopKey")
        shopName = intent.extras.getString("ShopName")
        menuArr = intent.getSerializableExtra("MenuArr") as ArrayList<MenuFood>
        shopRating = intent.extras.getString("ShopRating")
        address = intent.extras.getString("Address")


        bankIDTextView.text = bankID
        bankTextView.text = bank
        accountHolderTextView.text = accountHolder
        payTextView.text = pay + "원"
    }

    override fun onBackPressed() {
        var backDialogBuilder = AlertDialog.Builder(this@OrderFinishActivity)
        backDialog = backDialogBuilder.create()
        backDialogBuilder.setTitle("주문완료!")
        backDialogBuilder.setMessage("돌아가시겠습니까?")
        backDialogBuilder.setPositiveButton(
            "예"
        ) { dialog, which ->
            onPause()
            backDialog.dismiss()
            finish()
            var intent = Intent(this@OrderFinishActivity, ShopInfoActivity::class.java)
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
