package com.example.week2.util.group

import android.util.Log
import android.widget.Toast
import com.example.week2.global_access_token
import com.example.week2.global_origin_url
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request

data class Group(val id: String, val password: String, val group_name: String, val host_id: String)

// get groups list
// 유저가 속한 그룹의 리스트를 불러옵니다
fun get_group_list(): List<Group>{
    val url = global_origin_url + "/users/group/show"

    try{
        val client = OkHttpClient()

        val request = Request.Builder()
            .url(url)
            .header("Authorization", "Bearer $global_access_token")
            .build()

        val response = client.newCall(request).execute()
        if(response.isSuccessful){
            val ret: List<Group> = Gson().fromJson(response.body?.string(), Array<Group>::class.java).toList()
            return ret
        }else{
            val ret: List<Group> = listOf(Group("-1", "", "", ""))
            return ret
        }
    } catch (e: Exception){
        Log.e("group", e.toString())
        val ret: List<Group> = listOf(Group("-1", "", "", ""))
        return ret
    }
}