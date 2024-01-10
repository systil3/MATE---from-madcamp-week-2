package com.example.week2.util.login

import android.util.Log
import com.example.week2.global_origin_url
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

// register new user
fun register_account(user_id: String, user_pass: String, user_nickname: String, user_email: String): Int{
    val url = global_origin_url + "/users/create/"


    var data = JSONObject()
    data.put("id", user_id)
    data.put("password", user_pass)
    data.put("name", user_nickname)
    data.put("email", user_email)



    try{
        val client = OkHttpClient()
        val requestBody = data.toString().toRequestBody("application/json".toMediaTypeOrNull())
        val request = Request.Builder().url(url).post(requestBody).build()

        val response = client.newCall(request).execute()
        if(response.isSuccessful){
            return 1
        }else{
            return -1
        }
    }catch (e : Exception){
        Log.e("register", e.toString())
        return -1
    }
}