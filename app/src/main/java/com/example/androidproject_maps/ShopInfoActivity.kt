package com.example.androidproject_maps


import android.content.ContentValues
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ListView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_shop_info.*


private lateinit var database: DatabaseReference
class ShopInfoActivity :AppCompatActivity() {

    private lateinit var mFirebaseauth: FirebaseAuth
    private lateinit var mAuthStateListener : FirebaseAuth.AuthStateListener
    private var loginStatus : Boolean = false
    private lateinit var uid : String
    private lateinit var shopKey : String
    private lateinit var shopName : String
    private lateinit var menuArr : ArrayList<MenuFood>
    private lateinit var shopRating : String
    private lateinit var bank : String
    private lateinit var bankID : String
    private lateinit var address : String
    private lateinit var accountHolder : String
    private lateinit var customer_phoneNum : String
    var reviewList : ArrayList<Review> = arrayListOf()
    var reviewAdapter = ReviewAdapter(this,reviewList)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop_info)

        shopKey = intent.extras.getString("ShopKey")
        shopName = intent.extras.getString("ShopName")
        menuArr = intent.getSerializableExtra("MenuArr") as ArrayList<MenuFood>
        shopRating = intent.extras.getFloat("ShopRating").toString()
        bank = intent.extras.getString("Bank")
        bankID = intent.extras.getString("BankID")
        address = intent.extras.getString("Address")
        accountHolder = intent.extras.getString("AccountHolder")

        mFirebaseauth = FirebaseAuth.getInstance()

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

        reviewbt.setOnClickListener {
            mainListView.visibility = View.INVISIBLE
            Linear_detail.visibility = View.INVISIBLE
            Linear_review.visibility = View.VISIBLE
            Review_ListView.adapter = reviewAdapter
            var db = FirebaseDatabase.getInstance().getReference("shops/" + shopKey + "/reviews")
            val valeventlistener = object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    reviewList.clear()
                    for (snapshot: DataSnapshot in p0.children) {
                        val review = snapshot.getValue(Review::class.java)
                        if (review != null) {
                            reviewList.add(review)
                        }
                    }
                    reviewAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }
            }
            db.orderByKey().limitToLast(20).addValueEventListener(valeventlistener)//reviews의 key값으로 정렬한후 가장큰(가장 최근)20개만 가져와라.
        }
        setLoginStatus()

        var adapter = MainListAdapter(this,menuArr,shopKey)
        var list : ListView = findViewById(R.id.mainListView)
        list.setAdapter(adapter)
        shop_name.text = shopName
        /* 상점 이미지 세팅*/
        val storage = FirebaseStorage.getInstance().reference
        var storageShopImgRef = storage.child("images").child(shopKey).child("ShopImg").child("foodtruck.png")
        /*메모리에 다운로드, 앱이 꺼지면 날라감*/
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
                orderintent.putExtra("ShopRating",shopRating)
                orderintent.putExtra("Address",address)
                orderintent.putExtra("Bank",bank)
                orderintent.putExtra("BankID",bankID)
                orderintent.putExtra("AccountHolder",accountHolder)
                orderintent.putExtra("CustomerPhoneNum",customer_phoneNum)
                finish()
                startActivity(orderintent)
            } else {
                //로그인 안되어있으면 로그인창으로 넘기기
                signinDialog()
            }
        }

        mypagebt.setOnClickListener{
            if(loginStatus) {
                var mypageintent = Intent(this@ShopInfoActivity, MypageActivity::class.java)
                mypageintent.putExtra("Uid",uid)
                startActivity(mypageintent)
            }
            else{
                signinDialog()
            }
        }
        var db = FirebaseDatabase.getInstance().getReference("shops/")
        val valeventlistener = object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                for (snapshot: DataSnapshot in p0.children) {
                    if(snapshot.key.equals(shopKey)) {
                        val value: Shop? = snapshot.getValue(Shop::class.java)
                        if (value != null) {
                            ratingBar2.rating = value.rating
                        }
                    }
                }
            }


            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }
        db.addValueEventListener(valeventlistener)



    }
    private fun signinDialog(){
       var builder = AlertDialog.Builder(this@ShopInfoActivity)
        builder.setMessage("회원가입이 필요합니다.\n회원가입 창으로 이동하시겠습니까?")
        builder.setPositiveButton(
            "예"
        ){dialog, which ->
            var loginintent = Intent(this@ShopInfoActivity, LoginActivity::class.java)
            loginintent.putExtra("MenuArr", menuArr)
            loginintent.putExtra("ShopKey", shopKey)
            loginintent.putExtra("ShopName",shopName)
            loginintent.putExtra("ShopRating",shopRating)
            loginintent.putExtra("Address",address)
            loginintent.putExtra("Bank",bank)
            loginintent.putExtra("BankID",bankID)
            loginintent.putExtra("AccountHolder",accountHolder)
            finish()
            startActivity(loginintent)
        }
        builder.setNegativeButton(
            "아니오"
        ){dialog, which ->

        }
        builder.show()
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
    private fun setLoginStatus(){
        mAuthStateListener = object : FirebaseAuth.AuthStateListener{
            override fun onAuthStateChanged(p0: FirebaseAuth) {
                if(p0.currentUser != null){//로그인이 되어있냐
                    loginStatus = true
                    uid = p0.uid!!//아이디 만들때 무조건 uid 넣을것기 때문에 널일수없음.
                    var db = FirebaseDatabase.getInstance().getReference("/Customers/")
                    val valeventlistener = object : ValueEventListener {
                        override fun onDataChange(p0: DataSnapshot) {
                            for (snapshot: DataSnapshot in p0.children) {
                                val value = snapshot.getValue(Customer::class.java)
                                if (value != null) {
                                    val valueKey = snapshot.key.toString()
                                    if (valueKey.equals(uid)) {
                                        customer_phoneNum = value.phonenum
                                    }
                                }
                            }
                        }

                        override fun onCancelled(p0: DatabaseError) {
                            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                        }
                    }
                    db.addListenerForSingleValueEvent(valeventlistener)
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
    var accountHolder : String = "",
    var address : String = "",
    var bank : String = "",
    var bankID : String = "",
    var shop_name: String = "",
    var latitude: String = "",//위도
    var longitude: String = "",//경도
    var rating: Float = 0.toFloat() ,
    var photourl : String = "",
    var ownerUid : String = "",
    var review_count : String = "0",
    var stars: MutableMap<String, Boolean> = HashMap()
) {

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "shop_name" to shop_name ,
            "latitude" to latitude,
            "longitude" to longitude,
            "photourl" to photourl,
            "rating" to rating,
            "ownerUid" to ownerUid,
            "stars" to stars
        )
    }
}
private fun writeNewshop(accountHolder: String,address: String,bank: String,bankID: String,shop_name: String, latitude: String, longitude: String, rating: Float, photourl: String,ownerUid: String) {
    // Create new post at /user-posts/$userid/$postid and at
    // /posts/$postid simultaneously
    val key = database.child("shops").push().key
    if (key == null) {
        Log.w(ContentValues.TAG, "Couldn't get push key for shops")
        return
    }

    val shop = Shop(accountHolder, address, bank, bankID,shop_name, latitude, longitude,rating,photourl,ownerUid)
    val shopValues = shop.toMap()

    val childUpdates = HashMap<String, Any>()
    childUpdates["/shops/$key"] = shopValues


    database.updateChildren(childUpdates)
}
@IgnoreExtraProperties
data class Foodmenu(
    var food_name: String? = "",
    var price: String? = "",//위도
    var photourl : String? = "",
    var starCount: Int = 0,
    var stars: MutableMap<String, Boolean> = HashMap()
) {

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "food_name" to food_name ,
            "price" to price,
            "imageUri" to photourl,
            "starCount" to starCount,
            "stars" to stars
        )
    }
}
private fun writeNewfoodmenu(food_name: String, price: String, shopKey : String,photourl : String) {
    // Create new post at /user-posts/$userid/$postid and at
    // /posts/$postid simultaneously
    val key = database.child("shops/menus").push().key
    if (key == null) {
        Log.w(ContentValues.TAG, "Couldn't get push key for menus")
        return
    }

    val foodmenu = Foodmenu(food_name, price,photourl)
    val foodmenuValues = foodmenu.toMap()

    val childUpdates = HashMap<String, Any>()
    childUpdates["/shops/"+shopKey+"/menus/$key"] = foodmenuValues


    database.updateChildren(childUpdates)
}
@IgnoreExtraProperties
data class Review (
    var client_name: String = "",
    var client_uid : String = "",
    var rating: Float = 0.toFloat() ,
    var client_rating_url : String = "",
    var photourl : String = "",
    var review_text : String = "",
    var shopKey : String = "",
    var time : String = "",
    var stars: MutableMap<String, Boolean> = HashMap()
) {

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "client_name" to client_name ,
            "client_uid" to client_uid,
            "rating" to rating,
            "client_rating_url" to client_rating_url,
            "photourl" to photourl,
            "review_text" to review_text,
            "shopKey" to shopKey,
            "time" to time
        )
    }
}
