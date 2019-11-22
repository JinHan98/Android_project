package com.example.androidproject_maps

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView





class OrderListAdapter(val context: Context, val menuList : ArrayList<MenuFood>, var amounts : Array<Int>) : BaseAdapter() {
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
        val downbt = view.findViewById<Button>(R.id.downbt)
        val upbt = view.findViewById<Button>(R.id.upbt)
        view.findViewById<TextView>(R.id.order_amounts).text = amounts[position].toString()
        downbt.setOnClickListener({
            if(amounts[position] > 0){
                amounts[position] = amounts[position] - 1
                notifyDataSetChanged()
            }
        })
        upbt.setOnClickListener {
            amounts[position] = amounts[position] + 1
            notifyDataSetChanged()
        }
        return view

    }



}