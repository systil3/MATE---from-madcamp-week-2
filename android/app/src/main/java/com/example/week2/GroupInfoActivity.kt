package com.example.week2

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.week2.util.group.GetGroupUserRes
import com.example.week2.util.group.exit_group
import com.example.week2.util.group.get_group_users
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GroupInfoActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val group_name = intent.getStringExtra("group_name")
        val group_id = intent.getStringExtra("group_id")
        val group_pass = intent.getStringExtra("group_pass")
        val group_host = intent.getStringExtra("group_host")


        setContent{
            var group_members by remember { mutableStateOf(emptyList<GetGroupUserRes>()) }

            LaunchedEffect(Unit){
                GlobalScope.launch {
                    val tmp = get_group_users(group_id!!)
                    withContext(Dispatchers.Main){
                        group_members = tmp
                        Log.i("members", group_members.toString())
                    }
                }
            }

            Box(modifier = Modifier
                .fillMaxSize()
                .background(Color(0x4586BFE8))){

                Column(modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 20.dp, end = 20.dp, top = 15.dp)){
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .clip(shape = RoundedCornerShape(10.dp))
                            .background(color = Color(0xffabe1ff).copy(alpha = 0.89f)),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(
                            text = "그룹 정보 보기", fontSize = 24.sp,
                            modifier = Modifier.padding(start = 10.dp)
                        )
                    }

                    Row (
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .padding(top = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Text(text = group_name!!, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                        Row(modifier = Modifier.clickable {
                            Toast.makeText(applicationContext, "$group_pass", Toast.LENGTH_SHORT).show()},
                            verticalAlignment = Alignment.CenterVertically){
                            Text("비밀번호 보기", fontSize = 14.sp, color = Color.Gray)
                            Image(painter = painterResource(id = R.drawable.locker), contentDescription = null, modifier = Modifier.size(25.dp))
                        }

                    }

                    Divider(
                        Modifier
                            .fillMaxWidth()
                            .padding(all = 10.dp), color = Color(0xff86bfe8).copy(alpha = 0.27f))

                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp)){

                        Column (modifier = Modifier.weight(1f)){
                            Text("그룹 ID", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(10.dp))
                            Text(text = "$group_id", fontSize = 22.sp)
                            Text(text = "복사 ▶", fontSize = 20.sp, color = Color(0xFF3E5CFF),
                                modifier = Modifier.clickable { copyToClipboard(applicationContext, group_id!!) })
                        }


                        Image(painter = painterResource(id = R.drawable.group_sample), contentDescription = null,
                            modifier = Modifier
                                .size(150.dp)
                                .padding(end = 20.dp))
                    }

                    Divider(
                        Modifier
                            .fillMaxWidth()
                            .padding(all = 10.dp), color = Color(0xff86bfe8).copy(alpha = 0.27f))

                    Column (modifier = Modifier.fillMaxWidth()){
                        Text(text = "구성 인원 ( ${group_members.size}명 )", fontSize = 24.sp, fontWeight = FontWeight.Bold)

                        Spacer(modifier = Modifier.height(10.dp))
                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                            .background(
                                color = Color(0xFFC5DFE1),
                                shape = RoundedCornerShape(10.dp)
                            )){

                            LazyColumn(modifier = Modifier
                                .fillMaxSize()
                                .padding(all = 15.dp)){
                                items(group_members){member ->
                                    Spacer(Modifier.height(5.dp))

                                    Box(
                                        Modifier
                                            .fillMaxWidth()
                                            .height(53.dp)){
                                        Box(
                                            Modifier
                                                .fillMaxWidth(.99f)
                                                .height(50.dp)
                                                .background(
                                                    color = Color(0xFF74B92F),
                                                    shape = RoundedCornerShape(8.dp)
                                                )
                                                .align(Alignment.BottomEnd)){}

                                        Box(modifier = Modifier
                                            .fillMaxWidth(.99f)
                                            .height(50.dp)
                                            .background(
                                                color = Color(0xFFC2FFAC),
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .align(Alignment.TopStart),
                                            contentAlignment = Alignment.Center){
                                            Text("${member.name}")
                                        }

                                    }

                                    Spacer(Modifier.height(5.dp))
                                }

                            }
                        }
                    }



                    Box(
                        Modifier
                            .fillMaxWidth()
                            .weight(1f), contentAlignment = Alignment.BottomCenter){
                        Column{
                            Divider(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(all = 10.dp), color = Color(0xff86bfe8).copy(alpha = 0.27f))
                            Spacer(Modifier.height(20.dp))
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .height(45.dp)){
                                Box(
                                    Modifier
                                        .fillMaxWidth(.99f)
                                        .height(40.dp)
                                        .align(Alignment.BottomEnd)
                                        .background(
                                            color = Color(0xFFFF74AF),
                                            shape = RoundedCornerShape(10.dp)
                                        )){}

                                Box(
                                    Modifier
                                        .fillMaxWidth(.99f)
                                        .height(40.dp)
                                        .align(Alignment.TopStart)
                                        .background(
                                            color = Color(0xFFFFC5C5),
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                        .clickable {
                                            if (group_host == global_user_id) {
                                                Toast
                                                    .makeText(
                                                        applicationContext,
                                                        "그룹장은 탈퇴가 불가능합니다",
                                                        Toast.LENGTH_SHORT
                                                    )
                                                    .show()
                                            } else {
                                                GlobalScope.launch {
                                                    val flag = exit_group(
                                                        group_id = group_id!!,
                                                        group_host = group_host!!,
                                                        group_pass = group_pass!!
                                                    )
                                                    withContext(Dispatchers.Main) {
                                                        if (flag == 1) {
                                                            Toast
                                                                .makeText(
                                                                    applicationContext,
                                                                    "탈퇴했습니다",
                                                                    Toast.LENGTH_SHORT
                                                                )
                                                                .show()
                                                            check_home_group = true
                                                            val intent = Intent(
                                                                applicationContext,
                                                                HomeActivity::class.java
                                                            )
                                                            startActivity(intent)
                                                            finish()
                                                        } else if (flag == 0) {
                                                            Toast
                                                                .makeText(
                                                                    applicationContext,
                                                                    "탈퇴하지 못했습니다",
                                                                    Toast.LENGTH_SHORT
                                                                )
                                                                .show()
                                                        } else if (flag == -1) {
                                                            Toast
                                                                .makeText(
                                                                    applicationContext,
                                                                    "오류가 발생했습니다",
                                                                    Toast.LENGTH_SHORT
                                                                )
                                                                .show()
                                                        }
                                                    }
                                                }
                                            }
                                        }, contentAlignment = Alignment.Center, ){
                                    Text("이 그룹 탈퇴", fontSize = 18.sp)
                                }

                            }




                            Spacer(Modifier.height(10.dp))
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .height(45.dp)){
                                Box(
                                    Modifier
                                        .fillMaxWidth(.99f)
                                        .height(40.dp)
                                        .background(
                                            color = Color(0xFFD24E4E),
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                        .align(Alignment.BottomEnd)){}

                                Box(
                                    Modifier
                                        .fillMaxWidth(.99f)
                                        .height(40.dp)
                                        .align(Alignment.TopStart)
                                        .background(
                                            color = Color(0xFFFF7575),
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                        .clickable {
                                            if (group_host != global_user_id) {
                                                Toast
                                                    .makeText(
                                                        applicationContext,
                                                        "그룹장만 가능한 기능입니다",
                                                        Toast.LENGTH_SHORT
                                                    )
                                                    .show()
                                            } else {
                                                GlobalScope.launch {
                                                    val flag = exit_group(
                                                        group_id = group_id!!,
                                                        group_host = group_host!!,
                                                        group_pass = group_pass!!
                                                    )
                                                    withContext(Dispatchers.Main) {
                                                        if (flag == 1) {
                                                            Toast
                                                                .makeText(
                                                                    applicationContext,
                                                                    "그룹을 삭제했습니다",
                                                                    Toast.LENGTH_SHORT
                                                                )
                                                                .show()
                                                            check_home_group = true
                                                            val intent = Intent(
                                                                applicationContext,
                                                                HomeActivity::class.java
                                                            )
                                                            startActivity(intent)
                                                            finish()
                                                        } else if (flag == 0) {
                                                            Toast
                                                                .makeText(
                                                                    applicationContext,
                                                                    "그룹을 삭제하지 못했습니다",
                                                                    Toast.LENGTH_SHORT
                                                                )
                                                                .show()
                                                        } else if (flag == -1) {
                                                            Toast
                                                                .makeText(
                                                                    applicationContext,
                                                                    "오류가 발생했습니다",
                                                                    Toast.LENGTH_SHORT
                                                                )
                                                                .show()
                                                        }
                                                    }
                                                }
                                            }
                                        }, contentAlignment = Alignment.Center, ){
                                    Text("그룹 삭제 (그룹장 전용)", fontSize = 18.sp)
                                }


                            }



                            Spacer(Modifier.height(20.dp))
                        }
                    }
                }
            }
        }
    }
}

private fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("Copied Text", text)
    clipboard.setPrimaryClip(clip)
}