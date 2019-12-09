package com.example.androidproject_maps

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_logindetail.*

class LogindetailActivity : AppCompatActivity() {
    private lateinit var menuArr : ArrayList<MenuFood>
    private lateinit var shopKey : String
    private lateinit var shopName : String
    private lateinit var shopRating : String
    private lateinit var bank : String
    private lateinit var bankID : String
    private lateinit var address : String
    private lateinit var accountHolder : String
    private lateinit var mdataBase : DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logindetail)
        shopKey = intent.extras.getString("ShopKey")
        shopName = intent.extras.getString("ShopName")
        shopRating = intent.extras.getString("ShopRating")
        bank = intent.extras.getString("Bank")
        bankID = intent.extras.getString("BankID")
        address = intent.extras.getString("Address")
        accountHolder = intent.extras.getString("AccountHolder")
        menuArr = intent.getSerializableExtra("MenuArr") as ArrayList<MenuFood>

        OKbutton.setOnClickListener{
            if(nicknameEditTextView.text.equals(null) || phonenumEditTextView.text.equals(null)){
                checkDialog()
            }
            else{
                finishDialog(phonenumEditTextView.text.toString(),nicknameEditTextView.text.toString())
            }
        }
    }
    private fun finishDialog(phoneNum : String , nickName : String){
        var builder = AlertDialog.Builder(this@LogindetailActivity)
        builder.setMessage("기입하신 내용으로 회원가입 하시겠습니까?")
        builder.setPositiveButton(
            "예"
        ){dialog, which ->
            //db 에쓰고
            var user = FirebaseAuth.getInstance().currentUser
            var mUid = user!!.uid // 구글 로그인하고 넘어왔기때문에 null일수 없음
            mdataBase = FirebaseDatabase.getInstance().getReference("/Customers/"+mUid+"/phonenum/")
            mdataBase.setValue(phoneNum)
            mdataBase = FirebaseDatabase.getInstance().getReference("/Customers/"+mUid+"/nickname/")
            mdataBase.setValue(nickName)
            mdataBase = FirebaseDatabase.getInstance().getReference("/Customers/"+mUid+"/totalpay/")
            mdataBase.setValue("0")
            mdataBase = FirebaseDatabase.getInstance().getReference("/Customers/"+mUid+"/ratingurl/")
            mdataBase.setValue("/images/ClientLevel/silver.PNG")
            //shopinfoActivity로 넘기기
            var intent = Intent(this@LogindetailActivity, ShopInfoActivity::class.java)
            intent.putExtra("MenuArr", menuArr)
            intent.putExtra("ShopKey", shopKey)
            intent.putExtra("ShopName",shopName)
            intent.putExtra("ShopRating",shopRating.toFloat())
            intent.putExtra("Address", address)
            intent.putExtra("AccountHolder", accountHolder)
            intent.putExtra("Bank", bank)
            intent.putExtra("BankID", bankID)
            finish()
            startActivity(intent)
        }
        builder.setNegativeButton(
            "아니오"
        ){dialog, which ->

        }
        builder.show()
    }
    private fun checkDialog(){
        var builder = AlertDialog.Builder(this@LogindetailActivity)
        builder.setTitle("오류!")
        builder.setMessage("닉네임과 전화번호를 입력해주세요!!")
        builder.setPositiveButton(
            "확인"
        ){dialog, which ->

        }
        builder.show()
    }

    override fun onBackPressed() {
        var builder = AlertDialog.Builder(this@LogindetailActivity)
        builder.setMessage("아직 회원가입이 완료되지 않았습니다. 돌아가시겠습니까?")
        builder.setPositiveButton(
            "예"
        ) { dialog, which ->
            var intent = Intent(this@LogindetailActivity, ShopInfoActivity::class.java)
            intent.putExtra("MenuArr", menuArr)
            intent.putExtra("ShopKey", shopKey)
            intent.putExtra("ShopName",shopName)
            intent.putExtra("ShopRating",shopRating.toFloat())
            intent.putExtra("Address", address)
            intent.putExtra("AccountHolder", accountHolder)
            intent.putExtra("Bank", bank)
            intent.putExtra("BankID", bankID)
            finish()
            startActivity(intent)
        }
        builder.setNegativeButton(
            "아니오"
        ){dialog, which ->


        }
        builder.show()
    }

}
