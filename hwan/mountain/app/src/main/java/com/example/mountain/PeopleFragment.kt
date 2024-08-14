package com.example.mountain

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

class PeopleFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 레이아웃 인플레이트
        return inflater.inflate(R.layout.fragment_people, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val createRoomButton: Button = view.findViewById(R.id.create_room_button)
        val joinRoomButton: Button = view.findViewById(R.id.join_room_button)

        createRoomButton.setOnClickListener {
            // RoomCreateFragment로 이동
            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragment_container, RoomCreateFragment())
            fragmentTransaction.addToBackStack(null)  // 뒤로 가기 가능하게 하기 위함
            fragmentTransaction.commit()
        }

        joinRoomButton.setOnClickListener {
            // RoomJoinFragment로 이동
            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragment_container, RoomJoinFragment())
            fragmentTransaction.addToBackStack(null)  // 뒤로 가기 가능하게 하기 위함
            fragmentTransaction.commit()
        }
    }
}
