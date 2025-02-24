package com.xeatpos.comman

import android.content.Context
import android.content.SharedPreferences

class SharedPreferenceData private constructor(context: Context) {

    private val sharedPreferences: SharedPreferences?

    fun saveData(key: Constant, value: String?) {
        val prefsEditor = sharedPreferences!!.edit()
        prefsEditor.putString(key.toString(), value)
        prefsEditor.commit()
        prefsEditor.apply()
    }

    fun saveData1(key: String, value: String?) {
        val prefsEditor = sharedPreferences!!.edit()
        prefsEditor.putString(key.toString(), value)
        prefsEditor.commit()
        prefsEditor.apply()
    }

    fun getData(key: Constant): String? {
        return if (sharedPreferences != null) {
            sharedPreferences.getString(key.toString(), "")
        } else ""
    }

    fun getData1(key: String): String? {
        return if (sharedPreferences != null) {
            sharedPreferences.getString(key, "")
        } else ""
    }

    companion object {
        private var yourPreference: SharedPreferenceData? = null
        fun getInstance(context: Context): SharedPreferenceData? {
            if (yourPreference == null) {
                yourPreference = SharedPreferenceData(context)
            }
            return yourPreference
        }
    }

    init {
        sharedPreferences = context.getSharedPreferences(
            "my_Pref",
            Context.MODE_PRIVATE
        )
    }


    fun clearSharedPrefrence() {
        val prefsEditor = sharedPreferences!!.edit()
        prefsEditor.clear()
        prefsEditor.apply()
        prefsEditor.commit()
    }

}