package com.xeatpos.model

import android.os.Parcelable
import com.xeatpos.data.ResponseReportPrint
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RangeReportModel(
    val amount: String,
    val delivery_fees: String,
    val total_delivery_order: String,
    val total_order: String,
    val total_pickup_order: String,
    val total_sales: String,
) : Parcelable
