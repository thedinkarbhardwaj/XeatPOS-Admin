package com.xeatpos.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ResponseMenuDetailsModel {

    data class MenuDetails(var `data`: Data,
                           var message: String,
                           var status: String)

    data class Data(
        val category: String,
        val created_at: String,
        val cuisine: String,
        val f_price: String,
        val franch_name: String,
        val id: Int,
        val image: String,
        val ingredients: String,
        val name: String,
        val r_id: Int,
        val status: String,
        val sub_item: Int,
        val sizes: List<Sizes>,
        val add_on_types: List<AddOnTypes>,
        val type: String
    )

    data class Sizes(
        val  item_size: String,
        val  size_fulltext: String,
        val  item_price: String

    )

    data class AddOnTypes(
        val name: String,
        val add_on: List<AddOn>,
    )

    data class AddOn(
        val name: String,
        val add_on_size: List<AddOnSize>,
    )
    data class AddOnSize(
        val item_size: String,
        val item_price: String
    )

}