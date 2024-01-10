package com.example.week2.util.group

import android.util.Log
import com.example.week2.global_access_token
import com.example.week2.global_origin_url
import com.google.gson.Gson
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request

data class GetGroupUserRes (val id: String, val password: String, val name: String, val email: String)

// get groups list
// 유저가 속한 그룹의 리스트를 불러옵니다
fun get_group_users(group_id: String): List<GetGroupUserRes>{
    val url = global_origin_url + "/group/users"

    try{
        val client = OkHttpClient()
        val httpUrl = url.toHttpUrlOrNull()?.newBuilder()
            ?.addQueryParameter("group_id", group_id)
            ?.build()

        val request = Request.Builder()
            .url(httpUrl!!)
            .header("Authorization", "Bearer $global_access_token")
            .build()

        val response = client.newCall(request).execute()
        if(response.isSuccessful){
            val ret: List<GetGroupUserRes> = Gson().fromJson(response.body?.string(), Array<GetGroupUserRes>::class.java).toList()
            return ret
        }else{
            val ret: List<GetGroupUserRes> = listOf(GetGroupUserRes("-1", "", "", ""))
            return ret
        }
    } catch (e: Exception){
        Log.e("group", e.toString())
        val ret: List<GetGroupUserRes> = listOf(GetGroupUserRes("-1", "", "", ""))
        return ret
    }
}