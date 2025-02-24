package com.xeatpos.data

class ResponsePickedOrdersDetails {

    data class PickedOrdersDetails(
        val complete_order_data: CompleteOrderData,
        val message: String,
        val status: String
    )

    data class CompleteOrderData(
        val couponApplied: String,
        val service_charges: String,
        val delivery_charges: String,
        val vat_charges: String,
        val discount_rate: String,
        val driver_id: String,
        val instruction: String,
        val order_at: String,
        val order_id: Int,
        val order_item_data: List<OrderItemData>,
        val order_type: String,
        val res_id: String,
        val status: String,
        val total_amount: String,
        val user_id: Int,
        val username: String,
        val driver_pic: String,
        val driver_name: String,
        val driver_phone: String,
        val orderby: String,
        val payment_mode: String,
        val small_charges: String
    )

    data class OrderItemData(
        val add_on_items: List<AddOnItems>,
        val count: Int,
        val id: Int,
        val is_add_on: Int,
        val menu_item_id: Int,
        val menu_item_name: String,
        val order_id: Int,
        val price: String,
        val total_price: String
    )

    data class AddOnItems(
        val id: String,
        val name: String,
        val price: String
    )
}