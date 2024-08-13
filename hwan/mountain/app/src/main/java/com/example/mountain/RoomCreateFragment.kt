package com.example.mountain

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment

class RoomCreateFragment : Fragment() {

    private lateinit var roomNameInput: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_room_create, container, false)

        // UI 요소 초기화
        roomNameInput = view.findViewById(R.id.room_name)
        val createRoomButton: Button = view.findViewById(R.id.create_room_button)
        val cancelButton: Button = view.findViewById(R.id.cancel_button)

        // 방 만들기 버튼 클릭 시 HostRoomDetailFragment로 전환
        createRoomButton.setOnClickListener {
            val roomName = roomNameInput.text.toString()

            // HostRoomDetailFragment로 데이터 전달
            val hostRoomFragment = HostRoomDetailFragment.newInstance(roomName)
            replaceFragment(hostRoomFragment)
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
