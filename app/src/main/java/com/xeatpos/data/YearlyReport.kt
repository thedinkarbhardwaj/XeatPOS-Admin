package com.xeatpos.response.yearly

data class YearlyReport(
    val message: String,
    val status: String,
    val yearly_orders: List<YearlyOrder>
)