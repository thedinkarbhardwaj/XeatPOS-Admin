package com.xeatpos.response.daily

data class DailyReport(
    val daily_orders: List<DailyOrder>,
    val message: String,
    val status: String
)