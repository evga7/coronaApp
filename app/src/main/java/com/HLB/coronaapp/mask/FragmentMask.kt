package com.HLB.coronaapp.mask

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.HLB.coronaapp.R
import com.HLB.coronaapp.singleton.Singleton
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.*
import com.naver.maps.map.overlay.*
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.util.MarkerIcons
import kotlinx.android.synthetic.main.fragment_mask.*
import java.util.*

class FragmentMask : Fragment(), OnMapReadyCallback {

    // 네이버맵 객체
    private lateinit var navermap: NaverMap

    // 네이버 맵뷰
    private lateinit var mapView: MapView

    private var mapListener: (()->Unit)? = null

    // onAttach 를 통해서 Context 를 얻어옴.
    private lateinit var mContext: Context

    // 네이버 FusedLocationSource
    private lateinit var locationSource: FusedLocationSource

    // 네이버 위치 오버레이 ==> 설명 추가해야 함
    private lateinit var locationOverlay: LocationOverlay

    // 네이버 위치 유아이세팅 ==> 설명 추가해야 함
    private lateinit var uiSettings: UiSettings

    // 마스크 판매처(약국) 정보를 담을 리스트
    private var array : ArrayList<Pharmacy>? = null

    // 사용자의 위도와 경도
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    private var userChoice: LatLng = LatLng(0.0,0.0)
    private var choiceMarker = Marker()

    private val markers = mutableListOf<Marker>()

    private lateinit var cameraUpdate: CameraUpdate

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        if (locationSource.onRequestPermissionsResult(requestCode, permissions,
                grantResults)) {
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    // Fragment 를 Activity 에 attach 할 때 호출
    // 여기서는 context 를 구하기 위해서 구현함.
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    // 초기화 리소스들이 들어가는 곳
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Singleton.nDialog) {
            showNoticeDialog()
            Singleton.nDialog = false
        }

        locationSource =
            FusedLocationSource(this,
                LOCATION_PERMISSION_REQUEST_CODE
            )
    }

    // Layout 을 inflate 하는 곳, View 객체를 얻어서 초기화
    // XML 로 만들어 놓은 View 를 inflater 를 활용하여 생성할 수 있음.
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
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
            "월" -> info = "월요일 : 출생년도 끝 [ 1, 6 ] 구매 가능"
            "화" -> info = "화요일 : 출생년도 끝 [ 2, 7 ] 구매 가능"
            "수" -> info = "수요일 : 출생년도 끝 [ 3, 8 ] 구매 가능"
            "목" -> info = "목요일 : 출생년도 끝 [ 4, 9 ] 구매 가능"
            "금" -> info = "금요일 : 출생년도 끝 [ 0, 5 ] 구매 가능"
            else -> info = "평일에 구매 못하신 분들 구매 가능"
        }

        textInfo.setText(info)

        stockInfoClose.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                stockInfo.visibility = View.INVISIBLE
            }
        })

        stockInfo.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
            }
        })

        searchButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                Singleton.getPharmacyData(userChoice.latitude, userChoice.longitude,mContext)
                stockInfo.visibility = View.VISIBLE
                search.visibility = View.INVISIBLE
                choiceMarker.map = null
            }
        })

        searchCloseButton.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                search.visibility = View.INVISIBLE
                choiceMarker.map = null
            }
        })

        choiceMarker.onClickListener = Overlay.OnClickListener { overlay ->

            if(choiceMarker.map != null) {
                choiceMarker.map = null
                search.visibility = View.INVISIBLE
            }
            true
        }

    }

    // 화면이 중지되면 호출되는 함수.
    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    // Fragment 화면 삭제
    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    // Fragment 완전히 종료할 때 호출
    // Replace 함수나 backward 로 Fragment 를 삭제하는 경우 실행되는 함수.
    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
        array?.clear()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        mapView.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    fun getNaverMap() : NaverMap {
        return navermap
    }

    // NaverMap 객체가 준비되면 onMapReady 콜백 메서드가 호출됨. 비동기.
    override fun onMapReady(naverMap: NaverMap) {
        navermap = naverMap

        mapListener = {
            //네이버맵에 locationSource 를 지정
            navermap.locationSource = locationSource
            navermap.uiSettings.isLocationButtonEnabled = true
            navermap.locationOverlay.isVisible = true

            navermap.setOnMapClickListener{ pointF, coord ->
                Singleton.userLatLng = LatLng(coord.latitude, coord.longitude)
                latitude = coord.latitude
                longitude = coord.longitude
                Singleton.search = true
                cameraUpdate = CameraUpdate.scrollAndZoomTo(LatLng(coord.latitude, coord.longitude), 14.0)
                    .animate(CameraAnimation.Linear)
                navermap.moveCamera(cameraUpdate)
                search.visibility = View.VISIBLE
                userChoice = LatLng(coord.latitude, coord.longitude)
                choiceMarker.iconTintColor = Color.rgb(94, 171, 232)
                choiceMarker.position = LatLng(latitude, longitude)
                choiceMarker.width = 80
                choiceMarker.height = 120
                choiceMarker.map = navermap
            }
        }
        mapListener?.invoke()

        // GPS 가 켜져 있지 않은 경우, 위치 오버레이 객체를 지정하고 지도에 띄움. (위치오버레이 == 유저)
        locationOverlay = naverMap.locationOverlay
        locationOverlay.isVisible = true
        locationOverlay.position = LatLng(latitude, longitude)

        naverMap.locationSource = locationSource
        //네이버맵으로부터 UiSettings 를 가져옴
        uiSettings = naverMap.uiSettings
        // 현위치 버튼을 보이게 함.
        uiSettings.isLocationButtonEnabled = true

        // 사용자로부터 받은 위치를 지도 실행시 보이는 최초 위치로 둠.
        cameraUpdate = CameraUpdate.scrollAndZoomTo(locationOverlay.position, 14.0)
        naverMap.moveCamera(cameraUpdate)

        // 카메라 범위를 대한민국으로 한정
        naverMap.extent = LatLngBounds(LatLng(31.43, 122.37), LatLng(44.35, 132.0))

        // 마커 그리는 함수.
        createMarker()

        /* // 네이버맵 클릭 시 열린 infoWindow 를 닫게 함.
        naverMap.setOnMapClickListener { pointF, latLng ->
            infoWindow.close()
        }
        */
    }

    // 마스크 지도 이용전 보일 공지사항을 보일 다이얼로그
    fun showNoticeDialog() {
        // 여기가 문제.
        val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.notice_dialog, null)
        val alertDialog = AlertDialog.Builder(mContext)
            .setPositiveButton("확 인") { dialog, which ->
            }
            //.setNeutralButton("취소", null)
            .create()

        // 여백 눌러도 창 안없어지게
        alertDialog.setCancelable(false)
        alertDialog.setView(view)
        alertDialog.show()
    }

    // 사용자의 위도와 경도 set
    fun setLatLng(userLatLng: LatLng) {
        latitude = userLatLng.latitude
        longitude = userLatLng.longitude
    }

    // 인근 마스크 판매처의 정보를 담은 리스트를 set
    fun setPharmacyArray(pharmacy : ArrayList<Pharmacy>?) {
        array = pharmacy
    }

    // onMapReady 에서 마커를 그리도록 하는 함수.
    private fun createMarker() {

        if(array == null) {
            return
        }

        // 마커 위에 띄울 약국정보창(말풍선 같이 생김).
        val infoWindow = InfoWindow()
        infoWindow.adapter = object : InfoWindow.DefaultTextAdapter(mContext) {
            override fun getText(infoWindow: InfoWindow): CharSequence {
                return infoWindow.marker?.tag as CharSequence? ?: ""
            }
        }
        // 마커를 클릭 시 infoWindow 로 정보를 보이기 위한 OnClickListener
        val markerListener = Overlay.OnClickListener { overlay ->
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


        if(markers.isNotEmpty()) {
            markers.forEach { marker ->
                marker.map = null
            }
            markers.clear()
        }

        array!!.forEach {

            markers += Marker().apply {

                position = LatLng(it.latitude, it.longitude)
                icon = MarkerIcons.BLACK

                // var stat : String? = null

                if (it.remain_stat == "plenty") {
                    iconTintColor = Color.rgb(48,211, 90)
                }

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
                    tag = it.name + "($tmp)" + "\n주소 : " + it.addr + "\n입고시간 : " + it.stock_at
                }

                this.onClickListener = markerListener
            }
        }

        array!!.clear()

        markers.forEach{ marker ->
            marker.map = navermap
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }

}