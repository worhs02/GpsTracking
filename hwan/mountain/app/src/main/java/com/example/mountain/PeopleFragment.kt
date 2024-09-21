package com.example.mountain

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mountain.DataModel.RoomDataResponse

class PeopleFragment : Fragment() {

    private lateinit var roomListAdapter: RoomListAdapter
    private lateinit var roomListRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_people, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        roomListRecyclerView = view.findViewById(R.id.room_list)

        roomListAdapter = RoomListAdapter { room ->
            if (room.password != null) {
                showPasswordDialog(room)
            } else {
                navigateToRoomDetail(room)
            }
        }

        roomListRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        roomListRecyclerView.adapter = roomListAdapter

        val createRoomButton: Button = view.findViewById(R.id.create_room_button)
        createRoomButton.setOnClickListener {
            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragment_container, RoomCreateFragment())
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        fetchRooms()
    }

    private fun fetchRooms() {
        val rooms = RoomRepository.getRooms()
        roomListAdapter.submitList(rooms)
    }

    private fun showPasswordDialog(room: RoomDataResponse) {
        val context = requireContext()

        // 다이얼로그의 레이아웃을 프로그래밍 방식으로 생성
        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 40, 50, 10)
        }

        // 타이틀 텍스트뷰 생성
        val titleTextView = TextView(context).apply {
            text = "비밀번호가 필요합니다"
            textSize = 18f
            setTypeface(typeface, Typeface.BOLD)
            setTextColor(Color.BLACK)
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 20)
        }
        layout.addView(titleTextView)

        // 비밀번호 입력 필드 생성
        val passwordEditText = EditText(context).apply {
            hint = "비밀번호 입력"
            setPadding(30, 20, 30, 20)
            setBackgroundResource(android.R.drawable.edit_text)
            inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        layout.addView(passwordEditText)

        // AlertDialog 생성
        val dialog = AlertDialog.Builder(context)
            .setView(layout)
            .setPositiveButton("확인", null)
            .setNegativeButton("취소", null)
            .create()

        // 다이얼로그가 표시될 때 리스너 설정
        dialog.setOnShowListener {
            // 확인 버튼 가져오기
            val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            positiveButton.setTextColor(Color.BLACK)

            // 취소 버튼 가져오기
            val negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            negativeButton.setTextColor(Color.BLACK)

            positiveButton.setOnClickListener {
                val inputPassword = passwordEditText.text.toString()
                if (inputPassword == room.password) {
                    dialog.dismiss()
                    navigateToRoomDetail(room)
                } else {
                    Toast.makeText(requireContext(), "비밀번호가 틀렸습니다", Toast.LENGTH_SHORT).show()
                }
            }

            negativeButton.setOnClickListener {
                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun navigateToRoomDetail(room: RoomDataResponse) {
        val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, ParticipantRoomDetailFragment.newInstance(room.roomName))
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
}
