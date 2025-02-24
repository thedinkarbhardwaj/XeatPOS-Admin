package com.xeatpos.data

class ResponseModel {

    data class Login(var status: String, var message: String, var token: String, var rest_name: String, var type: String, var image: String)

    data class Menus( var status : String, var message : String, var data : List<Data>)

    data class Data(var id : Int,
                          var r_id : Int,
                          var name : String,
                          var franch_name : String,
                          var type : String,
                          var image : String,
                          var f_price : String,
                          var category : String,
                          var status : String,
                          var ingredients : String)



}