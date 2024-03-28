package com.xeatpos.utils

import android.media.MediaPlayer
import android.media.Ringtone

class Constants {

    companion object {
        const val MENU_NAME = "menu_name"
        const val MENU_ID = "menu_id"
        const val ORDER_ID = "order_id"
        const val ORDER_TYPE= "order_check"
        const val DRIVER_FROM = "driver_from"
        lateinit var player: MediaPlayer
        lateinit var player_ring: Ringtone
    }



}