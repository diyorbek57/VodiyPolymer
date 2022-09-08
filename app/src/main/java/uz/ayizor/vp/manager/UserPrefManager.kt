package uz.ayizor.vp.manager

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import uz.ayizor.vp.model.User


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
        val user: User? = gson.fromJson(json, User::class.java)
        return user
    }

    fun nukeUser() {
        sharedPreferences?.edit()?.clear()?.apply()
    }

}