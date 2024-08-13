package com.example.mountain

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment

class HostRoomDetailFragment : Fragment() {

    private lateinit var roomName: TextView
    private lateinit var participantsList: ListView
    private lateinit var inviteButton: Button
    private lateinit var settingsButton: Button
    private lateinit var endRoomButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_host_room_detail, container, false)

        // UI 요소 초기화
        roomName = view.findViewById(R.id.room_name)
        participantsList = view.findViewById(R.id.participants_list)
        inviteButton = view.findViewById(R.id.invite_button)
        settingsButton = view.findViewById(R.id.settings_button)
        endRoomButton = view.findViewById(R.id.leave_button)

        // 방 이름 설정 (전달된 데이터 사용)
        val roomNameText = arguments?.getString(ARG_ROOM_NAME) ?: "Default Room"
        roomName.text = roomNameText

        // 참가자 목록 설정 (예시 데이터)
        val participants = listOf("이범준", "이유찬", "송재곤")
        val participantsAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, participants)
        participantsList.adapter = participantsAdapter

        // 초대 버튼 클릭 리스너 설정
        inviteButton.setOnClickListener {
            Toast.makeText(requireContext(), "초대 기능은 곧 제공됩니다!", Toast.LENGTH_SHORT).show()
        }

        // 설정 버튼 클릭 리스너 설정
        settingsButton.setOnClickListener {
            Toast.makeText(requireContext(), "설정 기능은 곧 제공됩니다!", Toast.LENGTH_SHORT).show()
        }

        // 방 종료 버튼 클릭 리스너 설정
        endRoomButton.setOnClickListener {
            Toast.makeText(requireContext(), "방이 종료되었습니다.", Toast.LENGTH_SHORT).show()
            requireActivity().supportFragmentManager.popBackStack()
        }

        return view
    }

    companion object {
        private const val ARG_ROOM_NAME = "room_name"

        fun newInstance(roomName: String): HostRoomDetailFragment {
            val fragment = HostRoomDetailFragment()
            val args = Bundle().apply {
                putString(ARG_ROOM_NAME, roomName)
            }
            fragment.arguments = args
            return fragment
        }
    }
}
