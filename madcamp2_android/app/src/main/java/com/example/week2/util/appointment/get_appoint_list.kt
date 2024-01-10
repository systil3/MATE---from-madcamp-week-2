package com.example.week2.util.appointment

import android.util.Log
import com.example.week2.global_access_token
import com.example.week2.global_origin_url
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request

data class Appointment(val id:String, val time: String, val title: String, val lodation:String, val group_id:String)

fun get_appoint_list():List<Appointment>{
    val url = global_origin_url + "/users/appointment/show"

    try{
        val client = OkHttpClient()

        val request = Request.Builder()
            .url(url)
            .header("Authorization", "Bearer $global_access_token")
            .build()

        val response = client.newCall(request).execute()
        if(response.isSuccessful){
            val ret: List<Appointment> = Gson().fromJson(response.body?.string(), Array<Appointment>::class.java).toList()
            return ret
        }else{
            val ret: List<Appointment> = listOf(Appointment("-1", "", "", "", ""))
            return ret
        }
    } catch (e: Exception){
        Log.e("group", e.toString())
        val ret: List<Appointment> = listOf(Appointment("-1", "", "", "", ""))
        return ret
    }
}