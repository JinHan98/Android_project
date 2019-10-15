package com.example.androidproject_maps



class Shopinfo(){
    var name : String = ""
    var menuArr : ArrayList<MenuFood> = arrayListOf()
    constructor(name : String) : this(){
        this.name = name
    }
}