package com.xeatpos.home

import com.xeatpos.data.ResponseMenuDetailsModel

class MenuSizesModel {

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
        val discount : String,
        val discount_type : String,
        val r_id: Int,
        val status: String,
        val sub_item: Int,
        val menu_sizes: List<MenuSizes>,
        val type: String
    )

    data class MenuSizes(
        val  menu_size: String,
        val  menu_full_size: String,
        val  menu_size_price: String

    )


}