package com.example.week2.util.group

import com.example.week2.global_access_token
import com.example.week2.global_origin_url
import com.example.week2.global_user_id
import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

fun make_new_group(group_name: String, group_pass: String): Int{
    val url = global_origin_url + "/users/group/create"

    try {
        val client = OkHttpClient()

        val formBody = JSONObject()
            .put("id", "asdf")
            .put("password", group_pass)
            .put("group_name", group_name)
            .put("host_id", global_user_id)
            .toString()

        val request = Request.Builder()
            .url(url)
            .header("Content-Type", "application/json")
            .header("accept", "application/json")
            .header("Authorization", "Bearer $global_access_token")
            .post(formBody.toRequestBody("application/json".toMediaTypeOrNull()))
            .build()

        val response = client.newCall(request).execute()

        if(response.isSuccessful){ // 성공적으로 그룹에 들어갔을 때
            return 1
        }else{ // 그룹에 들어가는 것이 거절되었을 때: 서버 오류 등..
            return 0
        }
    }catch (e: Exception){ // 어플리케이션 자체에서 오류가 났을 때
        return -1
    }
}