package com.example.mountain.signUpFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.mountain.R

class PasswordFragment : Fragment() {

    private lateinit var repasswordText: TextView
    private lateinit var repasswordField: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_password, container, false)

        // Initialize views here
        repasswordText = view.findViewById(R.id.passwordText2)
        repasswordField = view.findViewById(R.id.etConfirmPassword)

        return view
    }

    fun getPassword(): String {
        return view?.findViewById<EditText>(R.id.etPassword)?.text.toString() ?: ""
    }

    fun getConfirmPassword(): String {
        return repasswordField.text.toString() ?: ""
    }

    fun changeConfirmTextState() {
        if (::repasswordText.isInitialized && ::repasswordField.isInitialized) {
            repasswordText.visibility = View.VISIBLE
            repasswordField.visibility = View.VISIBLE
        }
    }
}
