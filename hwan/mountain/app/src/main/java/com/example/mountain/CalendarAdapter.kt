package com.example.mountain

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class CalendarAdapter(private val onDateClickListener: (Date, View) -> Unit) : RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {

    private val days = mutableListOf<Date>()
    private val dateFormat = SimpleDateFormat("d", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_calendar_day, parent, false)
        return CalendarViewHolder(view, onDateClickListener)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        val date = days[position]
        holder.bind(date, dateFormat)
    }

    override fun getItemCount(): Int {
        return days.size
    }

    fun updateDays(newDays: List<Date>) {
        days.clear()
        days.addAll(newDays)
        notifyDataSetChanged()
    }

    class CalendarViewHolder(view: View, private val onDateClickListener: (Date, View) -> Unit) : RecyclerView.ViewHolder(view) {
        private val dayTextView: TextView = view.findViewById(R.id.dayTextView)
        private var currentDate: Date? = null

        init {
            view.setOnClickListener {
                currentDate?.let { date -> onDateClickListener(date, view) }
            }
        }

        fun bind(date: Date, dateFormat: SimpleDateFormat) {
            currentDate = date
            dayTextView.text = dateFormat.format(date)
        }
    }
}
