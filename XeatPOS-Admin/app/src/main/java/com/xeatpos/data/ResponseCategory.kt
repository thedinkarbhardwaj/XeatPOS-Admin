package com.xeatpos.data

class ResponseCategory {

    data class Categories (var `data`: List<Data>,
                var message: String,
                var status: String)


    data class Data(
        val c_name: String,
        val id: Int,
        val image: String,
        val r_id: Int,
        val total_items: Int
    )
}