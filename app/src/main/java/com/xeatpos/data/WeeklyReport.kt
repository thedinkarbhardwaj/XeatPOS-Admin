package com.xeatpos.response.weekly

data class WeeklyReport(
    val message: String,
    val status: String,
    val weekly_orders: List<WeeklyOrder>
)