package com.xeatpos.data

class ResponseOrderHistoryDetails {

    data class OrderHistoryDetails(
        val `data`: Data,
        val message: String,
        val status: String
    )

    data class Data(
        val service_charges: String,
        val delivery_charges: String,
        val vat_charges: String,
        val discount_rate: String,
        val driver_id: String,
        val driver_name: String,
        val driver_phone: String,
        val driver_pic: String,
        val instruction: String,
        val order_at: String,
        val order_id: Int,
        val order_item_data: List<OrderItemData>,
        val order_type: String,
        val driver_type: String,
        val orderby: String,
        val payment_mode: String,
        val phone: String,
        val rating: String,
        val res_id: String,
        val status: String,
        val total_amount: String,
        val user_id: Int
    )
    data class OrderItemData(
        val add_on_items: List<AddOnItem>,
        val count: Int,
        val id: Int,
        val is_add_on: Int,
        val menu_item_id: Int,
        val menu_item_name: String,
        val order_id: Int,
        val price: String,
        val total_price: String
    )

    data class AddOnItem(
        val id: String,
        val name: String,
        val price: String
    )

}