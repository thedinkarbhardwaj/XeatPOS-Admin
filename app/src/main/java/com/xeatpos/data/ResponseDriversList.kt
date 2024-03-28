package com.xeatpos.data

class ResponseDriversList {

    data class Drivers(
        val `data`: List<Data>,
        val message: String,
        val status: String
    )

    data class Data(
        val contact_num: String,
        val device_token: String,
        val device_type: String,
        val driver_name: String,
        val driver_pic: String,
        val id: Int,
        val password: String,
        val res_id: Int,
        val vehicle_name: String,
        val vehicle_num: String,
        val vehicle_pic: String
    )

}