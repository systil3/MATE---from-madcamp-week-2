package com.example.week2.util.login

import android.util.Log
import com.example.week2.global_origin_url
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

data class DuplicateReseponse(val exists: String)


// check if id is duplicated or not
// return 1 if not duplicated, return -1 when some error occured, return 0 if duplicated
fun check_duplicate(user_id: String): Int{
    Log.i("register", "start check_duplicate")

    val url = global_origin_url + "/users/find/$user_id"

    try{
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

        val response: Response = client.newCall(request).execute()

        Log.i("register", "start check_duplicate1")

        if(response.code != 200){
            return -1
        }

        val flag = Gson().fromJson(response.body?.string(), DuplicateReseponse::class.java)
        if(flag.exists == "true"){
            return 0
        }

        return 1
    }catch (e: Exception){
        Log.i("network", e.toString())
        return -1
    }
}