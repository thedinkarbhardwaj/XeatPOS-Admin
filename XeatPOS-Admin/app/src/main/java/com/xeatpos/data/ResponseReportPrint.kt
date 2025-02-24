package com.xeatpos.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

class ResponseReportPrint {

    data class ReportPrint(
        val message: String,
        val orders: Orders,
        val status: String
    )

    @Parcelize
    data class Orders(
        val amount: String,
        val delivery_fees: String,
        val total_delivery_order: Int,
        val total_order: Int,
        val total_pickup_order: Int,
        val total_sales: String,
        val restaurant_name: String,
        val admin_commision: String,
        val website_qrcode: String
    ) : Parcelable

}