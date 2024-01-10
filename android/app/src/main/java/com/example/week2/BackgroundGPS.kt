package com.example.week2

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ListenableWorker.Result.Retry
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.util.concurrent.TimeUnit

class BackgroundGPS(appContext: Context, workerParams: WorkerParameters): Worker(appContext, workerParams) {

    private val mContext = appContext
    private val url = "ws://dhki.kr:8888/socket/get"


    @SuppressLint("MissingPermission")
    override fun doWork(): Result{
        Log.i("background_gps", "work start")
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()
        val socketListener = object : WebSocketListener() {

            override fun onMessage(webSocket: WebSocket, text: String) {

            }
        }
        val webSocket = client.newWebSocket(request, socketListener)
        client.dispatcher.executorService.shutdown()

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(applicationContext)

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if(location != null){
                Log.i("background_gps", "get last location")
                val latitude = location.latitude
                val longitude = location.longitude

                var text:String = "location\n"
                text += "user_id:$global_user_id\n"
                text += "latitude:${latitude}\n"
                text += "longitude:${longitude}\n"
                text += "group_id:None\n"

                webSocket!!.send(text)
                Log.i("background_gps", "sended location!")
            }else{
                var text:String = "locationFail\n"
                text += "user_id:$global_user_id\n"
            }

            webSocket!!.close(1000, "Connection on map closed by client")
        }


        return Result.success()
    }
}