package com.example.mountain

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide

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

        val mountainAnimationView: ImageView = view.findViewById(R.id.mountain_animation_view)
        val createRoomButton: Button = view.findViewById(R.id.create_room_button)
        val joinRoomButton: Button = view.findViewById(R.id.join_room_button)

        // GIF를 ImageView에 로드
        Glide.with(this)
            .asGif()
            .load(R.drawable.mountain_animation) // drawable 폴더에 있는 GIF 파일 이름
            .into(mountainAnimationView)

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
