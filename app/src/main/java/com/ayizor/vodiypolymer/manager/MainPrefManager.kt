package com.ayizor.vodiypolymer.manager

import android.content.Context
import android.content.SharedPreferences

class MainPrefManager(context: Context) {

    val sharedPreferences: SharedPreferences?


    init {
        sharedPreferences = context.getSharedPreferences("vp_db", Context.MODE_PRIVATE)

    }

    fun storeDeviceToken(token: String?) {
        val prefsEditor = sharedPreferences!!.edit()
        prefsEditor.putString("device_token", token)
        prefsEditor.apply()
    }

    fun loadDeviceToken(): String? {
        return sharedPreferences!!.getString("device_token", "")
    }


    fun storeCurrency(currency: String?) {
        val prefsEditor = sharedPreferences!!.edit()
        prefsEditor.putString("currency", currency)
        prefsEditor.apply()
    }

    fun loadCurrency(): String? {
        return sharedPreferences!!.getString("currency", "")
    }
}