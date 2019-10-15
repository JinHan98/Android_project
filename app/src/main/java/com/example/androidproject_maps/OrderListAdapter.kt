package com.example.androidproject_maps

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class OrderListAdapter(val context: Context, val menuList : ArrayList<MenuFood>) : BaseAdapter() {
    override fun getCount(): Int {
        return menuList.size
    }

    override fun getItem(position: Int): Any {
        return menuList.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view : View = LayoutInflater.from(context).inflate(R.layout.order_item,null)
        view.findViewById<TextView>(R.id.foodnameTxt).text = menuList.get(position).name
        view.findViewById<TextView>(R.id.foodpriceTxt).text = menuList.get(position).price
        return view
    }
}