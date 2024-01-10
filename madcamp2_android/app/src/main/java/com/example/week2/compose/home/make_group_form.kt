package com.example.week2.compose.home

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.week2.util.group.join_new_group
import com.example.week2.util.group.make_new_group
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun make_group_form(flag: Int, closeDialog: () -> Unit){
    // flag -> 1 : make group, 2 : join group
    val context = LocalContext.current

    var group_name:String by remember { mutableStateOf("") }
    var group_pass:String by remember { mutableStateOf("") }
    var group_pass_second:String by remember { mutableStateOf("") }

    Box(Modifier.fillMaxSize()){
        Column(modifier = Modifier
            .padding(start = 20.dp, end = 20.dp)
            .align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally){

            OutlinedTextField(value = group_name, onValueChange = {group_name = it},
                label = { Text(if(flag == 1) "그룹명" else "그룹 태그") })

            Spacer(Modifier.height(30.dp))

            OutlinedTextField(value = group_pass, onValueChange = {group_pass = it},
                label = { Text("비밀번호") }, visualTransformation = PasswordVisualTransformation()
            )

            if(flag == 1){

                Spacer(Modifier.height(15.dp))
                OutlinedTextField(value = group_pass_second, onValueChange = {group_pass_second = it},
                    label = { Text("비밀번호 확인") }, visualTransformation = PasswordVisualTransformation()
                )
            }
        }

        Button(modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(bottom = 10.dp), onClick = nn@{
            if(flag == 1){
                if(group_name == "" || group_pass == "" || group_pass_second == ""){
                    Toast.makeText(context, "모든 필드를 입력하세요", Toast.LENGTH_SHORT).show()
                    return@nn
                }else if(group_pass != group_pass_second){
                    Toast.makeText(context, "비밀번호를 확인하세요", Toast.LENGTH_SHORT).show()
                    return@nn
                }

                GlobalScope.launch {
                    val ret = make_new_group(group_name, group_pass)
                    withContext(Dispatchers.Main){
                        if(ret == 1){
                            Toast.makeText(context, "그룹이 생성되었습니다", Toast.LENGTH_SHORT).show()
                        }else if(ret == 0){
                            Toast.makeText(context, "그룹 생성에 실패했습니다", Toast.LENGTH_SHORT).show()
                        }else if(ret == -1){
                            Toast.makeText(context, "오류가 발생했습니다", Toast.LENGTH_SHORT).show()
                        }

                        closeDialog()
                    }
                }

            }else if(flag == 2){
                if(group_name == "" || group_pass == ""){
                    Toast.makeText(context, "모든 필드를 입력하세요", Toast.LENGTH_SHORT).show()
                    return@nn
                }

                GlobalScope.launch {
                    val ret = join_new_group(group_name, group_pass)
                    withContext(Dispatchers.Main){
                        if(ret == 1){
                            Toast.makeText(context, "그룹에 가입되었습니다", Toast.LENGTH_SHORT).show()
                        }else if(ret == 0){
                            Toast.makeText(context, "그룹 가입에 실패했습니다", Toast.LENGTH_SHORT).show()
                        }else if(ret == -1){
                            Toast.makeText(context, "오류가 발생했습니다", Toast.LENGTH_SHORT).show()
                        }

                        closeDialog()
                    }
                }
            }
        }) {
            Text(text = if (flag == 1) "그룹 생성" else "그룹 가입")
        }
    }
}