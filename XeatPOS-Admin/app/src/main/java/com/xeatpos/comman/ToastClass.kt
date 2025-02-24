package com.xeatpos.comman

import android.app.Activity
import android.content.Context
import android.widget.Toast

object ToastClass {
    fun Context.toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


}