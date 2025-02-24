package com.xeatpos.data

class ResponsePickedOrders {

    data class PickedOrdersRoot(
        val complete_order: List<CompleteOrder>,
        val message: String,
        val status: String
    )

    data class CompleteOrder(
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