package com.example.week2

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.week2.util.login.check_duplicate
import com.example.week2.util.login.register_account
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterActivity :ComponentActivity() {
    @SuppressLint("CoroutineCreationDuringComposition")
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val global_flag:Boolean = intent.getBooleanExtra("flag", false)
        Log.i("register", "get global flag = $global_flag")
        setContent {
            var user_id by remember { mutableStateOf("") }
            var id_duplicated: Int by remember { mutableStateOf(0) }

            var user_pass by remember { mutableStateOf("") }
            var user_pass_second by remember { mutableStateOf("") }

            var user_nickname by remember { mutableStateOf("") }

            LaunchedEffect(Unit){
                if(global_flag){ // come from google signing -> assign id, password before
                    Log.i("register", "set id, password")
                    user_id = intent.getStringExtra("id")!!
                    user_pass = intent.getStringExtra("pass")!!
                    user_pass_second = intent.getStringExtra("pass")!!
                    Log.i("register", "id = $user_id, pass = $user_pass")

                    GlobalScope.launch {
                        var flag_duplicate = check_duplicate(user_id)

                        withContext(Dispatchers.Main){
                            if(flag_duplicate == 1) id_duplicated = 1
                            else{
                                Toast.makeText(applicationContext, "오류가 발생했습니다. 회원가입을 직접 진행해주세요", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                        }
                    }


                }
            }


            Surface(modifier = Modifier.fillMaxSize()){
                Text("회원 가입", fontSize = 24.sp, fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 20.dp, top = 20.dp))

                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp)){

                    // id form
                    Column(modifier = Modifier.fillMaxWidth()){
                        Row(verticalAlignment = Alignment.CenterVertically){
                            OutlinedTextField(value = user_id, onValueChange = {user_id = it; id_duplicated = 0},
                                modifier = Modifier.weight(0.7f), label = {Text("아이디")}, readOnly = global_flag)

                            Spacer(modifier = Modifier.width(20.dp))

                            // TODO
                            Button(modifier = Modifier.weight(0.3f), onClick = tt@{
                                if(user_id == ""){
                                    Toast.makeText(applicationContext, "아이디를 입력해주세요", Toast.LENGTH_SHORT).show()
                                    id_duplicated = 0
                                    return@tt
                                }
                                var flag:Int
                                try{
                                    GlobalScope.launch {
                                        val flag: Int = check_duplicate(user_id)

                                        if(flag == -1){
                                            throw Exception("Error!")
                                        }else if(flag == 0){
                                            id_duplicated = -1
                                        }else if(flag == 1){
                                            id_duplicated = 1
                                        }
                                    }
                                }catch(e: Exception){
                                    Toast.makeText(applicationContext, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                                }

                            }){
                                Text("중복확인", fontSize = 14.sp)
                            }
                        }

                        if(id_duplicated == -1){
                            Text("사용할 수 없는 아이디입니다.", fontSize = 12.sp, color = Color.Red)
                        }else if(id_duplicated == 1){
                            Text("아이디를 사용할 수 있습니다!", fontSize = 12.sp, color = Color.Green)
                        }
                    }

                    Spacer(Modifier.height(30.dp))

                    // user password
                    Column(horizontalAlignment = Alignment.Start,
                        modifier = Modifier.fillMaxWidth()){
                        OutlinedTextField(value = user_pass, onValueChange = {user_pass = it},
                            visualTransformation = PasswordVisualTransformation(),
                            label = {Text ("비밀번호")}, modifier = Modifier.fillMaxWidth(), readOnly = global_flag)

                        Spacer(Modifier.height(15.dp))

                        OutlinedTextField(value = user_pass_second, onValueChange = {user_pass_second = it},
                            visualTransformation = PasswordVisualTransformation(),
                            label = {Text ("비밀번호 확인")}, modifier = Modifier.fillMaxWidth(), readOnly = global_flag)
                    }

                    Spacer(modifier = Modifier.height(30.dp))

                    OutlinedTextField(value = user_nickname, onValueChange = {user_nickname = it},
                        label = {Text ("닉네임")}, modifier = Modifier.fillMaxWidth())

                    Spacer(modifier = Modifier.height(30.dp))

                    Row(horizontalArrangement = Arrangement.Center){
                        Button(onClick = { finish() }, modifier = Modifier.weight(0.3f)){
                            Text("취소")
                        }

                        Spacer(Modifier.width(30.dp))

                        Button(onClick = zz@{
                            if(user_id == "" || user_pass == "" || user_nickname == "" || user_pass_second == ""){
                                Toast.makeText(applicationContext, "모든 필드를 채워주세요!", Toast.LENGTH_SHORT).show()
                                return@zz
                            }
                            if(user_pass != user_pass_second){
                                Toast.makeText(applicationContext, "비밀번호가 일치하지 않습니다!", Toast.LENGTH_SHORT).show()
                                return@zz
                            }
                            if(id_duplicated != 1){
                                 Toast.makeText(applicationContext, "중복 확인을 진행하세요!", Toast.LENGTH_SHORT).show()
                                 return@zz
                            }

                            var flag:Int = 0
                            GlobalScope.launch {
                                flag = register_account(user_id, user_pass, user_nickname, "asdf@asdf")

                                withContext(Dispatchers.Main){
                                    if(flag == 1){
                                        Toast.makeText(applicationContext, "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show()

                                        if(global_flag){
                                            val intent = Intent(applicationContext, LoginActivity::class.java)
                                            startActivity(intent)
                                            finish()
                                        }else{
                                            finish()
                                        }

                                    }else if(flag == -1){
                                        Toast.makeText(applicationContext, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }, modifier = Modifier.weight(0.3f)){
                            Text("회원 가입")
                        }
                    }
                }
            }
        }
    }
}