package com.example.week2

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.LocalContext
import com.example.week2.util.login.user_login
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import com.example.week2.databinding.LoginBinding

class LoginActivity_yung : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var userid = ""
        var userpass = ""
        val context = LocalContext
        // XML 레이아웃 설정
        setContentView(R.layout.login)

        // 아이디 입력 창
        val userIdTextField: EditText = findViewById(R.id.UserId_Textfield)
        userIdTextField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // 이전 텍스트가 변경되기 전에 호출
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // 텍스트가 변경될 때 호출
                userid = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {
                // 텍스트가 변경된 후에 호출
            }
        })

        // 비밀번호 입력 창
        val passwordTextField: EditText = findViewById(R.id.Password_Textfield)
        passwordTextField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // 이전 텍스트가 변경되기 전에 호출
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // 텍스트가 변경될 때 호출
                userpass = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {
                // 텍스트가 변경된 후에 호출
            }
        })
        // 로그인 버튼
        val loginButton: LinearLayout = findViewById(R.id.LoginButton)
        loginButton.setOnClickListener {
            if(userid == "" || userpass == ""){
                Toast.makeText(applicationContext, "필드를 모두 채워주세요", Toast.LENGTH_SHORT).show()
            }

            var flag: Int = -2

            GlobalScope.launch {
                flag = user_login(userid, userpass)

                withContext(Dispatchers.Main){
                    if (flag == -1){
                        Toast.makeText(applicationContext, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                    }else if(flag == 1){
                        val intent = Intent(applicationContext, HomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    }else if (flag == 0){
                        val textView = findViewById<TextView>(R.id.password_comment)
                        textView.setText("아이디 또는 패스워드가 틀립니다.")
                    }
                }
            }
        }

        // 회원가입 버튼
        val registerButton: LinearLayout = findViewById(R.id.RegisterButton)
        registerButton.setOnClickListener {
            // 회원가입 액티비티로 이동하
            val intent = Intent(applicationContext, RegisterActivity_yung::class.java)
            startActivity(intent)
        }

        // 구글 연동 로그인 버튼
        val googleLoginButton: ImageView = findViewById(R.id.GoogleLoginButton)
        googleLoginButton.setOnClickListener {
            // 구글 로그인 액티비티로 이동하는 코드
            val intent = Intent(applicationContext, GoogleLoginActivity::class.java)
            startActivity(intent)
        }
    }
}