package com.example.mountain.SettingMenu

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import com.example.mountain.R

class NotificationFragment : Fragment() {

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.notice_setting, container, false)

        val backButton = view.findViewById<ImageButton>(R.id.back_button)

        backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return view
    }
}