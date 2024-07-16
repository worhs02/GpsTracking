package com.example.mountain

import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class SignupActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        dbHelper = DBHelper(this)

        val emailEditText = findViewById<EditText>(R.id.email)
        val passwordEditText = findViewById<EditText>(R.id.password)
        val rePasswordEditText = findViewById<EditText>(R.id.repassword)
        val signupButton = findViewById<Button>(R.id.signup_button)
        val backButton = findViewById<ImageButton>(R.id.back_button)


        // * 색상 바꾸는 코드
        setAsteriskColor(findViewById(R.id.email_label), "Email *")
        setAsteriskColor(findViewById(R.id.password_label), "Password *")
        setAsteriskColor(findViewById(R.id.repassword_label), "Re *")


        signupButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val rePassword = rePasswordEditText.text.toString()
            if (validateSignup(email, password, rePassword)) {
                if (dbHelper.addUser(email, password)) {
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Signup failed. Try again.", Toast.LENGTH_SHORT).show()
                }
            }
        }


        //뒤로가기 버튼
        backButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // 현재 액티비티를 종료하여 뒤로 가기 스택에서 제거
        }
    }

    // * 색상 바꾸는 함수
    private fun setAsteriskColor(textView: TextView, text: String) {
        val spannableString = SpannableString(text)
        val color = ContextCompat.getColor(this, android.R.color.holo_red_dark)
        val start = text.indexOf("*")
        if (start >= 0) {
            spannableString.setSpan(
                ForegroundColorSpan(color),
                start,
                start + 1,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        textView.text = spannableString
    }

    private fun validateSignup(email: String, password: String, rePassword: String): Boolean {
        if (email.isEmpty() || password.isEmpty() || rePassword.isEmpty()) {
            Toast.makeText(this, "빈칸을 채워주세요.", Toast.LENGTH_SHORT).show()
            return false
        }
        if (password != rePassword) {
            Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
}
