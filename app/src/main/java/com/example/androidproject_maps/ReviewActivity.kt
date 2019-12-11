package com.example.androidproject_maps

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_review.*

class ReviewActivity : AppCompatActivity() {
    private lateinit var uid : String
    private lateinit var mDataBaseReference: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review)
        uid = intent.extras.getString("Uid")
        mDataBaseReference = FirebaseDatabase.getInstance().getReference("Customers/"+uid+"/orders/")

        val valeventlistener = object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                var orderList = arrayListOf<OrderInfo>()
                var allOrderMenuList = arrayListOf<ArrayList<OrderMenu>>()
                for (snapshot: DataSnapshot in p0.children) {
                    val value = snapshot.getValue(OrderInfo::class.java)

                    if (value != null) {
                        orderList.add(value)
                        for (snapshot2: DataSnapshot in snapshot.children) {
                            var orderMenuList = arrayListOf<OrderMenu>()
                            if (snapshot2.key.toString().equals("ordermenus")) {
                                for (snapshot3: DataSnapshot in snapshot2.children) {
                                    val value2 = snapshot3.getValue(OrderMenu::class.java)
                                    if(value2 != null) {
                                        orderMenuList.add(value2)
                                    }
                                }
                                allOrderMenuList.add(orderMenuList)
                            }
                        }
                    }
                }
                var adapter = MyOrderAdapter(this@ReviewActivity,orderList,allOrderMenuList,uid)
                review_list.adapter = adapter
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }
        mDataBaseReference.addValueEventListener(valeventlistener)
    }
}
