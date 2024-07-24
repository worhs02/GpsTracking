package com.example.mountain.signUpFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.example.mountain.R

class PasswordFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_password, container, false)
    }

    fun getPassword(): String {
        return view?.findViewById<EditText>(R.id.etPassword)?.text.toString() ?: ""
    }
    fun getConfirmPassword(): String {
        return view?.findViewById<EditText>(R.id.etConfirmPassword)?.text.toString() ?: ""
    }
}
