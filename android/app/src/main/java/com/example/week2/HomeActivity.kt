package com.example.week2

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import com.example.week2.compose.home.appointment_card
import com.example.week2.compose.home.make_group_form
import com.example.week2.util.group.Group
import com.example.week2.util.group.get_group_list
import com.example.week2.util.group.join_new_group
import com.example.week2.util.group.make_new_group
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Space
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Divider
import androidx.compose.ui.Alignment.Companion.BottomEnd
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.TopStart
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.core.app.ActivityCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.week2.util.appointment.Appointment
import com.example.week2.util.appointment.get_appoint_list
import java.util.concurrent.TimeUnit

class HomeActivity : ComponentActivity() {
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001
    private val LOCATION_BACKGROUND_REQUEST_CODE = 1002

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        check_permission()

        val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if(check_home_group){
                finish()
                check_home_group = false
            }
        }

        setContent {
            var my_groups by remember { mutableStateOf(emptyList<Group>()) }
            var my_appoints by remember { mutableStateOf(emptyList<Appointment>()) }
            var open_group_form: Boolean by remember { mutableStateOf(false) }
            var form_flag: Int by remember { mutableStateOf(1) }
            val context = LocalContext.current

            LaunchedEffect(Unit) {
                GlobalScope.launch {
                    val tmp = get_group_list()
                    val tmp2 = get_appoint_list()

                    withContext(Dispatchers.Main) {
                        my_groups = tmp
                        my_appoints = tmp2
                    }
                }
            }

            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color(0x4586BFE8))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    // showing my nickname, profile button
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 20.dp, end = 20.dp, top = 30.dp)
                            .height(40.dp)
                            .clip(shape = RoundedCornerShape(10.dp))
                            .background(color = Color(0xffabe1ff).copy(alpha = 0.89f)),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(
                            text = "${global_nickname}님", fontWeight = FontWeight.Bold, fontSize = 24.sp,
                            modifier = Modifier.padding(start = 10.dp)
                        )
                    }
                    // nickname end

                    Divider(
                        modifier = Modifier.fillMaxWidth(0.8f)
                            .padding(top = 10.dp, bottom = 10.dp),
                        color = Color(0xff86bfe8).copy(alpha = 0.27f)
                    )

                    // showing my groups
                    Column {

                        Row(
                            modifier = Modifier
                                .padding(start = 20.dp, end = 20.dp)
                                .fillMaxWidth()
                                .height(40.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "내 그룹",
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = Color.Black
                            )

                            Row(horizontalArrangement = Arrangement.Center) {
                                Text("그룹 생성", fontSize = 14.sp, color = Color(0xff146cd4), modifier = Modifier.clickable { form_flag = 1; open_group_form = true })

                                Text("|", color = Color(0xff146cd4), modifier = Modifier.padding(start = 10.dp, end = 10.dp))

                                Text("그룹 참가", fontSize = 14.sp, color = Color(0xff146cd4), modifier = Modifier.clickable { form_flag = 2; open_group_form = true })
                            }

                        }

                        Spacer(Modifier.height(10.dp))
                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .padding(start = 20.dp, end = 20.dp)){
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.985f)
                                    .height(145.dp)
                                    .align(BottomEnd)
                                    .background(shape = RoundedCornerShape(8.dp), color = Color(0xffFF74AF))
                            ) {}

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.985f)
                                    .height(145.dp)
                                    .align(TopStart)
                                    .background(shape = RoundedCornerShape(8.dp), color = Color(0xffffc5c5)),
                                contentAlignment = Alignment.Center
                            ) {

                                if (my_groups.size == 0) { // 그룹 사이즈가 0일 때
                                    Text(
                                        text = "속한 그룹이 없습니다",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp
                                    )
                                } else if (my_groups[0].id == "-1") { // 오류 처리
                                    Text(
                                        text = "그룹 정보를 가져오는 중에 오류가 발생했습니다",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp
                                    )
                                } else {
                                    LazyRow(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .fillMaxHeight(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {

                                        items(my_groups) { group ->
                                            Row {
                                                Spacer(modifier = Modifier.width(10.dp))

                                                val shape = RoundedCornerShape(24f)

                                                Box(modifier = Modifier
                                                    .background(
                                                        color = Color.White,
                                                        shape = shape
                                                    )
                                                    .size(80.dp)
                                                    .clickable {
                                                        val intent = Intent(
                                                            context,
                                                            GroupInfoActivity::class.java
                                                        )
                                                        intent.putExtra(
                                                            "group_name",
                                                            group.group_name
                                                        )
                                                        intent.putExtra(
                                                            "group_id",
                                                            group.id
                                                        )
                                                        intent.putExtra(
                                                            "group_pass",
                                                            group.password
                                                        )
                                                        intent.putExtra(
                                                            "group_host",
                                                            group.host_id
                                                        )

                                                        startForResult.launch(intent)
                                                    }) {

                                                    Text(
                                                        text = "${group.group_name}",
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .align(Center),
                                                        textAlign = TextAlign.Center
                                                    )

                                                }

                                                Spacer(modifier = Modifier.width(10.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    // groups end

                    Spacer(Modifier.height(20.dp))
                    Divider(
                        modifier = Modifier.fillMaxWidth(0.8f),
                        color = Color(0xff86bfe8).copy(alpha = 0.27f)
                    )
                    Spacer(Modifier.height(20.dp))

                    // 약속 시작
                    Column(modifier = Modifier.padding(start = 20.dp, end = 20.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp)
                                .padding(start = 5.dp, end = 5.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "내 일정",
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = Color.Black
                            )
                            Text("필터링", fontSize = 14.sp, color = Color(0xff2552f1))
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .background(color = Color(0xffc5dfe1))
                                .clip(shape = RoundedCornerShape(6.dp))
                                .padding(top = 10.dp), contentAlignment = Center
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(all = 5.dp)
                                    .fillMaxSize()
                            ) {
                                LazyColumn(modifier = Modifier.fillMaxWidth(.7f).fillMaxHeight()) {
                                    items(my_appoints){ appoint ->
                                        Box(modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 10.dp, bottom = 10.dp)
                                            .height(45.dp)){

                                            Box(modifier = Modifier
                                                .fillMaxWidth(0.985f)
                                                .height(42.dp)
                                                .background(shape = RoundedCornerShape(8.dp), color = Color(0xffCFCA5C))
                                                .align(BottomEnd)){}

                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth(0.985f)
                                                    .height(42.dp)
                                                    .background(shape = RoundedCornerShape(8.dp), color = Color(0xfffdffad))
                                                    .align(TopStart),
                                                contentAlignment = Center
                                            ) {
                                                Text("${appoint.title}")
                                            }
                                        }
                                    }
                                }
                                Spacer(Modifier.width(10.dp))

                                Box(Modifier.fillMaxHeight().weight(1f)
                                    .clickable { /*TODO*/ }){

                                    Box(Modifier.fillMaxWidth(0.97f).fillMaxHeight(0.98f)
                                        .align(BottomEnd).background(shape = RoundedCornerShape(8.dp), color = Color(0xFF74A4AF))){}

                                    Box(Modifier.fillMaxWidth(0.97f).fillMaxHeight(0.98f)
                                        .align(TopStart).background(shape = RoundedCornerShape(8.dp), color = Color(0xFFA4F4F4))){
                                        Text("모든 일정 보기...", modifier = Modifier.fillMaxWidth().align(Center), textAlign = TextAlign.Center)
                                    }
                                }
                            }
                        }
                    }
                    //약속 끝

                    Spacer(Modifier.height(20.dp))
                    Divider(
                        modifier = Modifier.fillMaxWidth(0.8f),
                        color = Color(0xff86bfe8).copy(alpha = 0.27f)
                    )
                    Spacer(Modifier.height(10.dp))

                    Row(modifier = Modifier.padding(start = 20.dp, end = 20.dp).fillMaxWidth()) {
                        Text("그룹 위치 확인 >",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color.Black,
                            modifier = Modifier.clickable {
                                val intent = Intent(context, MapActivity::class.java)
                                startActivity(intent)
                            })
                    }

                    // Group form Dialog
                    if (open_group_form) {
                        Dialog(
                            onDismissRequest = {
                                open_group_form = false
                                GlobalScope.launch {
                                    val tmp = get_group_list()
                                    withContext(Dispatchers.Main) {
                                        my_groups = tmp
                                    }
                                }
                            },
                        ) {


                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(0.7f)
                                    .clip(shape = MaterialTheme.shapes.medium)
                            ) {
                                Column(Modifier.fillMaxSize()) {
                                    Box(Modifier.fillMaxWidth()) {
                                        Row(
                                            Modifier
                                                .fillMaxWidth()
                                                .align(Alignment.Center)
                                                .padding(top = 5.dp),
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Text(text = "그룹 생성",
                                                color = if (form_flag == 1) Color.Black else Color.Gray,
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.clickable { form_flag = 1 })

                                            Spacer(modifier = Modifier.width(20.dp))

                                            Text(text = "그룹 참가",
                                                color = if (form_flag == 2) Color.Black else Color.Gray,
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.clickable { form_flag = 2 })
                                        }

                                        Icon(Icons.Filled.Close,
                                            contentDescription = null,
                                            modifier = Modifier
                                                .align(Alignment.CenterEnd)
                                                .size(30.dp)
                                                .padding(end = 10.dp, top = 5.dp)
                                                .clickable {
                                                    open_group_form = false
                                                    GlobalScope.launch {
                                                        val tmp = get_group_list()
                                                        withContext(Dispatchers.Main) {
                                                            my_groups = tmp
                                                        }
                                                    }
                                                })
                                    }

                                    Spacer(Modifier.height(10.dp))

                                    make_group_form(flag = form_flag,
                                        closeDialog = {
                                            open_group_form = false

                                            GlobalScope.launch {
                                                val tmp = get_group_list()
                                                val tmp2 = get_appoint_list()
                                                withContext(Dispatchers.Main) {
                                                    my_groups = tmp
                                                    my_appoints = tmp2
                                                }
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                    // open group form Dialog end
                }

            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    check_permission2()
                } else {
                    finish()
                }
            }
            LOCATION_BACKGROUND_REQUEST_CODE -> {
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    val workRequest = PeriodicWorkRequestBuilder<BackgroundGPS>(15, TimeUnit.MINUTES).build()
                    WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork("backgroundGPS", ExistingPeriodicWorkPolicy.REPLACE, workRequest)
                }else{
                    finish()
                }
            }

        }
    }

    private fun check_permission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            // request GPS information
            ActivityCompat.requestPermissions(
                this,
                arrayOf( Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }else{
            // TODO: 백그라운드에서 Service 확인, 안돌고 있으면 시작
            check_permission2()
        }
    }
    private fun check_permission2(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED){
            // request GPS information
            ActivityCompat.requestPermissions(
                this,
                arrayOf( Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                LOCATION_BACKGROUND_REQUEST_CODE
            )
        }else{
            // TODO: 백그라운드에서 Service 확인, 안돌고 있으면 시작
            val workRequest = PeriodicWorkRequestBuilder<BackgroundGPS>(15, TimeUnit.MINUTES).build()
            WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork("backgroundGPS", ExistingPeriodicWorkPolicy.REPLACE, workRequest)
        }
    }
}

