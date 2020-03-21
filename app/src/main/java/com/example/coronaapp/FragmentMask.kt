package com.example.coronaapp

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
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.LocationOverlay
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.util.MarkerIcons
import kotlinx.android.synthetic.main.fragment_mask.*
import java.util.ArrayList
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext


class FragmentMask(pharmacy : ArrayList<Pharmacy>, uLL: LatLng) : Fragment(), OnMapReadyCallback, CoroutineScope {

    // 확인할, 확인이 필요한 권한의 목록 생성
    var permission_list = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    lateinit var job: Job

    // 네이버 맵뷰
    private lateinit var mapView: MapView

    // onAttach 를 통해서 Context 를 얻어옴.
    private lateinit var mContext: Context

    // 네이버 FusedLocationSource
    private lateinit var locationSource: FusedLocationSource

    // 네이버 위치 오버레이
    private lateinit var locationOverlay: LocationOverlay
    private lateinit var uiSettings: UiSettings

    // 보고 따라한거.
    var array : ArrayList<Pharmacy>? = pharmacy

    // 사용자의 위도와 경도
    private var userLatLng: LatLng = uLL

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    // 초기화 리소스들이 들어가는 곳
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        job = Job()

        // 사용자의 위치를 가져옴 (미구현)
        //건대사거리
        //userLatLng = LatLng(37.540661, 127.0714121)
        // 우리학교 근처
        //userLatLng = LatLng(37.5479841,127.073755)
        // 중곡역
        //userLatLng = LatLng(37.565535,127.081892)

        //사용자에게 위치 권한 설정을 물어봄.
        checkPermission()

        locationSource =
            FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)

        Log.d("order", "onCreate")
    }

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

    // NaverMap 객체가 준비되면 onMapReady 콜백 메서드가 호출됨. 비동기.
    override fun onMapReady(naverMap: NaverMap) {

        GlobalScope.launch {
            delay(5000)
        }

        Log.d("order", "onMapReady 시작!!")

        // 위치 오버레이 객체를 지정하고 지도에 띄움. (위치오버레이 == 유저)
        locationOverlay = naverMap.locationOverlay
        locationOverlay.isVisible = true
        locationOverlay.position = userLatLng

        //네이버맵에 locationSource 를 지정
        naverMap.locationSource = locationSource

        //네이버맵으로부터 UiSettings 를 가져옴
        uiSettings = naverMap.uiSettings
        // uiSettings.isCompassEnabled = true
        uiSettings.isLocationButtonEnabled = true

        val cameraUpdate = CameraUpdate.scrollAndZoomTo(userLatLng, 14.0)
        naverMap.moveCamera(cameraUpdate)

        val infoWindow = InfoWindow()
        infoWindow.adapter = object : InfoWindow.DefaultTextAdapter(mContext) {
            override fun getText(infoWindow: InfoWindow): CharSequence {
                return infoWindow.marker?.tag as CharSequence? ?: ""
            }
        }

        val markers = mutableListOf<Marker>()
        array!!.forEach {
            markers+=Marker().apply {
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
                if (it.type == "01")
                    tmp = "약국"
                else if (it.type == "02")
                    tmp = "우체국"
                else
                    tmp = "농협"

                if(it.remain_stat == "null")
                    tag = it.name + " ($tmp) " + "\n정보없음"
                else
                    tag = it.name + "($tmp)" + "\n입고시간 : " + it.stock_at

                setOnClickListener {
                    infoWindow.open(this)
                    true
                }

            }
        }

        markers.forEach{ marker ->
            marker.map = naverMap
        }

        Log.d("order", "onMapReady")

    }

    // 사용자에게 권한을 확인할 함수. onCreate 에서 호출, 마시멜로우 이상부터 확인해야함.
    private fun checkPermission() {
        // 실행한 기기의 안드로이드 버전이 마시멜로우 보다 낮으면 검사를 하지 않음.
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return
        }

        for(permission : String in permission_list) {

            var chk = checkCallingOrSelfPermission(mContext, permission)

            if(chk == PackageManager.PERMISSION_DENIED) {
                requestPermissions(permission_list,0)
                break
            }
        }

        Log.d("order", "checkPermission")
    }

    // Layout 을 inflate 하는 곳, View 객체를 얻어서 초기화
    // XML 로 만들어 놓은 View 를 inflater 를 활용하여 생성할 수 있음.
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        Log.d("order", "onCreateView")

        return inflater.inflate(R.layout.fragment_mask, container, false)
    }

    // fragment 생성 이후 호출되는 함수.
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mapView = map_view
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        Log.d("order", "onActivityCreated")

    }

    // Fragment 를 Activity 에 attach 할 때 호출
    // 여기서는 context 를 구하기 위해서 구현함.
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context

        // 사용자의 위치를 기반으 약국의 정보를 가져옴.
        var getPharmacy = GetPharmacy()
        getPharmacy.execute()

        Log.d("order", "onAttach")
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
        Log.d("order", "onResume")
    }

    // 화면이 중지되면 호출되는 함수.
    override fun onPause() {
        super.onPause()
        mapView.onPause()
        Log.d("order", "onPause")
    }

//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//        mapView.onSaveInstanceState(outState)
//    }

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
        job.cancel()
        Log.d("order", "onDestroy")
    }

//    override fun onLowMemory() {
//        super.onLowMemory()
//        mapView.onLowMemory()
//    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }
}