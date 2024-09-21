package com.example.mountain

import android.app.AlertDialog
import android.content.ContentValues
import android.graphics.Rect
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.*
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mountain.DataModel.CalendarRequest
import com.example.mountain.Server.RetrofitClient
import com.google.firebase.events.Event
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.database.Cursor
import androidx.core.content.ContentProviderCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.LinearLayoutManager


class CalendarFragment : Fragment() {

    private lateinit var calendarRecyclerView: RecyclerView
    private lateinit var monthTextView: TextView
    private lateinit var overlay: RelativeLayout
    private lateinit var overlayContent: RelativeLayout
    private lateinit var selectedDateTextView: TextView
    private lateinit var weekdayHeader: LinearLayout
    private lateinit var addEventButton: Button
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var eventAdapter: EventAdapter

    private val calendarAdapter = CalendarAdapter(
        onDateClickListener = { date, view -> showOverlay(date, view) },
        holidays = setOf(/* 여기에 공휴일 날짜를 추가하세요 */)
    )
    private val calendar = Calendar.getInstance()
    private var initialDate: Date? = null

    private lateinit var gestureDetector: GestureDetector
    private var isSwiping = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_calendar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        databaseHelper = DatabaseHelper(requireContext())
        eventAdapter = EventAdapter()

        calendarRecyclerView = view.findViewById(R.id.calendarRecyclerView)
        monthTextView = view.findViewById(R.id.monthTextView)
        overlay = view.findViewById(R.id.overlay)
        overlayContent = view.findViewById(R.id.overlayContent)
        selectedDateTextView = view.findViewById(R.id.selectedDateTextView)
        weekdayHeader = view.findViewById(R.id.weekdayHeader)
        addEventButton = view.findViewById(R.id.addEventButton)

        calendarRecyclerView.layoutManager = GridLayoutManager(context, 7)
        calendarRecyclerView.adapter = calendarAdapter

        arguments?.getString("selectedDate")?.let { selectedDate ->
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale("ko", "KR"))
            initialDate = dateFormat.parse(selectedDate)
        }

        updateCalendar()

        overlay.setOnClickListener {
            hideOverlay()
        }

        // RecyclerView 초기화
        eventAdapter = EventAdapter()
        val eventRecyclerView = view.findViewById<RecyclerView>(R.id.eventRecyclerView)
        eventRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        eventRecyclerView.adapter = eventAdapter

        addEventButton.setOnClickListener {
            val selectedDate = selectedDateTextView.text.toString() // 오버레이에서 선택된 날짜 가져오기
            addEventButtonTapped(selectedDate)
        }


        // GestureDetector 초기화
        gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            private val SWIPE_THRESHOLD = 5
            private val SWIPE_VELOCITY_THRESHOLD = 5

            override fun onScroll(e1: MotionEvent?, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
                isSwiping = true
                return super.onScroll(e1, e2, distanceX, distanceY)
            }

            override fun onFling(e1: MotionEvent?, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
                if (e1 != null) {
                    val diffX = e2.x - e1.x
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            onSwipeRight()
                        } else {
                            onSwipeLeft()
                        }
                        isSwiping = false
                        return true
                    }
                }
                return false
            }

            override fun onSingleTapUp(e: MotionEvent): Boolean {
                if (!isSwiping) {
                    e?.let {
                        val view = calendarRecyclerView.findChildViewUnder(it.x, it.y)
                        view?.let { clickedView ->
                            val position = calendarRecyclerView.getChildAdapterPosition(clickedView)
                            Log.d("CalendarFragment", "Clicked position: $position")
                            val date = calendarAdapter.getDateAtPosition(position)
                            if (date != null) {
                                showOverlay(date, clickedView)
                                Log.d("CalendarFragment", "Date clicked: ${date.toDateString()}")
                            } else {
                                Log.e("CalendarFragment", "Date not found at position: $position")
                            }
                        }
                    }
                    return true
                }
                return false
            }



        })

        view.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            true
        }

        calendarRecyclerView.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            true
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
        val dateFormat = SimpleDateFormat("MMMM", Locale("ko", "KR"))
        monthTextView.text = dateFormat.format(calendar.time)

        updateWeekdayHeader()

        val days = mutableListOf<Date>()
        val tempCalendar = calendar.clone() as Calendar

        tempCalendar.set(Calendar.DAY_OF_MONTH, 1)
        val firstDayOfMonth = tempCalendar.get(Calendar.DAY_OF_WEEK) - 1
        tempCalendar.add(Calendar.DAY_OF_MONTH, -firstDayOfMonth)

        val lastDayOfMonth = calendar.clone() as Calendar
        lastDayOfMonth.set(Calendar.DAY_OF_MONTH, lastDayOfMonth.getActualMaximum(Calendar.DAY_OF_MONTH))

        val lastDayOfWeek = lastDayOfMonth.get(Calendar.DAY_OF_WEEK) - 1
        val daysToStartOfLastWeek = -lastDayOfWeek
        val startOfLastWeek = lastDayOfMonth.clone() as Calendar
        startOfLastWeek.add(Calendar.DAY_OF_MONTH, daysToStartOfLastWeek)

        val daysToAdd = Calendar.SATURDAY - lastDayOfMonth.get(Calendar.DAY_OF_WEEK)
        val endOfLastWeek = lastDayOfMonth.clone() as Calendar
        endOfLastWeek.add(Calendar.DAY_OF_MONTH, daysToAdd - 1)

        while (true) {
            val currentDay = tempCalendar.time
            days.add(currentDay)
            tempCalendar.add(Calendar.DAY_OF_MONTH, 1)
            if (currentDay.after(endOfLastWeek.time)) {
                break
            }
        }

        val selectedMonth = calendar.get(Calendar.MONTH)
        val selectedYear = calendar.get(Calendar.YEAR)
        calendarAdapter.updateDays(days, selectedMonth, selectedYear)

        calendarRecyclerView.post {
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
        val dateFormat = SimpleDateFormat("yyyy. MM. dd", Locale("ko", "KR"))
        val selectedDate = dateFormat.format(date)
        selectedDateTextView.text = "$selectedDate"

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

        // 이벤트 목록 업데이트
        updateEventsInOverlay(selectedDate)
    }

    private fun sendDateToServer(selectedDate: String, content: String) {
        //user_id 가져오기
        val user_id = 1
        val dateRequest = CalendarRequest(user_id, selectedDate, content)

        RetrofitClient.apiService.sendSelectedDate(dateRequest).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d("API", "Date sent successfully: $selectedDate")
                } else {
                    Log.e("API", "Failed to send date: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("API", "Error: ${t.message}")
            }
        })
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
    fun addEventButtonTapped(selectedDate: String) {
        val eventTitleInput = EditText(requireContext()) // 올바른 컨텍스트 사용
        val dialog = AlertDialog.Builder(requireContext())// Context 변경
            .setTitle("이벤트 추가")
            .setMessage("이벤트 이름을 입력하세요:")
            .setView(eventTitleInput)
            .setPositiveButton("추가") { dialogInterface, _ ->
                val eventTitle = eventTitleInput.text.toString().trim()
                if (eventTitle.isNotEmpty()) {
                    saveEventToDatabase(selectedDate, eventTitle)
                    updateEventsInOverlay(selectedDate)
                } else {
                    Toast.makeText(requireContext(), "이벤트 제목은 비어있을 수 없습니다", Toast.LENGTH_SHORT).show()

                }
                dialogInterface.dismiss()
            }
            .setNegativeButton("취소") { dialogInterface, _ -> dialogInterface.dismiss() }
            .create()

        dialog.show()
    }


    private fun saveEventToDatabase(date: String, title: String) {
        // 데이터베이스 작업을 위한 스레드 또는 Coroutine 사용
        val event = com.example.mountain.dataClass.Event(date, title) // Event 데이터 클래스 생성
        val db = databaseHelper.writableDatabase // 데이터베이스 열기

        // 이벤트를 데이터베이스에 삽입
        val contentValues = ContentValues().apply {
            put("date", event.date) // 날짜
            put("title", event.title) // 제목
        }

        db.insert("events", null, contentValues) // 이벤트 삽입
        db.close() // 데이터베이스 닫기
    }


    private fun updateEventsInOverlay(date: String) {
        val db = databaseHelper.readableDatabase // 데이터베이스 열기
        val cursor = db.query(
            "events", // 테이블 이름
            arrayOf("title"), // 가져올 컬럼
            "date = ?", // 조건
            arrayOf(date), // 조건의 값
            null, null, null // 그룹화, 정렬
        )

        val eventsList = mutableListOf<String>()
        while (cursor.moveToNext()) {
            val title = cursor.getString(0) // 제목 가져오기
            eventsList.add(title) // 리스트에 추가
        }
        cursor.close() // 커서 닫기
        db.close() // 데이터베이스 닫기

        // RecyclerView 업데이트
        updateRecyclerView(eventsList)
    }

    private fun updateRecyclerView(eventsList: List<String>) {
        // RecyclerView 어댑터에 데이터 업데이트
        eventAdapter.updateEvents(eventsList)
    }
}
