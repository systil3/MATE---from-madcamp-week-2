package com.example.week2

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.week2.util.login.user_login
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class LoginActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var userid by remember{ mutableStateOf("") }
            var userpass by remember { mutableStateOf("") }

            val context = LocalContext.current

            Box(modifier = Modifier
                .fillMaxSize()
                .background(color = LightGray)){
                Text(text = "Login",
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(start = 10.dp, top = 10.dp),
                    fontSize = 24.sp, fontWeight = FontWeight.Bold)

                Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally){
                    Image(painter = painterResource(id = R.drawable.ic_launcher_foreground), contentDescription = null,
                        modifier = Modifier.size(150.dp))

                    Spacer(modifier = Modifier.height(50.dp))

                    OutlinedTextField(value = userid, onValueChange = {userid = it},
                        label = { Text("ID", color = Color.White) },
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color.White,
                            unfocusedBorderColor = Color.White,
                            cursorColor = Color.White
                        ))

                    Spacer(modifier = Modifier.height(20.dp))

                    OutlinedTextField(value = userpass, onValueChange = { userpass = it },
                        visualTransformation = PasswordVisualTransformation(), label = { Text("Password", color = Color.White) },
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color.White,
                            unfocusedBorderColor = Color.White,
                            cursorColor = Color.White
                        ))

                    // TODO
                    // 로그인 통신 구현 -> util에 구현하는 게 좋을 듯
                    Row (verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 30.dp)){
                        Button(onClick = vv@{
                            if(userid == "" || userpass == ""){
                                Toast.makeText(applicationContext, "필드를 모두 채워주세요", Toast.LENGTH_SHORT).show()
                                return@vv
                            }

                            var flag: Int = -2

                            GlobalScope.launch {
                                flag = user_login(userid, userpass)

                                withContext(Dispatchers.Main){
                                    if (flag == -1){
                                        Toast.makeText(applicationContext, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                                    }else if(flag == 1){
                                        val intent = Intent(context, HomeActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }else if (flag == 0){
                                        Toast.makeText(applicationContext, "로그인 실패", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }) {
                            Text("로그인")
                        }

                        Spacer(Modifier.width(20.dp))

                        Button(onClick = {
                            val intent = Intent(context, RegisterActivity::class.java)
                            startActivity(intent)
                        }) {
                            Text("회원 가입")
                        }
                    }

                    Button(onClick = { // goto google login
                        val intent = Intent(context, GoogleLoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    }) {
                        Text("Sign with Google")
                    }
                }
            }
        }
    }
}

