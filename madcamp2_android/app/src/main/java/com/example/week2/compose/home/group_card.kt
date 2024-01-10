package com.example.week2.compose.home

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.week2.util.group.Group
import com.example.week2.util.group.exit_group
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.core.content.ContextCompat.startActivity
import com.example.week2.GroupInfoActivity

@Composable
private fun group_card(group: Group, context: Context){
    val shape = RoundedCornerShape(24f)

    Box(modifier = Modifier
        .background(color = Color.White, shape = shape)
        .size(80.dp)
        .clickable {
            val intent = Intent(context, GroupInfoActivity::class.java)
            intent.putExtra("group_name", group.group_name)
            intent.putExtra("group_id", group.id)
            intent.putExtra("group_pass", group.password)
//            startActivity(intent)
        }){

        Column(modifier = Modifier.align(Alignment.Center)) {
            Text(text = "${group.group_name}", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
        }

    }
}

@Composable
private fun group_info_dialog(dismiss: () -> Unit, group: Group){
    val context = LocalContext.current

    Dialog(onDismissRequest = { dismiss() }) {
        Surface (modifier = Modifier
            .clip(MaterialTheme.shapes.medium)){
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(all = 15.dp)) {
                Text(text = group.group_name, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(5.dp))
                Text(text = group.id, fontSize = 18.sp, fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable {
                        copyToClipboard(context, group.id)
                    })
                Spacer(Modifier.height(20.dp))
                Text(text = group.host_id)
                Spacer(Modifier.height(20.dp))
                Text(text = group.password)
                Spacer(Modifier.height(20.dp))

                Button(onClick = {
                    GlobalScope.launch {
                        val flag = exit_group(group.id, group.password, group.host_id)

                        withContext(Dispatchers.Main){
                            if(flag == 1){
                                Toast.makeText(context, "그룹에서 탈퇴했습니다", Toast.LENGTH_SHORT).show()
                                dismiss()
                            }else if(flag == 0){
                                Toast.makeText(context, "그룹에서 탈퇴하지 못했습니다", Toast.LENGTH_SHORT).show()
                            }else if(flag == -1){
                                Toast.makeText(context, "오류가 발생했습니다", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }) {
                    Text("그룹 탈퇴")
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