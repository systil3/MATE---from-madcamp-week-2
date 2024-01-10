package com.example.week2.util.group

import android.util.Log
import com.example.week2.global_access_token
import com.example.week2.global_origin_url
import com.google.gson.Gson
import okhttp3.FormBody
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request

private data class JoinNewGroupRes(val completed: String, val msg: String)

fun join_new_group(group_tag: String, group_pass: String): Int{
    val url = global_origin_url + "/users/group/join"

    try {
        val client = OkHttpClient()
        val httpUrl = url.toHttpUrlOrNull()?.newBuilder()
            ?.addQueryParameter("group_id", group_tag)
            ?.addQueryParameter("group_password", group_pass)
            ?.build()

        val request = Request.Builder()
            .url(httpUrl!!)
            .header("Authorization", "Bearer $global_access_token")
            .post(FormBody.Builder().build())
            .build()

        val response = client.newCall(request).execute()

        if(response.isSuccessful){ // 성공적으로 그룹이 만들어졌을 때
            val res = Gson().fromJson(response.body?.string(), JoinNewGroupRes::class.java)
            if(res.completed == "true"){
                return 1
            }else if(res.completed == "false"){
                return 0
            }else{
                return -1
            }
        }else{ // 그룹이 만들어지는 것이 거절되었을 때: 서버 오류 등..
            return -1
        }
    }catch (e: Exception){ // 어플리케이션 자체에서 오류가 났을 때
        Log.e("error", e.toString())
        return -1
    }
}