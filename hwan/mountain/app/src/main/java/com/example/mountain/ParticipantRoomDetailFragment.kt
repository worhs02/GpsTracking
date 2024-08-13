package com.example.mountain

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment

class ParticipantRoomDetailFragment : Fragment() {

    private lateinit var roomName: TextView
    private lateinit var participantsList: ListView
    private lateinit var chatInput: EditText
    private lateinit var sendButton: Button
    private lateinit var leaveRoomButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_participant_room_detail, container, false)

        // UI 요소 초기화
        roomName = view.findViewById(R.id.room_name)
        participantsList = view.findViewById(R.id.participants_list)
        leaveRoomButton = view.findViewById(R.id.leave_button)

        // 방 이름 설정 (전달된 데이터 사용)
        val roomNameText = arguments?.getString(ARG_ROOM_NAME) ?: "Default Room"
        roomName.text = roomNameText

        // 참가자 목록 설정 (예시 데이터)
        val participants = listOf("이범준", "이유찬", "송재곤")
        val participantsAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, participants)
        participantsList.adapter = participantsAdapter


        // 나가기 버튼 클릭 리스너 설정
        leaveRoomButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        return view
    }

    companion object {
        private const val ARG_ROOM_NAME = "room_name"

        fun newInstance(roomName: String): ParticipantRoomDetailFragment {
            val fragment = ParticipantRoomDetailFragment()
            val args = Bundle().apply {
                putString(ARG_ROOM_NAME, roomName)
            }
            fragment.arguments = args
            return fragment
        }
    }
}
