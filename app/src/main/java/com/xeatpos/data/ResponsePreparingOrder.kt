package com.xeatpos.data

class ResponsePreparingOrder {

    data class Preparing(
        val message: String,
        val preparing_order: List<PreparingOrder>,
        val status: String
    )

    data class PreparingOrder(
        val order_at: String,
        val order_id: Int,
        val order_type: String,
        val driver_type: String,
        val orderby: String,
        val phone: String,
        val status: String,
        val driver_name: String,
        val driver_pic: String,
        val driver_phone: String,
        val total_amount: String,
        val user_id: Int,

        )

}