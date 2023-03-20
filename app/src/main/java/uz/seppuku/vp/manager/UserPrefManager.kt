package uz.seppuku.vp.manager

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import uz.seppuku.vp.model.Location
import uz.seppuku.vp.model.User


class UserPrefManager(context: Context) {


    val sharedPreferences: SharedPreferences?


    init {
        sharedPreferences = context.getSharedPreferences("user_db", Context.MODE_PRIVATE)

    }

    fun storeUser(user: User?) {
        val prefsEditor = sharedPreferences!!.edit()
        val gson = Gson()
        val json = gson.toJson(user)
        prefsEditor.putString("user", json)
        prefsEditor.apply()
    }

    fun loadUser(): User? {
        val gson = Gson()
        val json: String? = sharedPreferences?.getString("user", "")
        return gson.fromJson(json, User::class.java)
    }

    fun storeUserLocations(locations: ArrayList<Location>) {
        val gson = Gson()
        val json = gson.toJson(locations)
        val prefsEditor = sharedPreferences!!.edit()
        prefsEditor.putString("user_locations", json)
        prefsEditor.apply()
    }

    fun loadUserLocations(): ArrayList<Location>? {
        val gson = Gson()
        val json: String? = sharedPreferences?.getString("user_locations", "")
        val type = object : TypeToken<ArrayList<Location?>?>() {}.type
        return try {
            gson.fromJson(json, type) as ArrayList<Location>
        } catch (e: NullPointerException) {
            null
        }


    }

    fun nukeUser() {
        sharedPreferences?.edit()?.clear()?.apply()
    }

}