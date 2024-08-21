package com.example.mountain

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mountain.DataModel.LoginRequest
import com.example.mountain.DataModel.LoginResponse
import com.example.mountain.Server.ApiService
import com.example.mountain.Server.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var apiService: ApiService // Retrofit API 서비스 인스턴스

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Retrofit API 서비스 초기화
        apiService = RetrofitClient.apiService

        val usernameEditText = findViewById<EditText>(R.id.email)
        val passwordEditText = findViewById<EditText>(R.id.password)
        val loginButton = findViewById<Button>(R.id.login_button)
        val kakaoLoginButton = findViewById<ImageButton>(R.id.kakao_login)
        val naverLoginButton = findViewById<ImageButton>(R.id.naver_login)
        val googleLoginButton = findViewById<ImageButton>(R.id.google_login)

        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "빈칸을 채워주세요.", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                val loginRequest = LoginRequest(username, password)

                apiService.login(loginRequest).enqueue(object : Callback<LoginResponse> {
                    override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                        if (response.isSuccessful) {
                            val token = response.body()?.token
                            if (token != null) {
                                // 로그인 성공, MainActivity로 이동
                                val intent = Intent(this@LoginActivity, MainActivity::class.java).apply {
                                    putExtra("USER_TOKEN", token)
                                }
                                startActivity(intent)
                                finish()
                            }
                        } else {
                            Toast.makeText(this@LoginActivity, "로그인 실패: ${response.message()}", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        Toast.makeText(this@LoginActivity, "로그인 실패: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
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

    // 로그인 상태를 SharedPreferences에 저장
    private fun saveLoginStatus(isLoggedIn: Boolean) {
        val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putBoolean("isLoggedIn", isLoggedIn)
            apply()
        }
    }

    // Login 화면에서 Sign up 화면으로 넘어가는 코드
    fun openSignupActivity(view: android.view.View) {
        val intent = Intent(this, SignupActivity::class.java)
        startActivity(intent)
    }
}
