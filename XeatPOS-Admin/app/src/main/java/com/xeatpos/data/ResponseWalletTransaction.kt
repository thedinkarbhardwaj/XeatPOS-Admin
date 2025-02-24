package com.xeatpos.data

class ResponseWalletTransaction {

    data class Wallet(
        val `data`: List<Data>,
        val message: String,
        val status: String,
        val totalbalance: String,
        val totalpages: Int
    )

    data class Data(
        val amount: String,
        val amount_from: String,
        val closing_balance: String,
        val created_at: String,
        val id: Int,
        val order_id: String,
        val res_id: String,
        val status: String,
        val transaction_type: String
    )

}