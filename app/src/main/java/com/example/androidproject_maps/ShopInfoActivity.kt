package com.example.androidproject_maps

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.android.synthetic.main.activity_shop_info.*


private lateinit var database: DatabaseReference
class ShopInfoActivity :AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop_info)
        val shop_name = intent.extras.getString("ShopName")
        val menuArr = intent.getSerializableExtra("MenuArr") as ArrayList<MenuFood>
        /*DB 레퍼런스 지정
        database = FirebaseDatabase.getInstance().getReference("shops/")*/


        var adapter = MainListAdapter(this,menuArr)
        var list : ListView = findViewById(R.id.mainListView)
        list.setAdapter(adapter)
        /*DB 데이터 빼오는 방법
        val valeventlistener = object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                for(snapshot : DataSnapshot in p0.children){
                    val value:Shop? = snapshot.getValue(Shop::class.java)
                    val key = snapshot.key.toString()
                    for(snapshot2 : DataSnapshot in snapshot.children){
                        if(snapshot2.key.toString().equals("menus")){
                            for(snapshot3 : DataSnapshot in snapshot2.children){
                                val menus : Foodmenu? = snapshot3.getValue(Foodmenu::class.java)
                                val foodname = menus?.food_name
                                val price = menus?.price
                                foodmenuList.add(MenuFood(foodname!!,price!!,"seapasta"))
                                foodMenuAdapter.notifyDataSetChanged()
                            }
                        }
                    }

                }
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }
        database.addValueEventListener(valeventlistener)
        */
        orderbt.setOnClickListener{
            val orderintent = Intent(this, Order::class.java)
            orderintent.putExtra("MenuArr",menuArr)
            startActivity(orderintent)
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
    var starCount: Int = 0,
    var stars: MutableMap<String, Boolean> = HashMap()
) {

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "food_name" to food_name ,
            "price" to price,
            "starCount" to starCount,
            "stars" to stars
        )
    }
}
private fun writeNewfoodmenu(food_name: String, price: String, shopKey : String) {
    // Create new post at /user-posts/$userid/$postid and at
    // /posts/$postid simultaneously
    val key = database.child("shops/menus").push().key
    if (key == null) {
        Log.w(ContentValues.TAG, "Couldn't get push key for menus")
        return
    }

    val foodmenu = Foodmenu(food_name, price)
    val foodmenuValues = foodmenu.toMap()

    val childUpdates = HashMap<String, Any>()
    childUpdates["/shops/"+shopKey+"/menus/$key"] = foodmenuValues


    database.updateChildren(childUpdates)
}
