package com.xeatpos

import android.app.Application
import com.xeatpos.utils.PreferenceHelper

val prefs: PreferenceHelper by lazy {
    App.prefs!!
}

class App: Application()
{
    companion object {
        var prefs: PreferenceHelper? = null
        lateinit var instance: App
            private set
    }

    override fun onCreate() {
        super.onCreate()

        instance = this
        prefs = PreferenceHelper(applicationContext)
    }
}