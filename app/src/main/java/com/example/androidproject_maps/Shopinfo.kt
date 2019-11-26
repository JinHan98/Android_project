package com.example.androidproject_maps



class Shopinfo(){
    var name : String = ""
    var menuArr : ArrayList<MenuFood> = arrayListOf()
    var photoUrl : String = ""
    var shopKey : String = ""
    var shopRate : Float = 0.toFloat()
    var latitude :Double =0.0
    var longitude :Double=0.0
    constructor(name : String,shopKey : String) : this(){
        this.name = name
        this.shopKey = shopKey
    }
}