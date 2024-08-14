package com.example.mountain

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CalendarView
import android.widget.ImageButton
import androidx.fragment.app.Fragment

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val calendarView = view.findViewById<CalendarView>(R.id.calendar_view)
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val date = "$year-${month + 1}-$dayOfMonth"
            showCalendarFragment(date)
        }

        val profileButton = view.findViewById<ImageButton>(R.id.profile_button)
        profileButton.setOnClickListener {
            // 프로필 버튼 클릭 시의 동작
        }

        val sosButton = view.findViewById<Button>(R.id.sos_button)
        sosButton.setOnClickListener {
            // SOS 버튼 클릭 시 SosFragment로 이동
            val sosFragment = SosFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, sosFragment)
                .addToBackStack(null)
                .commit()
        }

        return view
    }

    private fun showCalendarFragment(selectedDate: String) {
        val activity = activity as MainActivity
        activity.setCalendarFragmentWithDate(selectedDate)
    }
}
