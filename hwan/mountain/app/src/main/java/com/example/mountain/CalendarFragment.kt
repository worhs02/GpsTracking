package com.example.mountain

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.CalendarView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import java.text.SimpleDateFormat
import java.util.*

class CalendarFragment : Fragment() {

    private lateinit var calendarView: CalendarView
    private lateinit var overlay: RelativeLayout
    private lateinit var overlayContent: RelativeLayout
    private lateinit var selectedDateTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calendar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        calendarView = view.findViewById(R.id.calendarView)
        overlay = view.findViewById(R.id.overlay)
        overlayContent = view.findViewById(R.id.overlayContent)
        selectedDateTextView = view.findViewById(R.id.selectedDateTextView)

        // Set up the CalendarView
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val date = Calendar.getInstance()
            date.set(year, month, dayOfMonth)
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val selectedDate = dateFormat.format(date.time)

            selectedDateTextView.text = "Selected Date: $selectedDate"

            // Show overlay with expand animation
            showOverlay()
        }

        // Hide overlay when clicked
        overlay.setOnClickListener {
            hideOverlay()
        }
    }

    private fun showOverlay() {
        val scaleAnimation = ScaleAnimation(
            0.0f, 1.0f, // Start and end values for the X axis scaling
            0.0f, 1.0f, // Start and end values for the Y axis scaling
            Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
            Animation.RELATIVE_TO_SELF, 0.5f // Pivot point of Y scaling
        )
        scaleAnimation.duration = 300
        scaleAnimation.interpolator = AccelerateDecelerateInterpolator()
        overlayContent.startAnimation(scaleAnimation)
        overlay.visibility = View.VISIBLE
    }

    private fun hideOverlay() {
        val scaleAnimation = ScaleAnimation(
            1.0f, 0.0f, // Start and end values for the X axis scaling
            1.0f, 0.0f, // Start and end values for the Y axis scaling
            Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
            Animation.RELATIVE_TO_SELF, 0.5f // Pivot point of Y scaling
        )
        scaleAnimation.duration = 300
        scaleAnimation.interpolator = AccelerateDecelerateInterpolator()
        scaleAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                overlay.visibility = View.GONE
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })
        overlayContent.startAnimation(scaleAnimation)
    }
}
