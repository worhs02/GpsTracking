package com.example.mountain.signUpFragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.example.mountain.R
import com.example.mountain.SignupActivity

class EmailFragment : Fragment() {

    private lateinit var verifyCode: EditText
    private lateinit var emailEditText: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_email, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize EditText fields
        verifyCode = view.findViewById(R.id.etVerificationCode)
        emailEditText = view.findViewById(R.id.etEmail)

        // TextWatcher to detect changes in the email field
        emailEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Notify the activity about the change
                (activity as? SignupActivity)?.updateProgress(1)
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    fun getEmail(): String {
        return view?.findViewById<EditText>(R.id.etEmail)?.text.toString() ?: ""
    }

    fun getVerificationCode(): String {
        return verifyCode.text.toString() ?: ""
    }

    fun changeVerifyState() {
        // Ensure `verifyCode` is initialized before using it
        if (::verifyCode.isInitialized) {
            verifyCode.visibility = View.VISIBLE
        }
    }
}
