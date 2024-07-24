package com.example.mountain

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment

class SosFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sos, container, false)

        val locationText: TextView = view.findViewById(R.id.locationText)
        val call119Button: Button = view.findViewById(R.id.call119Button)

        // initial location text 설정
        locationText.text = "현위치: 123.456 / 123.456"


        // 119 전화 버튼 클릭할 때
        call119Button.setOnClickListener {
            Toast.makeText(context, "119로 연결 중...", Toast.LENGTH_SHORT).show()

            // 전화 통화 로직 구현

        }

        return view
    }
}
