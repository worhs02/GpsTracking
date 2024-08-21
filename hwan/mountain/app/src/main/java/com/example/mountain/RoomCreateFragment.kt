package com.example.mountain

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.mountain.DataModel.RoomDataRequest
import com.example.mountain.DataModel.RoomDataResponse
import com.example.mountain.Server.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RoomCreateFragment : Fragment() {

    private lateinit var roomNameInput: EditText
    private lateinit var passwordInput: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_room_create, container, false)

        roomNameInput = view.findViewById(R.id.room_name)
        passwordInput = view.findViewById(R.id.password)
        val createRoomButton: Button = view.findViewById(R.id.create_room_button)
        val cancelButton: Button = view.findViewById(R.id.cancel_button)

        createRoomButton.setOnClickListener {
            val roomName = roomNameInput.text.toString()
            val password = passwordInput.text.toString()

            if (roomName.isEmpty()) {
                Toast.makeText(requireContext(), "방 이름을 입력해주세요", Toast.LENGTH_SHORT).show()
            } else {
//                val roomData = RoomDataRequest(roomName, password)
//                RetrofitClient.apiService.createRoom(roomData).enqueue(object : Callback<RoomDataResponse> {
//                    override fun onResponse(call: Call<RoomDataResponse>, response: Response<RoomDataResponse>) {
//                        if (response.isSuccessful) {
//                            Toast.makeText(requireContext(), "방이 생성되었습니다.", Toast.LENGTH_SHORT).show()
//                            // 성공적으로 방을 생성한 후, 다음 화면으로 전환 가능
//                        } else {
//                            Toast.makeText(requireContext(), "방 생성에 실패했습니다.", Toast.LENGTH_SHORT).show()
//                        }
//                    }
//
//                    override fun onFailure(call: Call<RoomDataResponse>, t: Throwable) {
//                        Toast.makeText(requireContext(), "서버와 통신 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
//                    }
//                })
            }
        }

        cancelButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        return view
    }
}
