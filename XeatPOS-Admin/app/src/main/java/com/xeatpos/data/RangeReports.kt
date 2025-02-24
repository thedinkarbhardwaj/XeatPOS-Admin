package com.xeatpos.response.rangereport

data class RangeReports(
    val message: String,
    val orders: List<Orders>,
    val status: String,
    val totalPages: Int
)