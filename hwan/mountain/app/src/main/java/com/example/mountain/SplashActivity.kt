// SplashActivity.kt
package com.example.mountain

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.mountain.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Glide를 사용하여 GIF 파일 로드
        Glide.with(this)
            .asGif()
            .load(R.drawable.mountain_animation) // drawable 폴더에 있는 GIF 파일 이름
            .into(binding.splashImageView)

        // 3초 후에 다음 액티비티로 이동
        Handler(Looper.getMainLooper()).postDelayed({
            val isLoggedIn = checkLoginStatus()
            if (isLoggedIn) {
                // 로그인된 상태라면 MainActivity로 이동
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                // 로그인이 안된 상태라면 LoginActivity로 이동
                startActivity(Intent(this, LoginActivity::class.java))
            }
            finish()
        }, 3000) // 3000ms = 3초
    }

    // 로그인 상태 확인 (예시로 SharedPreferences를 사용)
    private fun checkLoginStatus(): Boolean {
        val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        return sharedPreferences.getBoolean("isLoggedIn", false)
    }
}
