package com.xeatpos.data

class ResponseNewOrders {

    data class NewOrders(
        val `data`: List<Data>,
        val message: String,
        val status: String
    )

    data class Data(
        val order_at: String,
        val order_id: Int,
        val order_type: String,
        val orderby: String,
        val driver_type: Int,
        val driver_from: String,
        val status: String,
        val total_amount: String,
        val schedule_type: String,
        val schedule_date: String,
        val schedule_time: String
        //val today_order: String
    )

}