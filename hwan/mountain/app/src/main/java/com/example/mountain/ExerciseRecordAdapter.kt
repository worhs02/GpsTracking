package com.example.mountain

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ExerciseRecordAdapter(private val records: List<ExerciseRecord>) :
    RecyclerView.Adapter<ExerciseRecordAdapter.ExerciseRecordViewHolder>() {

    class ExerciseRecordViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val distanceText: TextView = view.findViewById(R.id.distance_text)
        val caloriesText: TextView = view.findViewById(R.id.calories_text)
        val timeText: TextView = view.findViewById(R.id.time_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseRecordViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.exercise_record_item, parent, false)
        return ExerciseRecordViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExerciseRecordViewHolder, position: Int) {
        val record = records[position]
        holder.distanceText.text = "거리: ${record.distance} km"
        holder.caloriesText.text = "칼로리: ${record.calories} kcal"
        holder.timeText.text = "시간: ${record.time}"
    }

    override fun getItemCount() = records.size
}
