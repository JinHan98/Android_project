package com.example.androidproject_maps

import java.io.Serializable

class MenuFood( )  : Serializable{
    var name : String = ""
    var price : String = ""
    var photourl : String = ""
    var amounts : String = ""
    constructor(name: String,price:String,photo:String):this(){
        this.name = name
        this.price = price
        this.photourl = photo
    }
}
