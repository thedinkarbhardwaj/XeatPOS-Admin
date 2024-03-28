package com.necvotingmain.activities

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.Observer
import com.xeatpos.R
import com.xeatpos.activities.BaseActivity
import com.xeatpos.activities.HomeActivity
import com.xeatpos.activities.InfoActivity
import com.xeatpos.prefs

class SplashActivity : BaseActivity() {
    //    lateinit var sharedPreferenceData: SharedPreferenceData
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)


        Handler(Looper.myLooper()!!).postDelayed({
            if (prefs.token != "") {

                startActivity(Intent(applicationContext, HomeActivity::class.java))
                finish()

            } else {

                startActivity(Intent(this, InfoActivity::class.java))
                finish()

            }

        }, 3000)
    }



}