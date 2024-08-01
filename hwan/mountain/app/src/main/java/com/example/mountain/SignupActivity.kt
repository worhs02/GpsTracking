package com.example.mountain

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.mountain.DataModel.DataResponse
import com.example.mountain.DataModel.SignUpDataRequest
import com.example.mountain.Server.RetrofitClient
import com.example.mountain.signUpFragment.EmailFragment
import com.example.mountain.signUpFragment.PasswordFragment
import com.example.mountain.signUpFragment.UsernameFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignupActivity : AppCompatActivity() {

    private lateinit var btnNext: Button
    private lateinit var btnSignup: Button
    private var currentStep = 1
    private  lateinit var backButton:ImageButton

    private lateinit var dot1: TextView
    private lateinit var dot2: TextView
    private lateinit var dot3: TextView
    private lateinit var dot4: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        // Initialize views
        btnNext = findViewById(R.id.btnNext)
        btnSignup = findViewById(R.id.btnSignup)
        backButton = findViewById(R.id.back_button)

        dot1 = findViewById(R.id.dot1)
        dot2 = findViewById(R.id.dot2)
        dot3 = findViewById(R.id.dot3)
        dot4 = findViewById(R.id.dot4)



        // Show the initial fragment
        showFragment(EmailFragment())

        btnNext.setOnClickListener {
            when (currentStep) {
                1 -> {
                    val emailFragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as? EmailFragment
                    if (emailFragment?.getEmail()?.isNotEmpty() == true) {
                        // 이메일 인증 메소드 보내기
                        emailFragment.changeVerifyState()
                        updateProgress(2)
                    } else {
                        Toast.makeText(this, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show()
                    }
                }
                2 -> {
                    val emailFragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as? EmailFragment
                    if (emailFragment?.getVerificationCode()?.isNotEmpty() == true) { // 확인코드 확인
                        showFragment(PasswordFragment()) // This seems redundant; you might need to handle differently
                        updateProgress(3)
                    } else {
                        Toast.makeText(this, "인증 코드가 틀렸습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
                3 -> {
                    val passwordFragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as? PasswordFragment
                    val password = passwordFragment?.getPassword()
                    val confirmPassword = passwordFragment?.getConfirmPassword()

                    if (!password.isNullOrEmpty() && !confirmPassword.isNullOrEmpty()) {
                        if (password == confirmPassword) {
                            showFragment(UsernameFragment())
                            updateProgress(4)
                        } else {
                            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this, "Please enter and confirm your password", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        btnSignup.setOnClickListener {
            val usernameFragment =
                supportFragmentManager.findFragmentById(R.id.fragment_container) as? UsernameFragment
            val emailFragment =
                supportFragmentManager.findFragmentById(R.id.fragment_container) as? EmailFragment
            val passwordFragment =
                supportFragmentManager.findFragmentById(R.id.fragment_container) as? PasswordFragment

            val email = emailFragment?.getEmail() ?: ""
            val password = passwordFragment?.getPassword() ?: ""
            val confirmPassword = passwordFragment?.getConfirmPassword() ?: ""
            val username = usernameFragment?.getUsername() ?: ""

            if (username.isEmpty()) {
                Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
            } else {
                Log.d("SignupActivity", "Signup initiated")
                // Handle signup logic here
                val userData = SignUpDataRequest(email, password, username)
                // 사용자 등록 API 호출
                RetrofitClient.apiService.createUser(userData)
                    .enqueue(object : Callback<DataResponse> {
                        override fun onResponse(
                            call: Call<DataResponse>,
                            response: Response<DataResponse>
                        ) {
                            if (response.isSuccessful) {
                                Log.d("SignupActivity", "Signup successful: ${response.body()}")
                                fetchAllUsers() // 성공적으로 등록된 후 사용자 목록을 조회
                                updateProgress(5)
                            } else {
                                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                                Log.d("SignupActivity", "Signup failed: $errorBody")
                                Toast.makeText(
                                    this@SignupActivity,
                                    "Signup failed: $errorBody",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        override fun onFailure(call: Call<DataResponse>, t: Throwable) {
                            Log.d("SignupActivity", "Signup error: ${t.message}")
                            Toast.makeText(
                                this@SignupActivity,
                                "Signup error: ${t.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
                updateProgress(5)
            }
        }

            backButton.setOnClickListener{
            when (currentStep) {
                1 -> {
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                2 -> {
                    val emailFragment = EmailFragment()
                    showFragment(emailFragment)
                    updateProgress(1)
                }
                3 -> {
                    val emailFragment = EmailFragment()
                    showFragment(emailFragment)
                    updateProgress(2)
                }
                4 -> {
                    val passwordFragment = PasswordFragment()
                    showFragment(passwordFragment)
                    updateProgress(3)
                }
            }
        }

    }

    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    fun updateProgress(step: Int) {
        currentStep = step
        updateDots(step)


        when (step) {
            1 -> {
                btnNext.text = "인증하기"
                btnNext.visibility = View.VISIBLE
                btnSignup.visibility = View.GONE
            }
            2 -> {
                btnNext.text = "인증 코드 확인"
                btnNext.visibility = View.VISIBLE
                btnSignup.visibility = View.GONE
            }
            3 -> {
                btnNext.text = "다음"
                btnNext.visibility = View.VISIBLE
                btnSignup.visibility = View.GONE
            }
            4 -> {
                btnNext.text = "다음"
                btnNext.visibility = View.GONE
                btnSignup.visibility = View.VISIBLE
            }
            5 -> {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
    private fun updateDots(step: Int) {
        val activeDrawable = R.drawable.dot_active
        val inactiveDrawable = R.drawable.dot_inactive

        dot1.setBackgroundResource(if (step >= 1) activeDrawable else inactiveDrawable)
        dot2.setBackgroundResource(if (step >= 2) activeDrawable else inactiveDrawable)
        dot3.setBackgroundResource(if (step >= 3) activeDrawable else inactiveDrawable)
        dot4.setBackgroundResource(if (step >= 4) activeDrawable else inactiveDrawable)
    }
    // 사용자 목록을 조회하는 메서드
    private fun fetchAllUsers() {
        RetrofitClient.apiService.getAllUsers().enqueue(object : Callback<List<DataResponse>> {
            override fun onResponse(call: Call<List<DataResponse>>, response: Response<List<DataResponse>>) {
                if (response.isSuccessful) {
                    val users = response.body() ?: emptyList()
                    Log.d("SignupActivity", "Users fetched: $users")
                    // 사용자 목록 처리 로직 추가
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Unknown error"
                    Log.d("SignupActivity", "Failed to fetch users: $errorBody")
                }
            }

            override fun onFailure(call: Call<List<DataResponse>>, t: Throwable) {
                Log.d("SignupActivity", "Error fetching users: ${t.message}")
            }
        })
    }
}

