package com.ayizor.vodiypolymer.manager

import android.content.Context
import android.content.SharedPreferences
import com.ayizor.vodiypolymer.model.User
import com.google.gson.Gson


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

    fun loadUser(): User {
        val gson = Gson()
        val json: String? = sharedPreferences?.getString("user", "")
        val user: User = gson.fromJson(json, User::class.java)
        return user
    }

}