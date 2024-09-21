package com.example.mountain

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.mountain.DataModel.RoomDataResponse

class RoomCreateFragment : Fragment() {

    private lateinit var roomNameInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var enablePasswordCheckbox: CheckBox
    private lateinit var maxParticipantsInput: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_room_create, container, false)

        roomNameInput = view.findViewById(R.id.room_name)
        passwordInput = view.findViewById(R.id.password)
        enablePasswordCheckbox = view.findViewById(R.id.enable_password_checkbox)
        maxParticipantsInput = view.findViewById(R.id.max_participants)  // 추가된 최대 인원 수 입력 필드
        val createRoomButton: Button = view.findViewById(R.id.create_room_button)
        val backButton: ImageButton = view.findViewById(R.id.backButton)

        backButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        enablePasswordCheckbox.setOnCheckedChangeListener { _, isChecked ->
            passwordInput.isEnabled = isChecked
            passwordInput.alpha = if (isChecked) 1.0f else 0.5f
        }

        createRoomButton.setOnClickListener {
            val roomName = roomNameInput.text.toString()
            val password = if (enablePasswordCheckbox.isChecked) passwordInput.text.toString() else null
            val maxParticipants = maxParticipantsInput.text.toString().toIntOrNull() ?: 0

            if (roomName.isEmpty()) {
                Toast.makeText(requireContext(), "방 이름을 입력해주세요", Toast.LENGTH_SHORT).show()
            } else if (maxParticipants <= 0) {
                Toast.makeText(requireContext(), "유효한 최대 인원 수를 입력해주세요", Toast.LENGTH_SHORT).show()
            } else {
                createRoom(roomName, password, maxParticipants)
            }
        }

        return view
    }

    private fun createRoom(roomName: String, password: String?, maxParticipants: Int) {
        // 로컬 리스트에 방을 추가
        RoomRepository.addRoom(RoomDataResponse(RoomRepository.getNextRoomId(), roomName, password, 0, maxParticipants))
        Toast.makeText(requireContext(), "방이 생성되었습니다.", Toast.LENGTH_SHORT).show()
        requireActivity().supportFragmentManager.popBackStack()
    }
}
