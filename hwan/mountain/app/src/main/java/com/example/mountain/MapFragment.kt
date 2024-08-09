package com.example.mountain

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.naver.maps.map.MapView
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.NaverMap
import com.naver.maps.map.CameraUpdate
import com.naver.maps.geometry.LatLng
import java.util.*

class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mapView: MapView
    private lateinit var playButton: ImageButton
    private lateinit var pauseButton: ImageButton
    private lateinit var stopButton: ImageButton
    private lateinit var overlayInfo: LinearLayout
    private lateinit var exerciseTimeValue: TextView
    private lateinit var exerciseKmValue: TextView
    private lateinit var exerciseCaloriesValue: TextView
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    private var timer: Timer? = null
    private var timeElapsed: Long = 0
    private var isPaused: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 레이아웃 인플레이트
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView = view.findViewById(R.id.map_view)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        playButton = view.findViewById(R.id.play_button)
        pauseButton = view.findViewById(R.id.pause_button)
        stopButton = view.findViewById(R.id.stop_button)
        overlayInfo = view.findViewById(R.id.overlay_info)
        exerciseTimeValue = view.findViewById(R.id.exercise_time_value)
        exerciseKmValue = view.findViewById(R.id.exercise_km_value)
        exerciseCaloriesValue = view.findViewById(R.id.exercise_calories_value)

        val bottomSheet: LinearLayout = view.findViewById(R.id.bottom_sheet)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)

        // 초기 상태에서 보이는 높이 설정 (px 단위)
        bottomSheetBehavior.peekHeight = 300
        // 확장된 상태에서 남길 오프셋 (px 단위)
        bottomSheetBehavior.expandedOffset = 1000

        playButton.setOnClickListener {
            startExercise()
        }

        pauseButton.setOnClickListener {
            pauseExercise()
        }

        stopButton.setOnClickListener {
            stopExercise()
        }
    }

    override fun onMapReady(naverMap: NaverMap) {
        // 청계산 입구로 기본 위치 설정
        val cheonggyeMountainEntrance = LatLng(37.4483, 127.0565)
        naverMap.moveCamera(CameraUpdate.scrollTo(cheonggyeMountainEntrance))

        // 현재 위치를 파란색 점으로 표시
        val locationOverlay = naverMap.locationOverlay
        locationOverlay.isVisible = true
        locationOverlay.position = cheonggyeMountainEntrance
        locationOverlay.bearing = 0F
    }

    private fun startExercise() {
        playButton.visibility = View.GONE
        pauseButton.visibility = View.VISIBLE
        stopButton.visibility = View.GONE  // 멈춤 버튼은 보이지 않음

        isPaused = false
        timeElapsed = 0

        timer = Timer()
        val handler = Handler(Looper.getMainLooper())
        timer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                if (!isPaused) {
                    handler.post {
                        timeElapsed++
                        val hours = timeElapsed / 3600
                        val minutes = (timeElapsed % 3600) / 60
                        val seconds = timeElapsed % 60
                        exerciseTimeValue.text = String.format("%02d:%02d:%02d", hours, minutes, seconds)

                        // 하드코딩된 값 예시
                        exerciseKmValue.text = String.format("%.2f", timeElapsed * 0.001)
                        exerciseCaloriesValue.text = String.format("%.1f", timeElapsed * 0.1)
                    }
                }
            }
        }, 1000, 1000)
    }

    private fun pauseExercise() {
        playButton.visibility = View.VISIBLE
        pauseButton.visibility = View.GONE
        stopButton.visibility = View.VISIBLE  // 멈춤 버튼을 보이도록 설정
        isPaused = true
    }

    private fun stopExercise() {
        playButton.visibility = View.VISIBLE
        pauseButton.visibility = View.GONE
        stopButton.visibility = View.GONE
        timer?.cancel()
        isPaused = false

        // 운동 상태 초기화
        timeElapsed = 0
        exerciseTimeValue.text = "00:00:00"
        exerciseKmValue.text = "0.00"
        exerciseCaloriesValue.text = "0.0"
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }
}
