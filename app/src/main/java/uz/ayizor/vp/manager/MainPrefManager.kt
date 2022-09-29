package uz.ayizor.vp.manager

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

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

    fun storeLanguage(currency: String?) {
        val prefsEditor = sharedPreferences!!.edit()
        prefsEditor.putString("language", currency)
        prefsEditor.apply()
    }

    fun loadLanguage(): String? {
        return sharedPreferences!!.getString("language", "")
    }

    fun storeSearchHistory(area: ArrayList<String>) {
        val gson = Gson()
        val json = gson.toJson(area)
        val prefsEditor = sharedPreferences!!.edit()
        prefsEditor.putString("search_history", json)
        prefsEditor.apply()
    }

    fun loadSearchHistory(): ArrayList<String>? {
        val gson = Gson()
        val json: String? = sharedPreferences?.getString("search_history", null)
        val type = object : TypeToken<ArrayList<String?>?>() {}.type
        return try {
            gson.fromJson(json, type) as ArrayList<String>
        } catch (e: NullPointerException) {
            null
        }


    }
}