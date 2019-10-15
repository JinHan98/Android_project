package com.example.androidproject_maps

import java.io.Serializable

class MenuFood( )  : Serializable{
    var name : String = ""
    var price : String = ""
    var photo : String = ""
    constructor(name: String,price:String,photo:String):this(){
        this.name = name
        this.price = price
        this.photo = photo
    }
}