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
    private lateinit var startButton: ImageButton
    private lateinit var overlayInfo: LinearLayout
    private lateinit var exerciseTimeValue: TextView
    private lateinit var exerciseKmValue: TextView
    private lateinit var exerciseCaloriesValue: TextView
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    private var timer: Timer? = null
    private var timeElapsed: Long = 0

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

        startButton = view.findViewById(R.id.start_button)
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

        startButton.setOnClickListener {
            if (startButton.drawable.constantState == resources.getDrawable(R.drawable.ic_play).constantState) {
                startExercise()
            } else if (startButton.drawable.constantState == resources.getDrawable(R.drawable.ic_pause).constantState) {
                showStopButton()
            } else {
                stopExercise()
            }
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
        startButton.setImageResource(R.drawable.ic_pause)  // 일시정지 버튼 아이콘으로 변경
        timeElapsed = 0

        timer = Timer()
        val handler = Handler(Looper.getMainLooper())
        timer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
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
        }, 1000, 1000)
    }

    private fun showStopButton() {
        startButton.setImageResource(R.drawable.ic_stop)  // 정지 버튼 아이콘으로 변경
    }

    private fun stopExercise() {
        startButton.setImageResource(R.drawable.ic_play)  // 플레이 버튼 아이콘으로 변경
        timer?.cancel()
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
