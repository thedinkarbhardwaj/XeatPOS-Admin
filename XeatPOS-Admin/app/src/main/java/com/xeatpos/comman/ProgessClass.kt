package com.xeatpos.comman

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.widget.Button
import androidx.core.app.ActivityCompat.finishAffinity
import com.xeatpos.R


object ProgessClass {
    lateinit var dialog: Dialog
    lateinit var sharedPreferenceData: SharedPreferenceData
    fun Context.showDialog() {
        dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.progress_view)
        dialog.show()

    }

    fun Context.dismiss() {
        dialog.dismiss()
    }


//    fun Context.popUpUnAuthorised() {
//        dialog = Dialog(this)
//        sharedPreferenceData = SharedPreferenceData.getInstance(this)!!
//
//        var btn_ok: Button
//        dialog!!.setContentView(R.layout.cus_unauthorised_popup)
//        btn_ok = dialog!!.findViewById(R.id.btn_un)
//        btn_ok.setOnClickListener {
//            dialog!!.dismiss()
//            sharedPreferenceData.clearSharedPrefrence()
//            val intent=Intent(this,LoginActivity::class.java)
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//            startActivity(intent)
//            finishAffinity(this as Activity)
//
//        }
//        dialog!!.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        dialog!!.setCancelable(false)
//        dialog!!.setCanceledOnTouchOutside(false)
//        dialog!!.show()
//    }
}