package com.example.mountain

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.naver.maps.map.MapView
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.NaverMap
import com.naver.maps.map.CameraUpdate
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.overlay.PolylineOverlay
import com.naver.maps.map.widget.LocationButtonView
import java.util.*
import android.widget.Button

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
    private lateinit var locationButton: LocationButtonView

    private var timer: Timer? = null
    private var timeElapsed: Long = 0
    private var isPaused: Boolean = false

    private var totalDistance: Float = 0f
    private var lastLocation: Location? = null

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var naverMap: NaverMap

    // 네이버 지도에서 기본 지도, 위성 지도, 지형 지도 추가
    private lateinit var basicMapButton: Button
    private lateinit var satelliteButton: Button
    private lateinit var terrainButton: Button

    // PolylineOverlay 및 경로 좌표 리스트
    private lateinit var polyline: PolylineOverlay
    private val pathCoordinates = ArrayList<LatLng>()

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
        locationButton = view.findViewById(R.id.location_button)

        val bottomSheet: LinearLayout = view.findViewById(R.id.bottom_sheet)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)

        // 초기 상태에서 보이는 높이 설정 (px 단위)
        bottomSheetBehavior.peekHeight = 300
        // 확장된 상태에서 남길 오프셋 (px 단위)
        bottomSheetBehavior.expandedOffset = 1000

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        // PolylineOverlay 초기화
        polyline = PolylineOverlay()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                if (isPaused) return
                for (location in locationResult.locations) {
                    if (lastLocation != null) {
                        val distance = lastLocation!!.distanceTo(location)
                        totalDistance += distance
                        exerciseKmValue.text = String.format("%.2f", totalDistance / 1000)
                        exerciseCaloriesValue.text = String.format("%.1f", totalDistance * 0.1)

                        // 현재 위치를 경로에 추가하고 PolylineOverlay 갱신
                        val currentLatLng = LatLng(location.latitude, location.longitude)
                        pathCoordinates.add(currentLatLng)
                        polyline.coords = pathCoordinates
                        polyline.map = naverMap
                    } else {
                        // 첫 위치는 경로의 시작점으로 추가
                        pathCoordinates.add(LatLng(location.latitude, location.longitude))
                    }
                    lastLocation = location

                    // 파란 점 위치 업데이트
                    if (::naverMap.isInitialized) {
                        val locationOverlay = naverMap.locationOverlay
                        locationOverlay.position = LatLng(location.latitude, location.longitude)
                        naverMap.moveCamera(CameraUpdate.scrollTo(locationOverlay.position))
                    }
                }
            }
        }

        playButton.setOnClickListener {
            startExercise()
            Toast.makeText(requireContext(), "운동을 시작했습니다.", Toast.LENGTH_SHORT).show()
        }

        pauseButton.setOnClickListener {
            pauseExercise()
            Toast.makeText(requireContext(), "운동을 일시정지 했습니다.", Toast.LENGTH_SHORT).show()
        }

        stopButton.setOnClickListener {
            stopExercise()
            Toast.makeText(requireContext(), "운동을 종료했습니다.", Toast.LENGTH_SHORT).show()
        }

        // 네이버 지도에서 기본 지도, 위성 지도, 지형 지도 추가
        satelliteButton = view.findViewById(R.id.satellite_button)
        terrainButton = view.findViewById(R.id.terrain_button)
        basicMapButton = view.findViewById(R.id.basic_map_button)

        basicMapButton.setOnClickListener {
            if (::naverMap.isInitialized) {
                naverMap.mapType = NaverMap.MapType.Basic
            }
        }

        satelliteButton.setOnClickListener {
            if (::naverMap.isInitialized) {
                naverMap.mapType = NaverMap.MapType.Satellite
            }
        }

        terrainButton.setOnClickListener {
            if (::naverMap.isInitialized) {
                naverMap.mapType = NaverMap.MapType.Terrain
            }
        }
    }

    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap

        // 줌 버튼 비활성화
        naverMap.uiSettings.isZoomControlEnabled = false

        // 기본 제공되는 현위치 버튼과 NaverMap 연동
        locationButton.map = naverMap

        // 현재 위치로 이동
        moveToCurrentLocation()

        // 현재 위치를 파란색 점으로 표시
        val locationOverlay = naverMap.locationOverlay
        locationOverlay.isVisible = true

        // 위치 추적 모드 설정 (위치를 따라 움직임)
        naverMap.locationTrackingMode = LocationTrackingMode.Follow

        // 위치 업데이트 리스너를 추가하여 위치가 변경될 때마다 파란 점의 위치를 업데이트
        naverMap.addOnLocationChangeListener { location ->
            locationOverlay.position = LatLng(location.latitude, location.longitude)
        }
    }

    private fun moveToCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 권한 요청 필요
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val currentLocation = LatLng(location.latitude, location.longitude)
                naverMap.moveCamera(CameraUpdate.scrollTo(currentLocation))
            } else {
                Toast.makeText(requireContext(), "현재 위치를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startExercise() {
        playButton.visibility = View.GONE
        pauseButton.visibility = View.VISIBLE
        stopButton.visibility = View.GONE  // 멈춤 버튼은 보이지 않음

        isPaused = false
        lastLocation = null
        totalDistance = 0f

        // 경로 초기화
        pathCoordinates.clear()
        polyline.map = null

        // 타이머 시작
        if (timer != null) {
            timer?.cancel()
        }

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
                    }
                }
            }
        }, 1000, 1000)

        startLocationUpdates()
    }

    private fun pauseExercise() {
        playButton.visibility = View.VISIBLE
        pauseButton.visibility = View.GONE
        stopButton.visibility = View.VISIBLE  // 멈춤 버튼을 보이도록 설정
        isPaused = true
        stopLocationUpdates()
    }

    private fun stopExercise() {
        playButton.visibility = View.VISIBLE
        pauseButton.visibility = View.GONE
        stopButton.visibility = View.GONE

        // 타이머 종료 및 초기화
        timer?.cancel()
        timer = null

        isPaused = false
        stopLocationUpdates()

        // 운동 상태 초기화
        timeElapsed = 0
        totalDistance = 0f
        exerciseTimeValue.text = "00:00:00"
        exerciseKmValue.text = "0.00"
        exerciseCaloriesValue.text = "0.0"

        // 추가: 경로 초기화
        pathCoordinates.clear()
        polyline.map = null
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 권한 요청 필요
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            return
        }
        val locationRequest = LocationRequest.create().apply {
            interval = 2000
            fastestInterval = 1000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
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
        stopLocationUpdates()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }
}
