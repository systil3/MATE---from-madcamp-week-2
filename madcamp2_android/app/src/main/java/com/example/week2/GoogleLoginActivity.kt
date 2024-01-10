package com.example.week2

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

class GoogleLoginActivity: ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestId()
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(applicationContext, googleSignInOptions)
        val googleIntent: Intent = googleSignInClient.signInIntent

        val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result: ActivityResult ->
            if(result.resultCode == RESULT_OK){
                val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(result.data)

                lateinit var id:String
                lateinit var pass:String

                try{
                    val account = task.result
                    id = account.id.toString()
                    pass = md5(account.id.toString())
                }catch (e: Exception){
                    Log.e("google", e.toString())
                }

                GlobalScope.launch {
                    try {

                        val flag: Int = user_login(id, pass)

                        withContext(Dispatchers.Main) {
                            if (flag == 1) {
                                val intent = Intent(applicationContext, HomeActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else if (flag == 0) {
                                val intent = Intent(applicationContext, RegisterActivity_yung::class.java)
                                intent.putExtra("flag", true)
                                intent.putExtra("id", id)
                                intent.putExtra("pass", pass)
                                startActivity(intent)
                                finish()
                            }
                        }
                    } catch (e: Exception){
                        Log.e("google", e.toString())
                    }
                }
            }else{
                Log.e("google", result.toString())
            }

        }
        startForResult.launch(googleIntent)

        setContent{
            Surface(modifier = Modifier.fillMaxSize().background(Color.Black)){

            }
        }
    }
}

private fun md5(input:String): String {
    val md = MessageDigest.getInstance("MD5")
    return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
}