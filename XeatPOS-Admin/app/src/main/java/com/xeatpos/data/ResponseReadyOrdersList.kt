package com.xeatpos.data

class ResponseReadyOrdersList {

    data class Ready(
        val message: String,
        val ready_order: List<ReadyOrder>,
        val status: String
    )

   /* data class ReadyOrder(
        val order_at: String,
        val order_id: Int,
        val order_type: String,
        val orderby: String,
        val phone: String,
        val status: String,
        val driver_name: String,
        val driver_pic: String,
        val driver_phone: String,
        val total_amount: String,
        val user_id: Int,

        )*/

    data class ReadyOrder(
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