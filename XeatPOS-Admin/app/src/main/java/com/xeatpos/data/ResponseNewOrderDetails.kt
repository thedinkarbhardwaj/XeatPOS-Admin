package com.xeatpos.data

import java.io.Serializable

class ResponseNewOrderDetails{

    data class NewOrderDetails(
        val `data`: Data,
        val message: String,
        val status: String
    ) : Serializable

    data class Data(
        val order_at: String,
        val service_charges: String,
        val delivery_charges: String,
        val vat_charges: String,
        val discount_rate: String,
        val website_qrcode: String,
        val instruction: String,
        val order_id: Int,
        val order_item_data: List<OrderItemData>,
        val order_type: String,
        val driver_type: String,
        val payment_mode: String,
        val orderby: String,
        val total_amount: String,
        val address: String,
        val phone: String,
        val restaurant_name: String,
        val user_id: Int,
        val schedule_type: String,
        val schedule_date: String,
        val schedule_time: String,
        val small_charges: String
    ) : Serializable

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
    ) : Serializable

    data class AddOnItem(
        val id: String,
        val name: String,
        val price: String
    ) : Serializable

}