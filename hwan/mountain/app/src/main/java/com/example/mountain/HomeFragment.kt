package com.example.mountain

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val calendarView = view.findViewById<CalendarView>(R.id.calendar_view)
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val date = "$dayOfMonth/${month + 1}/$year"
            Toast.makeText(requireContext(), "Selected date: $date", Toast.LENGTH_SHORT).show()
        }

        // 설정 버튼 클릭 시 SettingsFragment로 전환
        val settingsButton = view.findViewById<ImageButton>(R.id.settings_button)
        settingsButton.setOnClickListener {
            val settingsFragment = SettingsFragment()
            val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, settingsFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        return view
    }
}
