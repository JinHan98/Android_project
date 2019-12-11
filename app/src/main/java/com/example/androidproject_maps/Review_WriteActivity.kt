package com.example.androidproject_maps

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_review__write.*
import java.sql.Time
import java.text.SimpleDateFormat


private lateinit var DB : DatabaseReference
class Review_WriteActivity : AppCompatActivity() {
    private lateinit var orderkey : String
    private lateinit var photoURI : Uri
    private lateinit var uid : String
    private lateinit var shopKey : String
    private var isAddImg = false
    private lateinit var database : DatabaseReference
    private lateinit var photoUrlForUpload : String
    private lateinit var shopName : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review__write)
        orderkey = intent.extras.getString("OrderKey")
        uid = intent.extras.getString("Uid")
        shopKey = intent.extras.getString("ShopKey")
        shopName = intent.extras.getString("ShopName")
        shop_name.text =  shopName
        addImgBt.setOnClickListener{
            selectAlbum()
        }

        finishBt.setOnClickListener{
            if(TextUtils.isEmpty(review_edit.text)){//리뷰를 작성했는가?
                checkDialog("리뷰를 작성해주세요 !")
            }
            else if(!isAddImg){//리뷰에 이미지를 넣었는가?
                checkDialog("리뷰 사진을 추가헤주세요 ㅠㅠ")
            }
            else{
                //Storage에 사진올리고 DB에쓰고
                uploadReview()
            }
        }

    }
    fun selectAlbum(){
        //앨범 열기
        var intent = Intent(Intent.ACTION_PICK)
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE)
        intent.setType("image/*")
        startActivityForResult(intent,1)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode != RESULT_OK){
            return
        }
        if(data != null){
            try{
                photoURI = data.data
                var bitmap = MediaStore.Images.Media.getBitmap(contentResolver,photoURI)
                reviewImgView.setImageBitmap(bitmap)
                isAddImg = true
            }catch(e : Exception){
                e.printStackTrace()
                Log.v("알림","앨범에서 가져오기 에러")
            }
        }
    }
    private fun checkDialog(message : String){
        var builder = AlertDialog.Builder(this@Review_WriteActivity)
        builder.setTitle(message)
        builder.setPositiveButton(
            "확인"
        ){dialog, which ->

        }
        builder.show()

    }
    private fun finishDialog(){
        var builder = AlertDialog.Builder(this@Review_WriteActivity)
        builder.setMessage("리뷰 작성 완료 !!\n소중한 리뷰 감사합니다 ~")
        builder.setPositiveButton(
            "확인"
        ){dialog, which ->
            //Db에 올리기
            database = FirebaseDatabase.getInstance().getReference("Customers/")
            val valeventlistener = object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    for (snapshot: DataSnapshot in p0.children) {
                        if(snapshot.key.equals(uid)) {
                            val value = snapshot.getValue(Customer::class.java)
                            if (value != null) {


                                var now = System.currentTimeMillis()
                                var timenow = Time(now)
                                var ordertimeDataFomat = SimpleDateFormat("yyyy년 MM월 dd일")
                                var reviewday = ordertimeDataFomat.format(timenow)

                                DB = FirebaseDatabase.getInstance().getReference("/shops/"+shopKey+"/")
                                writeNewReview(value.nickname,uid,review_rating.rating,value.ratingurl,photoUrlForUpload,review_edit.text.toString(),
                                    shopKey,reviewday,orderkey)
                                DB = FirebaseDatabase.getInstance().getReference("/Customers/"+uid +"/")
                                writeNewReview(value.nickname,uid,review_rating.rating,value.ratingurl,photoUrlForUpload,review_edit.text.toString(),
                                    shopKey,reviewday,orderkey)

                                //oder의 isreviewwrite를 true 바꿔야함. Customers에있는 order만 바꿔주면됨 어차피 MyOrderAdapter에서 얘만 검사함.
                                DB = FirebaseDatabase.getInstance().getReference("/Customers/"+uid +"/orders/"+orderkey+"/iswritereview")
                                DB.setValue(true)

                                //shop의 rating도 업데이트.
                                DB = FirebaseDatabase.getInstance().getReference("/shops/")
                                val valeventlistener2 = object : ValueEventListener {
                                    override fun onDataChange(p0: DataSnapshot) {
                                        for (snapshot: DataSnapshot in p0.children) {
                                            if(snapshot.key.equals(shopKey)) {
                                                val value2 = snapshot.getValue(Shop::class.java)
                                                if (value2 != null) {
                                                    var db = FirebaseDatabase.getInstance().getReference("/shops/"+shopKey+"/rating/")

                                                    val newRating = (value2.rating.toDouble()*value2.review_count.toDouble()+review_rating.rating.toDouble())/
                                                            (value2.review_count.toDouble()+1.toDouble())

                                                    db.setValue(newRating.toFloat())
                                                    var db2 = FirebaseDatabase.getInstance().getReference("/shops/"+shopKey+"/review_count/")
                                                    var new_reviewcount = value2.review_count.toInt()+1
                                                    db2.setValue(new_reviewcount.toString())


                                                }
                                            }
                                        }
                                    }

                                    override fun onCancelled(p0: DatabaseError) {
                                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                                    }
                                }
                                DB.addListenerForSingleValueEvent(valeventlistener2)

                            }
                        }
                    }
                }
                override fun onCancelled(p0: DatabaseError) {

                }
            }
            database.addListenerForSingleValueEvent(valeventlistener)
            finish()
            onBackPressed()
        }
        builder.show()
    }
    private fun uploadReview(){
        var storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://androidproject-49d96.appspot.com/images/"+shopKey+"/Reviews")
        var uploadTask : UploadTask

        uploadTask = storageRef.child(orderkey).putFile(photoURI)
        var failureListener = object : OnFailureListener {
            override fun onFailure(p0: java.lang.Exception) {
                Log.v("알림","사진 업로드 실패")
                p0.printStackTrace()
            }
        }
        var successListener = object : OnSuccessListener<UploadTask.TaskSnapshot> {
            override fun onSuccess(p0: UploadTask.TaskSnapshot?) {
                if(p0 != null) {
                    photoUrlForUpload = p0.storage.path
                    finishDialog()
                }
            }
        }
        uploadTask.addOnFailureListener(failureListener).addOnSuccessListener(successListener)
    }
    private fun writeNewReview(client_name : String , client_uid : String,rating : Float,client_rating_url: String,photourl: String,review_text: String,shopKey : String,time : String,orderKey: String) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously


        if (orderKey == null) {
            Log.w(ContentValues.TAG, "Couldn't get push key for reviews")
            return
        }

        val Review = Review(client_name, client_uid, rating ,client_rating_url,photourl,review_text,shopKey,time)
        val reviewValues = Review.toMap()

        val childUpdates = HashMap<String, Any>()
        childUpdates["/reviews/$orderKey"] = reviewValues


        DB.updateChildren(childUpdates)
    }
}
