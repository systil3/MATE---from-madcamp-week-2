package com.example.week2

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.core.widget.addTextChangedListener
import com.example.week2.util.login.check_duplicate
import com.example.week2.util.login.register_account
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterActivity_yung : ComponentActivity() {
    var userId: String = ""
    var userPassword: String = ""
    var userPasswordCheck: String = ""
    var userName: String = ""
    var userEmail: String = ""
    var id_duplicated: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val global_flag:Boolean = intent.getBooleanExtra("flag", false)
        Log.i("register", "get global flag = $global_flag")
        setContent {
            LaunchedEffect(Unit) {
                if (global_flag) { // come from google signing -> assign id, password before
                    Log.i("register", "set id, password")
                    userId = intent.getStringExtra("id")!!
                    userPassword = intent.getStringExtra("pass")!!
                    userPasswordCheck = intent.getStringExtra("pass")!!
                    Log.i("register", "id = $userId, pass = $userPassword")
                }
            }
        }

        // XML 레이아웃 설정
        setContentView(R.layout.register)

        // 아이디 입력 필드
        val userIdTextField: EditText = findViewById(R.id.RegisterUserIdTextField)
        userIdTextField.addTextChangedListener { text ->
            userId = text.toString()
        }

        // 중복확인 버튼
        val duplicateCheckButton: LinearLayout = findViewById(R.id.DuplicateCheckButton)
        val registerIdAvailableText: TextView = findViewById(R.id.RegisterIdAvailableText)
        registerIdAvailableText.setText("")
        duplicateCheckButton.setOnClickListener {
            if(userId == ""){
                Toast.makeText(applicationContext, "아이디를 입력해주세요", Toast.LENGTH_SHORT).show()
                id_duplicated = 0
            } else {
                try{
                    GlobalScope.launch {
                        val flag: Int = check_duplicate(userId)
                        if(flag == -1){
                            Log.wtf("ERROR", "FATAL : SERVER CONNECTION FAILED")
                            throw Exception("Error!")
                        } else if(flag == 0){
                            id_duplicated = -1
                        } else if(flag == 1){
                            id_duplicated = 1
                        }
                    }

                    if(id_duplicated == -1) {
                        registerIdAvailableText.setTextColor(Color.parseColor("#FF0000"))
                        registerIdAvailableText.setText("이미 사용중인 아이디입니다.") }
                    else if(id_duplicated == 1)
                        registerIdAvailableText.setText("아이디를 사용할 수 있습니다.")
                    
                } catch(e: Exception){
                    Toast.makeText(applicationContext,
                        "중복확인 진행을 위해 서버 접속 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // 비밀번호 입력 필드
        val userPassTextField: EditText = findViewById(R.id.RegisterUserPassTextField)
        userPassTextField.addTextChangedListener { text ->
            userPassword = text.toString()
        }

        // 비밀번호 확인 필드
        val userPassCheckTextField: EditText = findViewById(R.id.RegisterUserPassCheckTextField)
        userPassCheckTextField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                userPasswordCheck = s.toString()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // 사용자 닉네임 입력 필드
        val userNameField: EditText = findViewById(R.id.RegisterUserNameField)
        userNameField.addTextChangedListener { text ->
            userName = text.toString()
        }

        // 사용자 이메일 입력 필드
        val userEmailNameField: EditText = findViewById(R.id.RegisterUserEmailNameField)
        val userEmailDomainField: EditText = findViewById(R.id.RegisterUserEmailDomainField)

        userEmailNameField.addTextChangedListener { text ->
            updateEmail()
        }

        userEmailDomainField.addTextChangedListener { text ->
            updateEmail()
        }

        // 회원가입 취소 버튼
        val registerCancelButton: LinearLayout = findViewById(R.id.RegisterCancelButton)
        registerCancelButton.setOnClickListener {
            val intent = Intent(this, LoginActivity_yung::class.java)
            startActivity(intent)
            finish()
        }

        // 회원가입 진행 버튼
        val registerConfirmButton: LinearLayout = findViewById(R.id.RegisterConfirmButton)
        registerConfirmButton.setOnClickListener {
            if(userId == "" || userPassword == "" || userName == "" || userPasswordCheck == ""){
                Toast.makeText(applicationContext, "모든 필드를 채워주세요!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(userPassword != userPasswordCheck){
                Toast.makeText(applicationContext, "비밀번호가 일치하지 않습니다!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(id_duplicated != 1){
                Toast.makeText(applicationContext, "중복 확인을 진행하세요!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            else {
                var flag:Int = 0
                GlobalScope.launch {
                    flag = register_account(userId, userPassword, userName, userEmail)

                    withContext(Dispatchers.Main){
                        if(flag == 1){
                            Toast.makeText(applicationContext, "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show()

                            if(global_flag){
                                val intent = Intent(applicationContext, LoginActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else{
                                finish()
                            }

                        }else if(flag == -1){
                            Toast.makeText(applicationContext, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()

                        }
                    }
                }
            }
        }
    }

    private fun updateEmail() {
        userEmail = "${findViewById<EditText>(R.id.RegisterUserEmailNameField).text}" +
                "@${findViewById<EditText>(R.id.RegisterUserEmailDomainField).text}"
    }
}