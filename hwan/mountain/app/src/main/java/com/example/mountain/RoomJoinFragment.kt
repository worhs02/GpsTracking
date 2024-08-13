package com.example.mountain

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment

class RoomJoinFragment : Fragment() {

    private lateinit var roomSearchInput: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_room_join, container, false)

        // UI 요소 초기화
        roomSearchInput = view.findViewById(R.id.room_search)
        val joinRoomButton: Button = view.findViewById(R.id.join_room_button)
        val cancelButton: Button = view.findViewById(R.id.cancel_button)

        // 참가하기 버튼 클릭 시 ParticipantRoomDetailFragment로 전환
        joinRoomButton.setOnClickListener {
            val roomName = roomSearchInput.text.toString()

            // ParticipantRoomDetailFragment로 데이터 전달
            val participantRoomFragment = ParticipantRoomDetailFragment.newInstance(roomName)
            replaceFragment(participantRoomFragment)
        }

        // 취소 버튼 클릭 시 이전 화면으로 돌아가기
        cancelButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        return view
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = parentFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
}
