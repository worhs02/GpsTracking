package com.example.mountain

import android.annotation.SuppressLint
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Nickname
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout

class SignupActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var etUsername: EditText
    private lateinit var btnSignup: Button
    private lateinit var btnNext: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var progressLayout: ConstraintLayout
    private lateinit var EmailText1: TextView
    private lateinit var EmailText2: TextView
    private lateinit var PWText1: TextView
    private lateinit var PWText2: TextView
    private lateinit var RePWText1: TextView
    private lateinit var RePWText2: TextView
    private lateinit var NicknameText1: TextView
    private lateinit var NicknameText2: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        etUsername = findViewById(R.id.etUsername)
        btnSignup = findViewById(R.id.btnSignup)
        btnNext = findViewById(R.id.btnNext)
        progressBar = findViewById(R.id.progressBar)
        EmailText1 = findViewById(R.id.textViewEmailPrompt1)
        EmailText2 = findViewById(R.id.textViewEmailPrompt2)
        PWText1 = findViewById(R.id.textViewPwPrompt1)
        PWText2 = findViewById(R.id.textViewPwPrompt2)
        RePWText1 = findViewById(R.id.textViewRePwPrompt1)
        RePWText2 = findViewById(R.id.textViewRePwPrompt2)
        NicknameText1 = findViewById(R.id.textViewNamePrompt1)
        NicknameText2 = findViewById(R.id.textViewNamePrompt2)

        updateProgress(1) // Initially set to step 1

        btnNext.setOnClickListener {
            when (currentStep) {
                1 -> {
                    if (etEmail.text.toString().isNotEmpty()) {
                        updateProgress(2)
                    } else {
                        Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
                    }
                }
                2 -> {
                    if (etPassword.text.toString().isNotEmpty()) {
                        etConfirmPassword.visibility = View.VISIBLE
                        etConfirmPassword.requestFocus()
                        updateProgress(3)
                    } else {
                        Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show()
                    }
                }
                3 -> {
                    if (etConfirmPassword.text.toString().isNotEmpty()) {
                        etUsername.visibility = View.VISIBLE
                        etUsername.requestFocus()
                        updateProgress(4)
                    } else {
                        Toast.makeText(this, "Please confirm your password", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        btnSignup.setOnClickListener {
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()
            val confirmPassword = etConfirmPassword.text.toString()
            val username = etUsername.text.toString()

            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || username.isEmpty()) {
                Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
            } else if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            } else {
                // Here, you can add code to handle the signup logic, such as sending the data to a server
                Toast.makeText(this, "Signup successful", Toast.LENGTH_SHORT).show()
                updateProgress(4) // Final step completion
            }
        }
        

    }

    private var currentStep = 1

    private fun updateProgress(step: Int) {
        currentStep = step
        progressBar.progress = step

        when (step) {
            1 -> {
                etEmail.visibility = View.VISIBLE
                etPassword.visibility = View.GONE
                etConfirmPassword.visibility = View.GONE
                etUsername.visibility = View.GONE
                btnNext.visibility = View.VISIBLE
                btnSignup.visibility = View.GONE
                PWText1.visibility = View.GONE
                PWText2.visibility = View.GONE
                RePWText1.visibility = View.GONE
                RePWText2.visibility = View.GONE
                NicknameText1.visibility = View.GONE
                NicknameText2.visibility = View.GONE
            }
            2 -> {
                etEmail.visibility = View.VISIBLE
                etPassword.visibility = View.VISIBLE
                etConfirmPassword.visibility = View.GONE
                etUsername.visibility = View.GONE
                btnSignup.visibility = View.GONE
                etPassword.requestFocus()
//                EmailText1.visibility = View.GONE
                EmailText1.alpha = 0.2f
                EmailText2.alpha = 0.2f
                PWText1.visibility = View.VISIBLE
                PWText2.visibility = View.VISIBLE
            }
            3 -> {
                etEmail.visibility = View.VISIBLE
                etPassword.visibility = View.VISIBLE
                etConfirmPassword.visibility = View.VISIBLE
                etUsername.visibility = View.GONE
                btnNext.visibility = View.VISIBLE
                btnSignup.visibility = View.GONE
                etConfirmPassword.requestFocus()
                PWText1.alpha = 0.2f
                PWText2.alpha = 0.2f
                RePWText1.visibility = View.VISIBLE
                RePWText2.visibility = View.VISIBLE
            }
            4 -> {
                etEmail.visibility = View.VISIBLE
                etPassword.visibility = View.VISIBLE
                etConfirmPassword.visibility = View.VISIBLE
                etUsername.visibility = View.VISIBLE
                btnNext.visibility = View.GONE
                btnSignup.visibility = View.VISIBLE
                etUsername.requestFocus()
                RePWText1.alpha = 0.2f
                RePWText2.alpha = 0.2f
                NicknameText1.visibility = View.VISIBLE
                NicknameText2.visibility = View.VISIBLE

            }
        }
    }
}
