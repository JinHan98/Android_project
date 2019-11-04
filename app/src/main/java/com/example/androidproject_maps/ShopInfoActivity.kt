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
        val shopKey = intent.extras.getString("ShopKey")

        var adapter = MainListAdapter(this,menuArr)
        var list : ListView = findViewById(R.id.mainListView)
        list.setAdapter(adapter)
        /*
        // Reference to an image file in Cloud Storage
        val storageShopImgRef = FirebaseStorage.getInstance().reference

        // ImageView in your Activity
        val imageView = findViewById<ImageView>(R.id.imageView01)

        storageShopImgRef.child(shopKey+"/ShopImage/").downloadUrl.addOnSuccessListener {
            imageView.setImageURI(it)
        }.addOnFailureListener {
            // Handle any errors
        }
        */



        orderbt.setOnClickListener{
            val orderintent = Intent(this, Order::class.java)
            orderintent.putExtra("MenuArr",menuArr)
            orderintent.putExtra("ShopKey",shopKey)
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
