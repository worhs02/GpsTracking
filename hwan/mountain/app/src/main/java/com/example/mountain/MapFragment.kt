package com.example.mountain

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.naver.maps.map.MapView
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.NaverMap
import com.naver.maps.map.CameraUpdate
import com.naver.maps.geometry.LatLng

class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mapView: MapView
    private lateinit var startButton: Button
    private lateinit var overlayInfo: LinearLayout
    private lateinit var exerciseTime: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView = view.findViewById(R.id.map_view)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        startButton = view.findViewById(R.id.start_button)
        overlayInfo = view.findViewById(R.id.overlay_info)
        exerciseTime = view.findViewById(R.id.exercise_time)

        startButton.setOnClickListener {
            if (startButton.text == "등산 시작") {
                startButton.text = "등산 종료"
                // 등산 시작 로직 추가
            } else {
                startButton.text = "등산 시작"
                overlayInfo.visibility = View.VISIBLE
                // 운동 시간 및 기타 정보 업데이트 로직 추가
            }
        }
    }

    override fun onMapReady(naverMap: NaverMap) {
        // 네이버 지도가 준비되면 기본 위치를 서울로 설정
        val seoul = LatLng(37.5665, 126.9780)
        naverMap.moveCamera(CameraUpdate.scrollTo(seoul))
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
