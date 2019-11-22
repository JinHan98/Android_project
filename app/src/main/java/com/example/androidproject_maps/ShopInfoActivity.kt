package com.example.androidproject_maps


import android.content.ContentValues
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_shop_info.*


private lateinit var database: DatabaseReference
class ShopInfoActivity :AppCompatActivity() {

    private val RC_SIGN_IN: Int = 10
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var mFirebaseauth: FirebaseAuth
    private lateinit var mAuthStateListener : FirebaseAuth.AuthStateListener
    private lateinit var mDatabaseReference: DatabaseReference
    private var loginStatus : Boolean = false
    private lateinit var uid : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop_info)

        menubt.setOnClickListener{
            mainListView.visibility = View.VISIBLE
            Linear_detail.visibility = View.INVISIBLE
            Linear_review.visibility = View.INVISIBLE
        }

        shopdetailbt.setOnClickListener{
            mainListView.visibility = View.INVISIBLE
            Linear_detail.visibility = View.VISIBLE
            Linear_review.visibility = View.INVISIBLE
        }

        reviewbt.setOnClickListener{
            mainListView.visibility = View.INVISIBLE
            Linear_detail.visibility = View.INVISIBLE
            Linear_review.visibility = View.VISIBLE
        }


        val shopName = intent.extras.getString("ShopName")
        val menuArr = intent.getSerializableExtra("MenuArr") as ArrayList<MenuFood>
        val shopKey = intent.extras.getString("ShopKey")
        mFirebaseauth = FirebaseAuth.getInstance()
        initFBAuthState()
        var adapter = MainListAdapter(this,menuArr)
        var list : ListView = findViewById(R.id.mainListView)
        list.setAdapter(adapter)
        shop_name.text = shopName
        /* Reference to an image file in Cloud Storage*/
        val storage = FirebaseStorage.getInstance().reference
        var storageShopImgRef = storage.child("images").child("foodtruck.png")

        /*메모리에 다운로드 앱이 꺼지면 날라감*/

        var ONE_MEGABYTE : Long = 1024*1024
        storageShopImgRef?.getBytes(ONE_MEGABYTE).addOnCompleteListener{
            if(it.isSuccessful) {
            }
            imageView01.setImageBitmap(BitmapFactory.decodeByteArray(it.result!!,0,it.result!!.size))
            adapter.notifyDataSetChanged()
        }

        orderbt.setOnClickListener {
            if (loginStatus) {//로그인 되어있는지 확인하고 되어있으면 장바구니창으로
                var orderintent = Intent(this@ShopInfoActivity, Order::class.java)
                orderintent.putExtra("MenuArr", menuArr)
                orderintent.putExtra("ShopKey", shopKey)
                orderintent.putExtra("ShopName",shopName)
                orderintent.putExtra("uid", uid)
                startActivity(orderintent)
            } else {
                //로그인 안되어있으면 로그인창으로 넘기기
                Toast.makeText(this,"로그인이 되어있지 않습니다. 로그인창으로 넘어갑니다.",Toast.LENGTH_SHORT).show()
                Handler().postDelayed({},4000)
                var loginintent = Intent(this@ShopInfoActivity, LoginActivity::class.java)
                loginintent.putExtra("MenuArr", menuArr)
                loginintent.putExtra("ShopKey", shopKey)
                loginintent.putExtra("ShopName",shopName)
                startActivity(loginintent)
            }
        }


    }
    override fun onStart() {//oncreate 다음에 호출  액티비티가 사용자에게 보여지기 직전에 호출됨
        super.onStart()
        mFirebaseauth.addAuthStateListener(mAuthStateListener)
    }
    override fun onStop() {//onstart와 짝을이룸  사용자에게 액티비티가 보여지지 않을 때 호출됨.
        // 보통 객체의 null 체크후 값이 있을 경우 자원을 해제 할때 사용한다.
        super.onStop()
        if(mAuthStateListener != null){
            mFirebaseauth.removeAuthStateListener { mAuthStateListener }
        }
    }
    private fun initFBAuthState(){
        mAuthStateListener = object : FirebaseAuth.AuthStateListener{
            override fun onAuthStateChanged(p0: FirebaseAuth) {
                var message : String
                if(p0.currentUser != null){//로그인이 되어있냐
                    loginStatus = true
                    uid = p0.uid!!//아이디 만들때 무조건 uid 넣을것기 때문에 널일수없음.
                }
                else {
                    loginStatus = false
                }
            }
        }
    }
}


@IgnoreExtraProperties
data class Shop (
    var shop_name: String? = "",
    var latitude: String? = "",//위도
    var longitude: String? = "",//경도

    var starCount: Int = 0,
    var stars: MutableMap<String, Boolean> = HashMap()
) {

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "shop_name" to shop_name ,
            "latitude" to latitude,
            "longitude" to longitude,

            "starCount" to starCount,
            "stars" to stars
        )
    }
}
private fun writeNewshop(shop_name: String, latitude: String, longitude: String) {
    // Create new post at /user-posts/$userid/$postid and at
    // /posts/$postid simultaneously
    val key = database.child("posts").push().key
    if (key == null) {
        Log.w(ContentValues.TAG, "Couldn't get push key for shops")
        return
    }

    val shop = Shop(shop_name, latitude, longitude)
    val shopValues = shop.toMap()

    val childUpdates = HashMap<String, Any>()
    childUpdates["/shops/$key"] = shopValues


    database.updateChildren(childUpdates)
}
@IgnoreExtraProperties
data class Foodmenu(
    var food_name: String? = "",
    var price: String? = "",//위도
    var imageUri : String? = "",
    var starCount: Int = 0,
    var stars: MutableMap<String, Boolean> = HashMap()
) {

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "food_name" to food_name ,
            "price" to price,
            "imageUri" to imageUri,
            "starCount" to starCount,
            "stars" to stars
        )
    }
}
private fun writeNewfoodmenu(food_name: String, price: String, imageUri : String, shopKey : String) {
    // Create new post at /user-posts/$userid/$postid and at
    // /posts/$postid simultaneously
    val key = database.child("shops/menus").push().key
    if (key == null) {
        Log.w(ContentValues.TAG, "Couldn't get push key for menus")
        return
    }

    val foodmenu = Foodmenu(food_name, price,imageUri)
    val foodmenuValues = foodmenu.toMap()

    val childUpdates = HashMap<String, Any>()
    childUpdates["/shops/"+shopKey+"/menus/$key"] = foodmenuValues


    database.updateChildren(childUpdates)
}
