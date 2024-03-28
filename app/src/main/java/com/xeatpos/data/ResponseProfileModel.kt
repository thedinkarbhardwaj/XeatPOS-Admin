package com.xeatpos.data

class ResponseProfileModel {

    data class Profile (var `data`: Data,
                           var message: String,
                           var status: String)

    data class Data(
        val id: Int,
        val rest_name: String,
        val online: String,
        val image: String,
        val contact_number: String,
        val countryCode: String,
        val location: String,
        val rest_type: Int,
        val r_lat: String,
        val r_long: String
    )

}