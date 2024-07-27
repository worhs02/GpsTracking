package com.example.mountain

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        dbHelper = DBHelper(this)

        val emailEditText = findViewById<EditText>(R.id.email)
        val passwordEditText = findViewById<EditText>(R.id.password)
        val loginButton = findViewById<Button>(R.id.login_button)
        val kakaoLoginButton = findViewById<ImageButton>(R.id.kakao_login)
        val naverLoginButton = findViewById<ImageButton>(R.id.naver_login)
        val googleLoginButton = findViewById<ImageButton>(R.id.google_login)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "빈칸을 채워주세요.", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else if (dbHelper.checkUser(email, password)) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "이메일 또는 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()

                //화면 넘어가기 위한 임시 코드
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        kakaoLoginButton.setOnClickListener {
            // 카카오톡 로그인 로직 추가
        }

        naverLoginButton.setOnClickListener {
            // 네이버 로그인 로직 추가
        }

        googleLoginButton.setOnClickListener {
            // 구글 로그인 로직 추가
        }
    }

    // Login 화면에서 Sign up 화면으로 넘어가는 코드
    fun openSignupActivity(view: android.view.View) {
        val intent = Intent(this, SignupActivity::class.java)
        startActivity(intent)
    }
}
