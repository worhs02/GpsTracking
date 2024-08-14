package com.example.mountain

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.mountain.SettingMenu.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 이메일을 Intent에서 가져오기
        val email = intent.getStringExtra("USER_EMAIL")

        // ProfileFragment에 이메일을 전달하여 프로필 로드하기
        if (email != null) {
            val profileFragment = ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString("USER_EMAIL", email)
                }
            }

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, profileFragment)
                .commit()
        }

        loadFragment(HomeFragment()) // 기본 프래그먼트를 로드합니다.

        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            var selectedFragment: Fragment? = null
            when (item.itemId) {
                R.id.nav_home -> {
                    selectedFragment = HomeFragment()
                }
                R.id.nav_map -> {
                    selectedFragment = MapFragment()
                }
                R.id.nav_calendar -> {
                    selectedFragment = CalendarFragment()
                }
                R.id.nav_people -> {
                    selectedFragment = PeopleFragment()
                }
                R.id.nav_setting -> {
                    selectedFragment = SettingsFragment()
                }
            }
            if (selectedFragment != null) {
                loadFragment(selectedFragment)
            }
            true
        }
    }

    fun setCalendarFragmentWithDate(selectedDate: String) {
        bottomNavigationView.selectedItemId = R.id.nav_calendar
        val calendarFragment = CalendarFragment().apply {
            arguments = Bundle().apply {
                putString("selectedDate", selectedDate)
            }
        }
        loadFragment(calendarFragment)
    }

    private fun loadFragment(fragment: Fragment) {
        // 프래그먼트를 로드합니다.
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
