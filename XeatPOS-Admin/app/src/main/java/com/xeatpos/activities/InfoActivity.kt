package com.xeatpos.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.xeatpos.R
import com.xeatpos.databinding.ActivityInfoBinding
import com.xeatpos.prefs
import android.content.SharedPreferences




class InfoActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var binding: ActivityInfoBinding

    @SuppressLint("StringFormatInvalid")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_info)

        val prefsToken = getSharedPreferences(
            "pos", Context.MODE_PRIVATE
        )
        init()
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(
                        "====",
                        "Fetching FCM registration token failed",
                        task.exception
                    )
                    return@OnCompleteListener
                }

                // Get new FCM registration token
                val token = task.result
                prefsToken.edit().putString("token",token).apply()
                prefs.fcmToken = token

                // Log and toast
                val msg = getString(R.string.token, token)
                Log.d("Token: ", msg)
                //  Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            })


    }

    fun init() {

        binding.btnInfoLogin.setOnClickListener(this)

    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btn_info_login -> {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }

        }

    }
}