package com.example.mountain

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class CalendarAdapter(
    private val onDateClickListener: (Date, View) -> Unit,
    private val holidays: Set<Date> // 공휴일 목록 추가
) : RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {

    private val days = mutableListOf<Date>()
    private val dateFormat = SimpleDateFormat("d", Locale.getDefault())
    private lateinit var context: Context
    private val dates = mutableListOf<Date>()

    private var selectedMonth: Int = -1
    private var selectedYear: Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        context = parent.context // Context 저장
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_calendar_day, parent, false)
        return CalendarViewHolder(view, onDateClickListener, holidays)
    }

    fun getDateAtPosition(position: Int): Date? {
        return if (position in days.indices) {
            days[position]
        } else {
            null
        }
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        val date = days[position]
        holder.bind(date, dateFormat, selectedMonth, selectedYear)
    }

    override fun getItemCount(): Int {
        return days.size
    }

    fun updateDays(newDays: List<Date>, month: Int, year: Int) {
        days.clear()
        days.addAll(newDays)
        selectedMonth = month
        selectedYear = year
        notifyDataSetChanged()
    }

    class CalendarViewHolder(
        view: View,
        private val onDateClickListener: (Date, View) -> Unit,
        private val holidays: Set<Date> // 공휴일 목록 추가
    ) : RecyclerView.ViewHolder(view) {
        private val dayTextView: TextView = view.findViewById(R.id.dayTextView)
        private var currentDate: Date? = null

        init {
            view.setOnClickListener {
                currentDate?.let { date -> onDateClickListener(date, view) }
                Log.d("CalendarAdapter", "Date clicked:)")
            }
        }

        fun bind(date: Date, dateFormat: SimpleDateFormat, selectedMonth: Int, selectedYear: Int) {
            currentDate = date
            dayTextView.text = dateFormat.format(date)

            // 요일과 공휴일에 따라 색상 변경
            val calendar = Calendar.getInstance()
            calendar.time = date
            val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
            val isHoliday = holidays.contains(date)

            val lightRed = ContextCompat.getColor(dayTextView.context, R.color.light_red) // 연빨강
            val lightBlue = ContextCompat.getColor(dayTextView.context, R.color.light_blue) // 연파랑

            when {
                isHoliday -> dayTextView.setTextColor(lightRed) // 공휴일 연빨강
                dayOfWeek == Calendar.SUNDAY -> dayTextView.setTextColor(lightRed) // 일요일 연빨강
                dayOfWeek == Calendar.SATURDAY -> dayTextView.setTextColor(lightBlue) // 토요일 연파랑
                else -> dayTextView.setTextColor(Color.BLACK) // 평일 검정색
            }

            // 현재 월에 속하지 않는 날짜 투명도 조정
            val isCurrentMonth = calendar.get(Calendar.MONTH) == selectedMonth && calendar.get(Calendar.YEAR) == selectedYear
            dayTextView.alpha = if (isCurrentMonth) 1.0f else 0.4f // 현재 월인 경우 불투명, 아닌 경우 투명도 40%
        }
    }
}
