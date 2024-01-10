package com.example.week2

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.graphics.drawable.DrawableContainer
import android.os.Bundle
import android.os.Looper
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.week2.databinding.KakaoMapScreeenBinding
import com.example.week2.util.group.GetGroupUserRes
import com.example.week2.util.group.Group
import com.example.week2.util.group.get_group_list
import com.example.week2.util.group.get_group_users
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener

private data class LocationInfo(var user_id: String, var user_name: String, var latitude: Double, var longitude: Double)

@SuppressLint("MissingPermission")
class MapActivity : ComponentActivity(){
    var mapView: MapView? = null

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    private var my_latitute: Double = 0.0
    private var my_longitude: Double = 0.0

    private lateinit var webSocket: WebSocket
    private var currentGroupTag:String = ""

    private var flag:Boolean = false

    private val friendsList = mutableListOf<LocationInfo>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = KakaoMapScreeenBinding.inflate(layoutInflater)
        var my_groups:List<Group> = listOf()
        val userDistanceTextView: TextView = binding.UserDistanceTextView

        setContentView(binding.root)

        // apply kakao map
        mapView = MapView(this)
        binding.kakaoMapView.addView(mapView)

        // web socket connect
        connectSocket()

        // get GPS locations!!
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000 * 3)
            .setMinUpdateDistanceMeters(0f)
            .build()

        locationCallback = object : LocationCallback(){
            override fun onLocationResult(location: LocationResult) {
                super.onLocationResult(location)
                val lastLocation = location.lastLocation ?: return

                my_latitute = lastLocation.latitude
                my_longitude = lastLocation.longitude

                socketSendLocation()
            }
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())

        // apply compose view
        binding.mapComposeView.apply {

            setContent {
                var current_group:String by remember { mutableStateOf("") }
                var group_users: List<GetGroupUserRes> by remember { mutableStateOf(emptyList<GetGroupUserRes>()) }

                LaunchedEffect(Unit){
                    GlobalScope.launch {
                        val tmp = get_group_list()

                        if (tmp.size != 0 && tmp[0].id != "-1"){ // no error
                            val tmp2 = get_group_users(tmp[0].id)

                            withContext(Dispatchers.Main){
                                my_groups = tmp
                                current_group = my_groups[0].group_name
                                currentGroupTag = my_groups[0].id
                                group_users = tmp2
                            }
                        }else{ // some error
                            withContext(Dispatchers.Main){
                                Toast.makeText(applicationContext, "그룹이 없거나, 오류가 발생했습니다", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                        }
                    }
                }

                Row(modifier = Modifier
                    .fillMaxSize(), verticalAlignment = CenterVertically){
                    // dropdown menu of my groups
                    Column {
                        var expanded by remember { mutableStateOf(false) }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFDEF5FF))
                        ) {
                            my_groups.forEach{group ->
                                DropdownMenuItem(
                                    text = { Text(group.group_name) }, onClick = {
                                        GlobalScope.launch{
                                            val tmp = get_group_users(group.id)

                                            withContext(Dispatchers.Main){
                                                current_group = group.group_name
                                                currentGroupTag = group.id
                                                group_users = tmp
                                                expanded = false
                                            }

                                            flag = false
                                            mapView!!.removeAllPOIItems()
                                        }
                                    }, modifier = Modifier.fillMaxWidth()
                                        .padding(start = 30.dp, end = 30.dp, top = 10.dp, bottom = 10.dp)
                                        .background(color = Color.White, shape = MaterialTheme.shapes.medium))
                            }
                        }

                        Surface (modifier = Modifier
                            .fillMaxHeight(0.8f)
                            .width(150.dp)
                            .padding(10.dp)
                            .clip(MaterialTheme.shapes.medium)
                            .clickable { expanded = true }){
                            Box(modifier = Modifier.fillMaxSize()){
                                Text(
                                    text = "$current_group",
                                    modifier = Modifier.align(Center), fontSize = 20.sp, fontWeight = FontWeight.Bold
                                )
                            }
                        }

                    }

                    //divider
                    Divider(
                        Modifier
                            .fillMaxHeight(0.5f)
                            .width(2.dp)
                            .background(color = Color.Black))

                    LazyRow(verticalAlignment = CenterVertically){
                        items(group_users){user ->
                            user_card(user = user, onclick = {
                                val itemList = mapView!!.findPOIItemByName(user.id)

                                if(itemList != null){
                                    mapView!!.setMapCenterPoint(itemList[0].mapPoint, true)
                                    mapView!!.setZoomLevel(2, true)

                                    val got_latitude = itemList[0].mapPoint.mapPointGeoCoord.latitude
                                    val got_longitude = itemList[0].mapPoint.mapPointGeoCoord.longitude
                                    val distance_to_log = Distance().haversine(got_latitude, got_longitude,
                                        my_latitute, my_longitude)
                                    if(userDistanceTextView != null) {

                                        userDistanceTextView.text = "${user.name} 님 / " +
                                                "${Distance().distanceToString(distance_to_log)}"
                                    }

                                } else{
                                    Toast.makeText(applicationContext, "사용자 위치정보를 가져올 수 없습니다", Toast.LENGTH_SHORT).show()
                                    if(userDistanceTextView != null){
                                        userDistanceTextView.text = "      "
                                    }

                                }
                            })
                        }
                    }
                }
            }
        }
    }

    private fun update_marker(){


        if(!flag){
            mapView!!.removeAllPOIItems()

            for(info in friendsList){
                val marker = MapPOIItem()
                marker.apply {

                    itemName = info.user_id
                    mapPoint = MapPoint.mapPointWithGeoCoord(info.latitude, info.longitude)
                    markerType = if(info.user_id == global_user_id) MapPOIItem.MarkerType.RedPin else MapPOIItem.MarkerType.BluePin
                }
                mapView!!.addPOIItem(marker)
            }

            mapView!!.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(my_latitute, my_longitude), true)
            mapView!!.setZoomLevel(2, true)

            flag = true
        } else{
            for(info in friendsList){
                val marker = mapView!!.findPOIItemByName(info.user_id)

                try{
                    marker[0].mapPoint = MapPoint.mapPointWithGeoCoord(info.latitude, info.longitude)
                } catch (e: Exception){

                    val marker = MapPOIItem()

                    marker.apply {
                        itemName = info.user_id
                        mapPoint = MapPoint.mapPointWithGeoCoord(info.latitude, info.longitude)
                        markerType = if(info.user_id == global_user_id) MapPOIItem.MarkerType.RedPin else MapPOIItem.MarkerType.BluePin
                    }
                    mapView!!.addPOIItem(marker)
                }

                val distance_to_log = Distance().haversine(info.latitude, info.longitude,
                    my_latitute, my_longitude)
            }
        }
    }

    private fun connectSocket(){
        val url = "ws://dhki.kr:8888/socket/get"

        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

        val socketListener = object : WebSocketListener(){

            override fun onMessage(webSocket: WebSocket, text: String) {
                // 정보를 가공해서 리스트 형태로 저장합니다
                val type:String = text.substringBefore("\n")
                val messages: List<String> = text.substringAfter("\n").split("\n")

                when(type){
                    "location" -> { // 그룹원들의 위치를 전달받았을 때 처리
                        friendsList.clear()

                        for(message in messages){
                            var tmp = LocationInfo("", "", 0.0, 0.0)

                            val tokens: List<String> = message.split(" ")
                            for(token in tokens){
                                val value:List<String> = token.split(":")
                                when(value[0]){
                                    "user_id" -> {tmp.user_id = value[1]}
                                    "user_name" -> {tmp.user_name = value[1]}
                                    "latitude" -> {tmp.latitude = value[1].toDouble()}
                                    "longitude" -> {tmp.longitude = value[1].toDouble()}
                                }
                            }

                            friendsList.add(tmp)
                        }
                    }
                }
                update_marker()
            }
        }

        webSocket = client.newWebSocket(request, socketListener)
        client.dispatcher.executorService.shutdown()
    }

    private fun socketSendLocation(){
        var text:String = "location\n"
        text += "user_id:$global_user_id\n"
        text += "latitude:$my_latitute\n"
        text += "longitude:$my_longitude\n"
        text += "group_id:$currentGroupTag\n"

        webSocket!!.send(text)
    }

    override fun onDestroy() {
        super.onDestroy()
        webSocket!!.close(1000, "Connection on map closed by client")
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}

@Composable
private fun user_card(user: GetGroupUserRes, onclick: () -> Unit){
    Surface (modifier = Modifier
        .fillMaxHeight(0.8f)
        .width(90.dp)
        .padding(10.dp)
        .clip(MaterialTheme.shapes.medium)
        .clickable {
            onclick()
        }){
        Box(modifier = Modifier.fillMaxSize()){
            Text(text = user.name, modifier = Modifier.align(Center),
                fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }

    }
}