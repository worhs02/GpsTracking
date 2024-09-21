package com.example.mountain

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.fragment.app.FragmentTransaction
import com.example.mountain.SettingMenu.NotificationFragment
import com.example.mountain.SettingMenu.PermissionFragment
import com.example.mountain.SettingMenu.ProfileFragment

class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        val profileLayout: LinearLayout = view.findViewById(R.id.profile_layout)
        val notificationLayout: LinearLayout = view.findViewById(R.id.notification_layout)
        val permissionLayout: LinearLayout = view.findViewById(R.id.permission_layout)
        val logoutLayout: LinearLayout = view.findViewById(R.id.logout_layout)

        val exerciseRecordsLayout: LinearLayout = view.findViewById(R.id.exercise_records_layout)
        exerciseRecordsLayout.setOnClickListener {
            val exerciseRecordsFragment = ExerciseRecordsFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, exerciseRecordsFragment)
                .addToBackStack(null)
                .commit()
        }

        profileLayout.setOnClickListener {
            // ProfileFragment로 전환
            val profileFragment = ProfileFragment() // 올바르게 생성
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, profileFragment) // container는 fragment가 포함될 뷰의 ID입니다.
            transaction.addToBackStack(null) // Back stack에 추가 (뒤로 가기 기능)
            transaction.commit()
        }

        notificationLayout.setOnClickListener {
            // 다른 Fragment로 전환하거나 특정 작업을 수행
            // 예를 들어 NotificationFragment로 전환
            val notificationFragment = NotificationFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, notificationFragment)
                .addToBackStack(null)
                .commit()
        }

        permissionLayout.setOnClickListener {
            // PermissionFragment로 전환 또는 PermissionActivity 시작
            val permissionFragment = PermissionFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, permissionFragment)
                .addToBackStack(null)
                .commit()
        }

        logoutLayout.setOnClickListener {
            showLogoutConfirmationDialog()
        }

        return view
    }
    private fun showLogoutConfirmationDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("로그아웃")
        builder.setMessage("로그아웃 하시겠습니까?")
        builder.setPositiveButton("확인") { dialog, which ->
            // 로그아웃 처리
            logoutUser()

            // 로그인 화면으로 이동
            val loginIntent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(loginIntent)

            // 현재 Activity 종료
            requireActivity().finish()
        }
        builder.setNegativeButton("취소") { dialog, which ->
            // 대화상자를 닫습니다
            dialog.dismiss()
        }

        val dialog = builder.create()

        // 다이얼로그가 생성된 후 버튼의 텍스트 색상 변경
        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(requireContext().getColor(android.R.color.black))
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(requireContext().getColor(android.R.color.black))
        }

        dialog.show()
    }

    private fun logoutUser() {
        // SharedPreferences 인스턴스 가져오기
        val sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // 로그인 정보를 삭제
        editor.remove("token")    // 예를 들어, 저장된 인증 토큰 삭제

        // 변경사항 저장
        editor.apply()
    }
}
