package com.xeatpos.activities

import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager

import android.content.IntentFilter
import android.content.Intent
import android.widget.Toast
import com.xeatpos.notifications.MyFirebaseMessagingService
import android.app.NotificationManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.view.View
import android.view.Window
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import com.xeatpos.R
import com.xeatpos.prefs
import android.view.LayoutInflater

import android.content.SharedPreferences
import android.util.Log

import android.widget.TextView

import android.widget.RadioGroup
import com.xeatpos.comman.ProgessClass.dismiss
import com.xeatpos.utils.Constants
import java.lang.String


open class BaseActivity : AppCompatActivity() {

    var flag: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    private val mMessageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val t = intent!!.getStringExtra("value1")
            val t1 = intent.getStringExtra("value2")

            Log.d("IntentDattttaaaa",t.toString() + "      Second --  " + t1.toString())

            //Toast.makeText(applicationContext, t, Toast.LENGTH_SHORT).show()
            if (t.equals("New Order Received")) {
                runOnUiThread {

                    showCustomAlert()

                }
            } else {

                runOnUiThread {

                }

            }

        }


    }


    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(applicationContext).registerReceiver(
            mMessageReceiver,
            IntentFilter("myFunction")
        )
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(applicationContext).unregisterReceiver(mMessageReceiver)
    }

    private fun NewOrderDialog() {

        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.setCanceledOnTouchOutside(false)
        dialog.setContentView(R.layout.new_order_dialog)
        val btnGoToNewOrder = dialog.findViewById(R.id.btnGoToNewOrder) as Button
        val btnCancelDialogNewOrder = dialog.findViewById(R.id.btnCancelDialogNewOrder) as Button

        btnGoToNewOrder.setOnClickListener {

            dialog.dismiss()

            val notify_manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notify_manager.cancelAll();

            startActivity(Intent(this, HomeActivity::class.java))
            finishAffinity()

        }

        btnCancelDialogNewOrder.setOnClickListener(View.OnClickListener {


            runOnUiThread {
                dialog.dismiss()
            }

            val handler = Handler()
            handler.postDelayed(Runnable { // Close dialog after 1000ms
                dialog.cancel()
            }, 500)

            val notify_manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notify_manager.cancelAll();

        })

        /*  btnCancelDialogNewOrder.setOnClickListener {

              dialog.dismiss()

              val notify_manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
              notify_manager.cancelAll();

              dialog.dismiss()

          }*/
        dialog.show()


    }

    private fun showCustomAlert() {

        val builder = AlertDialog.Builder(this)

        val inflater = layoutInflater
        val dialogView: View = inflater.inflate(R.layout.new_order_dialog, null)
        builder.setView(dialogView)

        val btnGoToNewOrder = dialogView.findViewById(R.id.btnGoToNewOrder) as Button
        val btnCancelDialogNewOrder =
            dialogView.findViewById(R.id.btnCancelDialogNewOrder) as Button


        val dialog = builder.create()

        if(dialog != null && dialog.isShowing){
            dialog.dismiss()
        }

        btnGoToNewOrder.setOnClickListener {

            flag = false
            try {
                if (Constants.player_ring.isPlaying) {
                    Constants.player_ring.stop()
                }
            } catch (e: java.lang.Exception) {
            }

            dialog.dismiss()

            dialog.cancel()

            val notify_manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notify_manager.cancelAll()

            startActivity(Intent(this, HomeActivity::class.java))
            finishAffinity()

        }

        btnCancelDialogNewOrder.setOnClickListener(View.OnClickListener {

            try {
                if (Constants.player_ring.isPlaying) {
                    Constants.player_ring.stop()
                }
            } catch (e: java.lang.Exception) {
            }
            flag = false

            dialog.dismiss()

            dialog.cancel()


            val notify_manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notify_manager.cancelAll();

            btnCancelDialogNewOrder.postDelayed(Runnable {

                dialog.dismiss()

            }, 500)

        })

        if (!flag)
            System.out.println("Dismiss Dialog")
        flag = true

        if (!dialog.isShowing) {
            dialog.show()
        }

    }


}