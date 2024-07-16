package com.example.mountain

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.widget.ImageButton

class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        // 백 버튼 클릭 시 HomeFragment로 돌아가기
        val backButton = view.findViewById<ImageButton>(R.id.back_button)
        backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return view
    }
}
