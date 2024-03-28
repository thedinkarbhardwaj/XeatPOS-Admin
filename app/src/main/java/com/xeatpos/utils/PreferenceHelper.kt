package com.xeatpos.utils

import android.content.Context
import android.content.SharedPreferences

class PreferenceHelper(context: Context)
{
    private var TOKEN = "token"
    private var RESTAURANT_NAME = "rest_name"
    private var TYPE = "type"
    private var RESTAURANT_IMAGE = "rest_image"
    private var FCM_TOKEN = "fcm_token"
    private var NEW_ORDER_ID = "new_order_id"
    private var REST_ID = "rest_id"

    private val preferences: SharedPreferences
              = context.getSharedPreferences("xeat_pos", Context.MODE_PRIVATE)

    var token: String
        get() = preferences.getString(TOKEN,"").toString()
        set(value) = preferences.edit().putString(TOKEN, value).apply()

    var restaurantImage: String
        get() = preferences.getString(RESTAURANT_IMAGE,"").toString()
        set(value) = preferences.edit().putString(RESTAURANT_IMAGE, value).apply()

    var restaurantName: String
        get() = preferences.getString(RESTAURANT_NAME,"").toString()
        set(value) = preferences.edit().putString(RESTAURANT_NAME, value).apply()

    var type: String
        get() = preferences.getString(TYPE,"").toString()
        set(value) = preferences.edit().putString(TYPE, value).apply()


    var fcmToken: String
        get() = preferences.getString(FCM_TOKEN,"").toString()
        set(value) = preferences.edit().putString(FCM_TOKEN, value).apply()

    var newOrderId: String
        get() = preferences.getString(NEW_ORDER_ID,"").toString()
        set(value) = preferences.edit().putString(NEW_ORDER_ID, value).apply()

    var rest_id: String
        get() = preferences.getString(REST_ID,"").toString()
        set(value) = preferences.edit().putString(REST_ID, value).apply()

    fun logout(){
        preferences.edit().clear().apply()
    }
}