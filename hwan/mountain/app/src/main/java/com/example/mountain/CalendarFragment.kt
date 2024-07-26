package com.example.mountain

import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class CalendarFragment : Fragment() {

    private lateinit var calendarRecyclerView: RecyclerView
    private lateinit var monthTextView: TextView
    private lateinit var overlay: RelativeLayout
    private lateinit var overlayContent: RelativeLayout
    private lateinit var selectedDateTextView: TextView
    private lateinit var weekdayHeader: LinearLayout

    private val calendarAdapter = CalendarAdapter(
        onDateClickListener = { date, view -> showOverlay(date, view) },
        holidays = setOf(/* 여기에 공휴일 날짜를 추가하세요 */)
    )
    private val calendar = Calendar.getInstance()
    private var initialDate: Date? = null

    private lateinit var gestureDetector: GestureDetector

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_calendar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        calendarRecyclerView = view.findViewById(R.id.calendarRecyclerView)
        monthTextView = view.findViewById(R.id.monthTextView)
        overlay = view.findViewById(R.id.overlay)
        overlayContent = view.findViewById(R.id.overlayContent)
        selectedDateTextView = view.findViewById(R.id.selectedDateTextView)
        weekdayHeader = view.findViewById(R.id.weekdayHeader)

        calendarRecyclerView.layoutManager = GridLayoutManager(context, 7)
        calendarRecyclerView.adapter = calendarAdapter

        // 날짜 설정
        arguments?.getString("selectedDate")?.let { selectedDate ->
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale("ko", "KR"))
            initialDate = dateFormat.parse(selectedDate)
        }

        updateCalendar()

        overlay.setOnClickListener {
            hideOverlay()
        }

        // GestureDetector 초기화
        gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            private val SWIPE_THRESHOLD = 1
            private val SWIPE_VELOCITY_THRESHOLD = 1

            override fun onFling(
                e1: MotionEvent?,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {

                if (e1 != null && e2 != null) {
                    val diffX = e2.x - e1.x
                    val diffY = e2.y - e1.y
                    if (Math.abs(diffX) > Math.abs(diffY)) {
                        if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                            if (diffX > 0) {
                                onSwipeRight()
                            } else {
                                onSwipeLeft()
                            }
                            return true
                        }
                    }
                }
                return false
            }
        })

        view.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)  // GestureDetector에 이벤트 전달
            true  // 이벤트 처리 완료
        }

        calendarRecyclerView.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)  // GestureDetector에 이벤트 전달
            true  // 이벤트 처리 완료
        }
    }

    private fun onSwipeRight() {
        calendar.add(Calendar.MONTH, -1)
        updateCalendar()
    }

    private fun onSwipeLeft() {
        calendar.add(Calendar.MONTH, 1)
        updateCalendar()
    }

    private fun updateCalendar() {
        val dateFormat = SimpleDateFormat("MMMM yyyy", Locale("ko", "KR"))
        monthTextView.text = dateFormat.format(calendar.time)

        updateWeekdayHeader()

        val days = mutableListOf<Date>()
        val tempCalendar = calendar.clone() as Calendar
        tempCalendar.set(Calendar.DAY_OF_MONTH, 1)
        val firstDayOfMonth = tempCalendar.get(Calendar.DAY_OF_WEEK) - 1
        tempCalendar.add(Calendar.DAY_OF_MONTH, -firstDayOfMonth)

        for (i in 0..41) {
            days.add(tempCalendar.time)
            tempCalendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        calendarAdapter.updateDays(days)

        // RecyclerView 레이아웃이 완전히 준비된 후에 날짜를 스크롤하고 오버레이를 표시
        calendarRecyclerView.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                calendarRecyclerView.viewTreeObserver.removeOnGlobalLayoutListener(this)

                initialDate?.let { date ->
                    val position = days.indexOfFirst { it.toDateString() == date.toDateString() }
                    if (position != -1) {
                        calendarRecyclerView.layoutManager?.let { layoutManager ->
                            layoutManager.scrollToPosition(position)
                            calendarRecyclerView.post {
                                showOverlay(date, calendarRecyclerView.getChildAt(position))
                            }
                        }
                    }
                }
            }
        })
    }

    private fun Date.toDateString(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale("ko", "KR"))
        return dateFormat.format(this)
    }

    private fun updateWeekdayHeader() {
        val daysOfWeek = arrayOf("일", "월", "화", "수", "목", "금", "토")

        for (i in 0 until weekdayHeader.childCount) {
            val textView = weekdayHeader.getChildAt(i) as TextView
            textView.text = daysOfWeek[i]
        }
    }

    private fun showOverlay(date: Date, view: View) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale("ko", "KR"))
        val selectedDate = dateFormat.format(date)
        selectedDateTextView.text = "Selected Date: $selectedDate"

        val rect = Rect()
        view.getGlobalVisibleRect(rect)

        overlay.visibility = View.VISIBLE
        overlayContent.visibility = View.VISIBLE

        val scaleAnimation = ScaleAnimation(
            0.0f, 1.0f,
            0.0f, 1.0f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        ).apply {
            duration = 300
            interpolator = AccelerateDecelerateInterpolator()
        }

        overlayContent.startAnimation(scaleAnimation)
    }

    private fun hideOverlay() {
        val scaleAnimation = ScaleAnimation(
            1.0f, 0.0f,
            1.0f, 0.0f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        ).apply {
            duration = 300
            interpolator = AccelerateDecelerateInterpolator()
            setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationEnd(animation: Animation?) {
                    overlay.visibility = View.GONE
                }

                override fun onAnimationStart(animation: Animation?) {}
                override fun onAnimationRepeat(animation: Animation?) {}
            })
        }

        overlayContent.startAnimation(scaleAnimation)
    }
}
