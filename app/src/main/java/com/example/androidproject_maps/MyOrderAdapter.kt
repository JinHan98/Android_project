package com.example.androidproject_maps

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView

class MyOrderAdapter(val context: Context, val orderList: ArrayList<OrderInfo>, val orderMenuList : ArrayList<ArrayList<OrderMenu>>,
                     val uid : String): BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = LayoutInflater.from(context).inflate(R.layout.activity_review_orderlist,null)
        val shopName = view.findViewById<TextView>(R.id.shopname)
        val orderMenu = view.findViewById<TextView>(R.id.ordermenu)
        val orderAmount = view.findViewById<TextView>(R.id.orderamount)
        val reviewBt = view.findViewById<Button>(R.id.reviewbt)
        val orderDay = view.findViewById<TextView>(R.id.ordertime)
        val reverse_idx = orderList.size -1 - position//가장 최근것이 맨위로 올라가기 위해서
        shopName.text = orderList.get(reverse_idx).shopname
        orderDay.text = orderList.get(reverse_idx).orderday
        var allMenuInfo  =""
        for(j in 0..orderMenuList.get(reverse_idx).size-1) {
            allMenuInfo =
                allMenuInfo + orderMenuList.get(reverse_idx).get(j).food_name + " " + orderMenuList.get(reverse_idx).get(j).amounts + "개 "
        }
        orderMenu.text = allMenuInfo

        orderAmount.text = orderList.get(reverse_idx).pay
        reviewBt.setOnClickListener{
            if(orderList.get(reverse_idx).iswritereview){
                //리뷰가 써져있으므로 해당리뷰 띄우기
                var intent = Intent(context,Review_ReadActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.putExtra("ShopName",orderList.get(reverse_idx).shopname)
                intent.putExtra("Uid",uid)
                intent.putExtra("OrderKey",orderList.get(reverse_idx).mykey)
                context.startActivity(intent)
            }
            else{
                //리뷰가 안써져있으므로 리뷰쓰러가기
                var intent = Intent(context,Review_WriteActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.putExtra("Uid",uid)
                intent.putExtra("OrderKey",orderList.get(reverse_idx).mykey)
                intent.putExtra("ShopKey",orderList.get(reverse_idx).shopKey)
                intent.putExtra("ShopName",orderList.get(reverse_idx).shopname)
                context.startActivity(intent)
            }
        }


        return view


    }

    override fun getItem(position: Int): Any {
        return orderList[orderList.size-1-position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return orderList.size
    }
}