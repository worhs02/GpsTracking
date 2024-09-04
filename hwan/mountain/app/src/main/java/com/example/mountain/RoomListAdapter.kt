package com.example.mountain

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mountain.DataModel.RoomDataResponse

class RoomListAdapter(
    private val onRoomClicked: (RoomDataResponse) -> Unit // 클릭 이벤트 리스너 추가
) : RecyclerView.Adapter<RoomListAdapter.RoomViewHolder>() {

    private val rooms = mutableListOf<RoomDataResponse>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_room, parent, false)
        return RoomViewHolder(view)
    }

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        holder.bind(rooms[position], onRoomClicked) // 클릭 이벤트 전달
    }

    override fun getItemCount(): Int = rooms.size

    fun submitList(newRooms: List<RoomDataResponse>?) {
        rooms.clear()
        if (newRooms != null) {
            rooms.addAll(newRooms)
        }
        notifyDataSetChanged()
    }

    class RoomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val roomNameTextView: TextView = itemView.findViewById(R.id.room_name_text_view)
        private val participantCountTextView: TextView = itemView.findViewById(R.id.participant_count_text_view)

        fun bind(room: RoomDataResponse, onRoomClicked: (RoomDataResponse) -> Unit) {
            roomNameTextView.text = room.roomName
            participantCountTextView.text = "${room.participantCount} / ${room.maxParticipants}"

            // 방 항목 클릭 시 이벤트 처리
            itemView.setOnClickListener {
                onRoomClicked(room)
            }
        }
    }
}
