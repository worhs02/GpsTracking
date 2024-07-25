package com.example.mountain

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.ViewPropertyAnimatorCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class CalendarFragment : Fragment() {

    private lateinit var calendarRecyclerView: RecyclerView
    private lateinit var monthTextView: TextView
    private lateinit var prevButton: Button
    private lateinit var nextButton: Button
    private lateinit var overlay: RelativeLayout
    private lateinit var overlayContent: RelativeLayout
    private lateinit var selectedDateTextView: TextView

    private val calendarAdapter = CalendarAdapter { date, view ->
        showOverlay(date, view)
    }
    private val calendar = Calendar.getInstance()

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

        calendarRecyclerView.layoutManager = GridLayoutManager(context, 7)
        calendarRecyclerView.adapter = calendarAdapter

        updateCalendar()

        overlay.setOnClickListener {
            hideOverlay()
        }
    }

    private fun updateCalendar() {
        val dateFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        monthTextView.text = dateFormat.format(calendar.time)

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
    }

    private fun showOverlay(date: Date, view: View) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val selectedDate = dateFormat.format(date)
        selectedDateTextView.text = "Selected Date: $selectedDate"

        // Get the global visible rect of the clicked view
        val rect = Rect()
        view.getGlobalVisibleRect(rect)

        // Make the overlay visible before starting the animation
        overlay.visibility = View.VISIBLE
        overlayContent.visibility = View.VISIBLE


        // Start the scale animation to zoom in to the center
        val scaleAnimation = ScaleAnimation(
            0.0f, 1.0f, // From 0 to 1 scale
            0.0f, 1.0f, // From 0 to 1 scale
            Animation.RELATIVE_TO_SELF, 0.5f, // Pivot at the center of the view
            Animation.RELATIVE_TO_SELF, 0.5f
        ).apply {
            duration = 300
            interpolator = AccelerateDecelerateInterpolator()
            setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {
                    // Center the overlay content in the screen
                    val params = overlayContent.layoutParams as RelativeLayout.LayoutParams
                    params.addRule(RelativeLayout.CENTER_IN_PARENT)
                    overlayContent.layoutParams = params
                }

                override fun onAnimationEnd(animation: Animation?) {
                    // Optionally, handle any action needed after the animation ends
                }

                override fun onAnimationRepeat(animation: Animation?) {
                    // No action needed
                }
            })
        }

        overlayContent.startAnimation(scaleAnimation)
    }

    private fun hideOverlay() {
        // Start the scale animation to zoom out
        val scaleAnimation = ScaleAnimation(
            1.0f, 0.0f, // From 1 to 0 scale
            1.0f, 0.0f, // From 1 to 0 scale
            Animation.RELATIVE_TO_SELF, 0.5f, // Pivot at the center of the view
            Animation.RELATIVE_TO_SELF, 0.5f
        ).apply {
            duration = 300
            interpolator = AccelerateDecelerateInterpolator()
            setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {
                    // No action needed
                }

                override fun onAnimationEnd(animation: Animation?) {
                    overlay.visibility = View.GONE
                }

                override fun onAnimationRepeat(animation: Animation?) {
                    // No action needed
                }
            })
        }

        overlayContent.startAnimation(scaleAnimation)
    }
}