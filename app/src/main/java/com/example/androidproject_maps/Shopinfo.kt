package com.example.androidproject_maps



class Shopinfo(){
    var name : String = ""
    var menuArr : ArrayList<MenuFood> = arrayListOf()
    var shopKey : String = ""
    constructor(name : String,shopKey : String) : this(){
        this.name = name
        this.shopKey = shopKey
    }
}