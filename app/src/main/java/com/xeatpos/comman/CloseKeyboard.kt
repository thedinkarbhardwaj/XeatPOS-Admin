package com.xeatpos.comman

import android.content.Context
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import java.lang.NullPointerException

object CloseKeyboard {

    fun Context.closeKey(view: View) {
        view.setOnTouchListener(View.OnTouchListener { v, event ->
            try {
                if (v.windowToken != null) {
                    val inputMethodManager =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                    inputMethodManager?.hideSoftInputFromWindow(v.windowToken, 0)
                    view.clearFocus()
                }
            } catch (e: NullPointerException) {
                e.printStackTrace()
            }
            true
        })
    }
}