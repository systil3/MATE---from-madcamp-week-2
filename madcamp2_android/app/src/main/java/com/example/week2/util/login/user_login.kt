package com.example.week2.util.login

import android.util.Log
import com.example.week2.global_access_token
import com.example.week2.global_nickname
import com.example.week2.global_origin_url
import com.example.week2.global_token_type
import com.example.week2.global_user_id
import com.google.gson.Gson
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request


data class LoginResponse(val access_token: String, val token_type: String, val name: String)

// login function
// if success to login, set global user id and nickname and return 1
// else return -1
fun user_login(user_id: String, user_pass: String): Int{
    val url = global_origin_url + "/users/login"


    val client = OkHttpClient()
    val formBody = FormBody.Builder()
        .add("grant_type", "")
        .add("username", user_id)
        .add("password", user_pass)
        .add("scope", "")
        .add("client_id", "")
        .add("client_secret", "")
        .build()

    val request = Request.Builder()
        .url(url)
        .header("accept", "application/json")
        .header("Content-Type", "application/x-www-form-urlencoded")
        .post(formBody)
        .build()

    try{
        val response = client.newCall(request).execute()
        if(response.isSuccessful){
            val user = Gson().fromJson(response.body?.string(), LoginResponse::class.java)
            global_user_id = user_id
            global_nickname = user.name
            global_access_token = user.access_token
            global_token_type = user.token_type

            return 1
        }else{
            return 0
        }

    }catch (e: Exception){
        Log.e("network", e.toString())
        return -1
    }

}