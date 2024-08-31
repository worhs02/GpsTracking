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
import com.google.android.gms.location.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.naver.maps.map.*
import com.naver.maps.geometry.LatLng
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
    private var isStarted: Boolean = false  // 운동이 시작되었는지 여부를 나타내는 플래그

    private var totalDistance: Float = 0f
    private var lastLocation: Location? = null
    private var lastAltitude: Double? = null

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var naverMap: NaverMap

    // 지도 타입 버튼들
    private lateinit var basicMapButton: Button
    private lateinit var satelliteButton: Button
    private lateinit var terrainButton: Button

    // PolylineOverlay 및 경로 좌표 리스트
    private lateinit var polyline: PolylineOverlay
    private val pathCoordinates = ArrayList<LatLng>()

    // 성인 평균 체중 (남성, 여성)
    private val maleWeight = 70f
    private val femaleWeight = 60f

    // MET 값
    private val walkingMET = 3.5
    private val joggingMET = 7.0
    private val runningMET = 11.0

    // 경사도에 따른 MET 값 가중치
    private val inclineWeight = 1.5

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
        bottomSheetBehavior.peekHeight = 300
        bottomSheetBehavior.expandedOffset = 1000

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        polyline = PolylineOverlay()
        polyline.color = resources.getColor(android.R.color.holo_green_light, null) // Polyline 색상 녹색으로 설정

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                if (isPaused) return
                for (location in locationResult.locations) {
                    if (lastLocation != null) {
                        val distance = lastLocation!!.distanceTo(location)
                        if (distance > 1) { // 거리가 1미터 이상인 경우에만 계산
                            totalDistance += distance
                            exerciseKmValue.text = String.format("%.2f", totalDistance / 1000)

                            val altitudeDifference = location.altitude - (lastAltitude ?: location.altitude)
                            lastAltitude = location.altitude

                            val elapsedTimeInHours = timeElapsed / 3600.0
                            val speed = (totalDistance / 1000) / elapsedTimeInHours
                            val incline = altitudeDifference / distance

                            var metValue = when {
                                speed < 4 -> walkingMET
                                speed < 10 -> joggingMET
                                else -> runningMET
                            }

                            if (incline > 0.05) {
                                metValue *= inclineWeight
                            }

                            // 칼로리 계산 (남성 기준, 사용자 맞춤 설정 가능)
                            val caloriesBurned = metValue * maleWeight * elapsedTimeInHours
                            exerciseCaloriesValue.text = String.format("%.1f", caloriesBurned)

                            val currentLatLng = LatLng(location.latitude, location.longitude)
                            pathCoordinates.add(currentLatLng)
                            polyline.coords = pathCoordinates
                            polyline.map = naverMap
                        }
                    } else {
                        // 처음 위치 설정
                        pathCoordinates.add(LatLng(location.latitude, location.longitude))
                        lastAltitude = location.altitude
                    }
                    lastLocation = location

                    // 위치 오버레이 업데이트
                    if (::naverMap.isInitialized) {
                        val locationOverlay = naverMap.locationOverlay
                        locationOverlay.position = LatLng(location.latitude, location.longitude)
                        locationOverlay.bearing = location.bearing
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
        naverMap.uiSettings.isZoomControlEnabled = false
        locationButton.map = naverMap // LocationButtonView와 NaverMap 연결

        // 위치 오버레이를 통해 파란 점을 현재 위치에 표시
        val locationOverlay = naverMap.locationOverlay
        locationOverlay.isVisible = true

        // 위치 추적 모드를 None으로 설정하여 자동으로 카메라가 이동하지 않도록 설정
        naverMap.locationTrackingMode = LocationTrackingMode.None

        // 위치 버튼 클릭 시 현재 위치로 이동
        locationButton.setOnClickListener {
            moveToCurrentLocation()
        }

        moveToCurrentLocation()
    }

    private fun moveToCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
        if (!isStarted) {
            // 운동이 처음 시작되는 경우에만 초기화
            playButton.visibility = View.GONE
            pauseButton.visibility = View.VISIBLE
            stopButton.visibility = View.GONE

            isPaused = false
            isStarted = true
            lastLocation = null
            lastAltitude = null
            totalDistance = 0f
            timeElapsed = 0

            pathCoordinates.clear()
            polyline.map = null

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
        } else if (isPaused) {
            // 운동이 일시 정지 후 재개될 때
            playButton.visibility = View.GONE
            pauseButton.visibility = View.VISIBLE
            stopButton.visibility = View.GONE

            isPaused = false
            startLocationUpdates()
        }
    }

    private fun pauseExercise() {
        playButton.visibility = View.VISIBLE
        pauseButton.visibility = View.GONE
        stopButton.visibility = View.VISIBLE
        isPaused = true
        stopLocationUpdates()
    }

    private fun stopExercise() {
        playButton.visibility = View.VISIBLE
        pauseButton.visibility = View.GONE
        stopButton.visibility = View.GONE

        timer?.cancel()
        timer = null

        isPaused = false
        isStarted = false  // 운동이 종료됨을 표시
        stopLocationUpdates()

        saveExerciseRecord() // 운동 데이터 저장

        timeElapsed = 0
        totalDistance = 0f
        exerciseTimeValue.text = "00:00:00"
        exerciseKmValue.text = "0.00"
        exerciseCaloriesValue.text = "0.0"

        pathCoordinates.clear()
        polyline.map = null
    }

    private fun saveExerciseRecord() {
        // 운동 데이터 저장 로직
        val exerciseRecord = ExerciseRecord(
            distance = totalDistance / 1000,
            calories = exerciseCaloriesValue.text.toString().toFloat(),
            time = exerciseTimeValue.text.toString(),
            path = pathCoordinates
        )

        // 운동 기록을 SharedPreferences, 데이터베이스, 또는 정적 리스트에 저장
        ExerciseRecordRepository.saveRecord(exerciseRecord)
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
