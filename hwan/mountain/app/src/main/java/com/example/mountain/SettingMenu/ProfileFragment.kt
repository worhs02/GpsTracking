package com.example.mountain.SettingMenu

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.mountain.R
import com.example.mountain.Server.ApiService
import com.example.mountain.Server.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileFragment : Fragment() {

    private lateinit var apiService: ApiService
    private var userEmail: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userEmail = arguments?.getString("USER_EMAIL")
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.profile_setting, container, false)

        val backButton = view.findViewById<ImageButton>(R.id.back_button)
        val nicknameEditText = view.findViewById<EditText>(R.id.set_nickname)
        val tagsTextView = view.findViewById<TextView>(R.id.tag_text_Field) // 태그를 표시할 TextView

        // 뒤로 가기 버튼 동작
        backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // RetrofitClient를 통해 ApiService 인스턴스 가져오기
        apiService = RetrofitClient.apiService

        // 서버에서 이메일로 사용자 프로필 가져오기
        userEmail?.let { email ->
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = apiService.getUserProfileByEmail(email)
                    withContext(Dispatchers.Main) {
                        nicknameEditText.setText(response.nickname)
                        tagsTextView.text = response.tags.joinToString{ "#$it" } // 태그를 쉼표로 구분하여 표시
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        return view
    }
}
