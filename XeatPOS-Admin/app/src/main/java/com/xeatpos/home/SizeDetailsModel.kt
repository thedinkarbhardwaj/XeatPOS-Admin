package com.xeatpos.home

class SizeDetailsModel {

    data class SizeDetails(var data : List<Data>,
                           var message: String,
                           var status: String)

    data class Data(
        val title: String,
        val menu_id: Int,
        val id: Int,
        val is_required: String,
        val is_size: String,
        val selection: List<Selection>
    )

    data class Selection(
        val  name: String,
        val  id: Int,
        val  variant_size: String,
        val  variant_price: String

    )

}