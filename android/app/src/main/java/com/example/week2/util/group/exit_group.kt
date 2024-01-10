package com.example.week2.util.group

import android.util.Log
import com.example.week2.global_access_token
import com.example.week2.global_origin_url
import com.example.week2.global_user_id
import com.google.gson.Gson
import okhttp3.FormBody
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request

private data class ExitGroupRes(val completed: Boolean, val msg: String)

fun exit_group(group_id: String, group_pass: String, group_host: String): Int{
    var url = global_origin_url
    if(group_host == global_user_id){
        url += "/users/group/delete"
    }else{
        url += "/users/group/dropout"
    }


    try{
        val client = OkHttpClient()
        var httpUrl = url.toHttpUrlOrNull()?.newBuilder()
            ?.addQueryParameter("group_id", group_id)
            ?.addQueryParameter("group_password", group_pass)
            ?.build()

        val request = Request.Builder()
            .url(httpUrl!!)
            .header("Authorization", "Bearer $global_access_token")
            .delete(FormBody.Builder().build())
            .build()

        val response = client.newCall(request).execute()
        if(response.isSuccessful){
            val res = Gson().fromJson(response.body?.string(), ExitGroupRes::class.java)
            if(res.completed){
                return 1
            }else{
                Log.e("error msg", res.msg)
                return 0
            }
        }else {
            return -1
        }

    }catch (e: Exception){
        Log.e("exit_group", e.toString())
        return -1
    }
}