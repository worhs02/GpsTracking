package com.example.mountain

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.mountain.DataModel.UserDataResponse
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
    private lateinit var backButton: ImageButton

    private lateinit var dot1: TextView
    private lateinit var dot2: TextView
    private lateinit var dot3: TextView
    private lateinit var dot4: TextView
    private lateinit var dot5: TextView


    @SuppressLint("MissingInflatedId")
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
        dot5 = findViewById(R.id.dot5)

//        // Initialize fragments
//        emailFragment = EmailFragment()
//        passwordFragment = PasswordFragment()
//        usernameFragment = UsernameFragment()


        val EmailFragment =
            supportFragmentManager.findFragmentByTag("EmailFragment") as? EmailFragment
                ?: EmailFragment()
        val PasswordFragment =
            supportFragmentManager.findFragmentByTag("PasswordFragment") as? PasswordFragment
                ?: PasswordFragment()
        val UsernameFragment =
            supportFragmentManager.findFragmentByTag("UsernameFragment") as? UsernameFragment
                ?: UsernameFragment()

        // Show the initial fragment
        showFragment(EmailFragment, "EmailFragment")

        btnNext.setOnClickListener {
            when (currentStep) {
                1 -> {
                    if (EmailFragment.getEmail().isNotEmpty()) {
                        // 이메일 인증 메소드 보내기
                        EmailFragment.changeVerifyStateVisible()
                        updateProgress(2)
                    } else {
                        Toast.makeText(this, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show()
                    }
                }

                2 -> {
                    if (EmailFragment.getVerificationCode().isNotEmpty()) { // 확인코드 확인
                        showFragment(PasswordFragment, "PasswordFragment")
                        updateProgress(3)
                    } else {
                        Toast.makeText(this, "인증 코드가 틀렸습니다.", Toast.LENGTH_SHORT).show()
                    }
                }

                3 -> {
                    if (PasswordFragment.getPassword().isNotEmpty()) {
                        PasswordFragment.changeConfirmTextState()
                        updateProgress(4)
                    } else {
                        Toast.makeText(this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
                    }
                }

                4 -> {
                    val confirmPassword = PasswordFragment.getConfirmPassword()
                    val password = PasswordFragment.getPassword()

                    if (!password.isNullOrEmpty() && !confirmPassword.isNullOrEmpty()) {
                        if (password == confirmPassword) {
                            showFragment(UsernameFragment, "UsernameFragment")
                            updateProgress(5)
                        } else {
                            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT)
                                .show()
                        }
                    } else {
                        Toast.makeText(
                            this,
                            "Please enter and confirm your password",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        btnSignup.setOnClickListener {
            val email = EmailFragment.getEmail()
            val password = PasswordFragment.getPassword()
            val confirmPassword = PasswordFragment.getConfirmPassword()
            val username = UsernameFragment.getUsername()

            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
            } else {
                Log.d("SignupActivity", "Signup initiated")

                // Signup logic
                val userData = SignUpDataRequest(email, password, username)
                RetrofitClient.apiService.createUser(userData)
                    .enqueue(object : Callback<UserDataResponse> {
                        override fun onResponse(
                            call: Call<UserDataResponse>,
                            response: Response<UserDataResponse>
                        ) {
                            if (response.isSuccessful) {
                                Log.d("SignupActivity", "Signup successful: ${response.body()}")

                                // 사용자의 ID를 가져온다
                                val userId = response.body()?.id ?: return

                                // 태그를 생성한다 (ID + 999)
                                val tagNum = userId + 999

                                // 태그 업데이트 API 호출
                                RetrofitClient.apiService.updateTag(userId, tagNum)
                                    .enqueue(object : Callback<UserDataResponse> {
                                        override fun onResponse(
                                            call: Call<UserDataResponse>,
                                            response: Response<UserDataResponse>
                                        ) {
                                            if (response.isSuccessful) {
                                                Log.d(
                                                    "SignupActivity",
                                                    "Tag number updated successfully"
                                                )
                                                fetchAllUsers()
                                                updateProgress(6)
                                            } else {
                                                val errorBody =
                                                    response.errorBody()?.string()
                                                        ?: "Unknown error"
                                                Log.d(
                                                    "SignupActivity",
                                                    "Tag number update failed: $errorBody"
                                                )
                                                Toast.makeText(
                                                    this@SignupActivity,
                                                    "Tag update failed: $errorBody",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }

                                        override fun onFailure(
                                            call: Call<UserDataResponse>,
                                            t: Throwable
                                        ) {
                                            Log.d(
                                                "SignupActivity",
                                                "Tag number update error: ${t.message}"
                                            )
                                            Toast.makeText(
                                                this@SignupActivity,
                                                "Error: ${t.message}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    })

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

                        override fun onFailure(call: Call<UserDataResponse>, t: Throwable) {
                            Log.d("SignupActivity", "Signup error: ${t.message}")
                            Toast.makeText(
                                this@SignupActivity,
                                "Signup error: ${t.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
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
                    showFragment(EmailFragment(), "EmailFragment")
                    EmailFragment.changeVerifyStateGone()
                    updateProgress(1)
                }

                3 -> {
                    showFragment(EmailFragment(), "EmailFragment")
                    EmailFragment.changeVerifyStateVisible()
                    updateProgress(2)
                }

                4 -> {
                    showFragment(PasswordFragment(), "PasswordFragment")
                    PasswordFragment.changerepassStateGone()
                    updateProgress(3)
                }

                5 -> {
                    showFragment(PasswordFragment(), "PasswordFragment")
                    PasswordFragment.changerepassStateVisible()
                    updateProgress(4)
                }
            }
        }
    }



    private fun showFragment(fragment: Fragment, tag: String) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        // Check if the fragment is already added
        val existingFragment = fragmentManager.findFragmentByTag(tag)
        if (existingFragment == null) {
            // If the fragment is not already added, add it
            fragmentTransaction.add(R.id.fragment_container, fragment, tag)
        } else {
            // If the fragment is already added, show it
            fragmentTransaction.show(existingFragment)
        }

        // Hide other fragments
        for (frag in fragmentManager.fragments) {
            if (frag != existingFragment) {
                fragmentTransaction.hide(frag)
            }
        }

        fragmentTransaction.commit()
    }




    fun updateProgress(step: Int) {

        Log.d("SignupActivity", "updateProgress called with step: $step") // 로그 추가
        currentStep = step
        updateDots(step)

        when (step) {
            1 -> {
                btnNext.text = "인증하기"
                btnNext.visibility = View.VISIBLE
                btnSignup.visibility = View.GONE

            }
            2 -> {
                Log.d("SignupActivity", "Current step: $step") // 로그 추가
                Toast.makeText(this, "Current step: $step", Toast.LENGTH_SHORT).show()
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
                btnNext.visibility = View.VISIBLE
                btnSignup.visibility = View.GONE
            }
            5 -> {
                btnNext.visibility = View.GONE
                btnSignup.visibility = View.VISIBLE
            }
            6 -> {
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
        dot5.setBackgroundResource(if (step >= 5) activeDrawable else inactiveDrawable)
    }

    // 사용자 목록을 조회하는 메서드
    private fun fetchAllUsers() {
        RetrofitClient.apiService.getAllUsers().enqueue(object : Callback<List<UserDataResponse>> {
            override fun onResponse(call: Call<List<UserDataResponse>>, response: Response<List<UserDataResponse>>) {
                if (response.isSuccessful) {
                    val users = response.body() ?: emptyList()
                    Log.d("SignupActivity", "Users fetched: $users")
                    // 사용자 목록 처리 로직 추가
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Unknown error"
                    Log.d("SignupActivity", "Failed to fetch users: $errorBody")
                }
            }

            override fun onFailure(call: Call<List<UserDataResponse>>, t: Throwable) {
                Log.d("SignupActivity", "Error fetching users: ${t.message}")
            }
        })
    }

}
