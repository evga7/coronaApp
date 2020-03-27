package com.example.coronaapp.Mask

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.PermissionChecker.checkCallingOrSelfPermission
import androidx.fragment.app.Fragment
import com.example.coronaapp.R
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.LocationOverlay
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.util.MarkerIcons
import kotlinx.android.synthetic.main.fragment_mask.*
import java.util.*


class FragmentMask : Fragment(), OnMapReadyCallback {

    // 확인할, 확인이 필요한 권한의 목록 생성
    var permission_list = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    // 네이버 맵뷰
    private lateinit var mapView: MapView

    // 네이버맵 객체
    private lateinit var navermap: NaverMap

    // onAttach 를 통해서 Context 를 얻어옴.
    private lateinit var mContext: Context

    // 네이버 FusedLocationSource
    private lateinit var locationSource: FusedLocationSource

    // 네이버 위치 오버레이
    private lateinit var locationOverlay: LocationOverlay
    private lateinit var uiSettings: UiSettings

    // 마스크 판매처(약국) 정보를 담을 리스트
    var array : ArrayList<Pharmacy>? = null

    // 사용자의 위도와 경도
    private var latitude: Double? = null
    private var longitude: Double? = null

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        if (locationSource.onRequestPermissionsResult(requestCode, permissions,
                grantResults)) {
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d("order", "onRequestPermissionsResult")
    }

    // Fragment 를 Activity 에 attach 할 때 호출
    // 여기서는 context 를 구하기 위해서 구현함.
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        Log.d("order", "onAttach")
    }

    // 초기화 리소스들이 들어가는 곳
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //사용자에게 위치 권한 설정을 물어봄.
        checkPermission()

        locationSource =
            FusedLocationSource(this,
                LOCATION_PERMISSION_REQUEST_CODE
            )

        Log.d("order", "onCreate")
    }

    // Layout 을 inflate 하는 곳, View 객체를 얻어서 초기화
    // XML 로 만들어 놓은 View 를 inflater 를 활용하여 생성할 수 있음.
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d("order", "onCreateView")
        return inflater.inflate(R.layout.fragment_mask, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView = map_view
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
    }

    // Fragment 화면에 표시될 때 호출
    override fun onStart() {
        super.onStart()
        mapView.onStart()
        Log.d("order", "onStart")
    }

    // Fragment 화면에 로딩이 끝났을 때 호출
    override fun onResume() {
        super.onResume()
        mapView.onResume()

        // 구매 가능한 출생년도 TextView 에 보이기
        val calenndar: Calendar = Calendar.getInstance()
        val days = listOf<String>("일", "월","화", "수", "목", "금", "토")
        val day: String = days[calenndar.get(Calendar.DAY_OF_WEEK) - 1]
        var info: String? = null

        when(day) {
            "월" -> info = "출생년도 끝 [ 1, 6 ] 구매 가능"
            "화" -> info = "출생년도 끝 [ 2, 7 ] 구매 가능"
            "수" -> info = "출생년도 끝 [ 3, 8 ] 구매 가능"
            "목" -> info = "출생년도 끝 [ 4, 9 ] 구매 가능"
            "금" -> info = "출생년도 끝 [ 0, 5 ] 구매 가능"
            else -> info = "평일에 구매하지 못하신 분들이 구매하는 날"
        }

        textInfo.setText(info)

        //mapView.getMapAsync(this)
        Log.d("order", "onResume")
    }

    // 화면이 중지되면 호출되는 함수.
    override fun onPause() {
        super.onPause()
        mapView.onPause()
        Log.d("order", "onPause")
    }

    // Fragment 화면 삭제
    override fun onStop() {
        super.onStop()
        mapView.onStop()
        Log.d("order", "onStop")
    }

    // Fragment 완전히 종료할 때 호출
    // Replace 함수나 backward 로 Fragment 를 삭제하는 경우 실행되는 함수.
    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
        array?.clear()
        Log.d("order", "onDestroy")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    // NaverMap 객체가 준비되면 onMapReady 콜백 메서드가 호출됨. 비동기.
    override fun onMapReady(naverMap: NaverMap) {

        navermap = naverMap

        Log.d("order", "onMapReady 시작!!")

        // 위치 오버레이 객체를 지정하고 지도에 띄움. (위치오버레이 == 유저)
        locationOverlay = naverMap.locationOverlay
        locationOverlay.isVisible = true
        locationOverlay.position = LatLng(latitude!!, longitude!!)

        //네이버맵에 locationSource 를 지정
        naverMap.locationSource = locationSource

        //네이버맵으로부터 UiSettings 를 가져옴
        uiSettings = naverMap.uiSettings

        // uiSettings.isCompassEnabled = true
        uiSettings.isLocationButtonEnabled = true

        // 사용자로부터 받은 위치를 지도 실행시 보이는 최초 위치로 둠.
        val cameraUpdate = CameraUpdate.scrollAndZoomTo(locationOverlay.position, 14.0)
        naverMap.moveCamera(cameraUpdate)

        // 마커 위에 띄울 정보창.
        val infoWindow = InfoWindow()

        infoWindow.adapter = object : InfoWindow.DefaultTextAdapter(mContext) {
            override fun getText(infoWindow: InfoWindow): CharSequence {
                return infoWindow.marker?.tag as CharSequence? ?: ""
            }
        }

        // 네이버맵 클릭 시 열린 infoWindow 를 닫게 함.
        naverMap.setOnMapClickListener { pointF, latLng -> 
            infoWindow.close()
        }

        //마커를 클릭 시 infoWindow 로 정보를 보이기 위한 OnClickListener
        val listener = Overlay.OnClickListener { overlay ->
            val marker = overlay as Marker

            if(marker.infoWindow == null) {
                // 현재 마커에 정보 창이 열려있지 않을 경우 엶
                infoWindow.open(marker)
            }
            else {
                infoWindow.close()
            }

            true
        }

        val markers = mutableListOf<Marker>()
        array!!.forEach {

            markers += Marker().apply {

                position = LatLng(it.latitude, it.longitude)
                icon = MarkerIcons.BLACK

                if (it.remain_stat == "plenty")
                    iconTintColor = Color.GREEN

                else if(it.remain_stat == "some") {
                    iconTintColor = Color.YELLOW
                }

                else if(it.remain_stat == "few") {
                    iconTintColor = Color.RED
                }

                else {
                    iconTintColor = Color.GRAY
                }

                width = 50
                height = 80

                var tmp : String? = null

                if (it.type == "01") {
                    tmp = "약국"
                }
                else if (it.type == "02") {
                    tmp = "우체국"
                }
                else {
                    tmp = "농협"
                }

                if(it.remain_stat == "null") {
                    tag = it.name + " ($tmp) " + "\n정보없음"
                }
                else {
                    tag = it.name + "($tmp)" + "\n입고시간 : " + it.stock_at
                }

                this.onClickListener = listener
            }
        }

        markers.forEach{ marker ->

            marker.map = navermap

        }

        Log.d("order", "onMapReady")
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }

    // 사용자에게 권한을 확인할 함수. onCreate 에서 호출, 마시멜로우 이상부터 확인해야함.
    private fun checkPermission() {
        // 실행한 기기의 안드로이드 버전이 마시멜로우 보다 낮으면 검사를 하지 않음.
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return
        }

        for(permission : String in permission_list) {

            val chk = checkCallingOrSelfPermission(mContext, permission)

            if(chk == PackageManager.PERMISSION_DENIED) {
                requestPermissions(permission_list,0)
                break
            }
        }

        Log.d("order", "checkPermission")
    }

    // 사용자의 위도와 경도 set
    fun setLatLng(userLatLng: LatLng) {

        latitude = userLatLng.latitude
        longitude = userLatLng.longitude
    }

    // 인근 마스크 판매처의 정보를 담은 리스트를 set
    fun setPharmacyArray(pharmacy : ArrayList<Pharmacy>) {

        array = pharmacy
    }
}