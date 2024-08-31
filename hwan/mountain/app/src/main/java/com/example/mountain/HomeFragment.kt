package com.example.mountain

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CalendarView
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import me.relex.circleindicator.CircleIndicator3

class HomeFragment : Fragment() {

    private lateinit var viewPager: ViewPager2
    private lateinit var indicator: CircleIndicator3

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        viewPager = view.findViewById(R.id.view_pager)
        indicator = view.findViewById(R.id.circle_indicator)

        // 이미지 및 텍스트 데이터 설정
        val images = listOf(R.drawable.ic_panorama_1, R.drawable.ic_panorama_2)
        val texts = listOf("2024 금산 보곡산골 산벛꽃축제 " +
                           "2024. 4. 13. (토)",
                           "2023 비단고을 산꽃축제" +
                                   "2023. 4. 15. (토) ~ (일)")

        // ViewPager 어댑터 설정
        val adapter = ImageSliderAdapter(images, texts)
        viewPager.adapter = adapter

        // Indicator를 ViewPager와 연결
        indicator.setViewPager(viewPager)

        // 어댑터에 데이터 변경 감지 설정
        adapter.registerAdapterDataObserver(indicator.adapterDataObserver)

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
