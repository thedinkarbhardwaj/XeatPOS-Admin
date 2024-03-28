package com.xeatpos.data

import java.io.Serializable

class ResponsePreparingOrderDetails {
    data class PreparingOrders(
        val message: String,
        val preparing_order_details: PreparingOrderDetails,
        val status: String
    ): Serializable
    data class PreparingOrderDetails(
        val driver_id: String,
        val driver_name: String,
        val driver_phone: String,
        val driver_pic: String,
        val instruction: String,
        val payment_mode: String,
        val order_id: Int,
        val order_item_data: List<OrderItemData>,
        val order_type: String,
        val driver_type: String,
        val orderby: String,
        val order_at: String,
        val phone: String,
        val status: String,
        val total_amount: String,
        val vat_charges: String,
        val delivery_charges: String,
        val service_charges: String,
        val discount_rate: String,
        val address: String,
        val website_qrcode: String,
        val restaurant_name: String,
        val user_id: Int,
        val small_charges: String
    ): Serializable

    data class OrderItemData(
        val add_on_items: List<ResponseNewOrderDetails.AddOnItem>,
        val count: Int,
        val id: Int,
        val is_add_on: Int,
        val menu_item_id: Int,
        val menu_item_name: String,
        val order_id: Int,
        val price: String,
        val total_price: String
    ): Serializable
    data class AddOnItem(
        val id: String,
        val name: String,
        val price: String
    ): Serializable



}