package com.xeatpos.data

class ResponseHistoryOrdersList {
    data class OrdersHistory(
        val `data`: List<Data>,
        val message: String,
        val status: String
    )

    data class Data(
        val discount_rate: String,
        val driver_id: String,
        val driver_name: String,
        val driver_phone: String,
        val driver_pic: String,
        val order_at: String,
        val order_id: Int,
        val order_type: String,
        val driver_type: String,
        val orderby: String,
        val phone: String,
        val status: String,
        val total_amount: String,
        val user_id: Int
    )

}