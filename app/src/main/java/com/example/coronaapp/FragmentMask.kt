package com.example.coronaapp

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.AsyncTask
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
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import java.net.URL
import java.util.ArrayList


class FragmentMask(uLatLng: LatLng) : Fragment(), OnMapReadyCallback {

    // 확인할, 확인이 필요한 권한의 목록 생성
    var permission_list = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    // private var gpsTracker: GpsTracker? = null
    //val pharmacy = ArrayList<Pharmacy>()

    val markers: MutableList<Marker> = mutableListOf<Marker>()

    private lateinit var marker: Marker

    // 네이버 맵뷰
    private lateinit var mapView: MapView

    private lateinit var navermap: NaverMap

    val pharmacy = ArrayList<Pharmacy>()

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
    private var latitude: Double? = uLatLng.latitude
    private var longitude: Double? = uLatLng.longitude

    // 초기화 리소스들이 들어가는 곳
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //mapView = map_view

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

        navermap = naverMap

        Log.d("order", "onMapReady 시작!!")
        // naverMap.locationTrackingMode = LocationTrackingMode.Follow

        getPharmacyData(latitude.toString(), longitude.toString(), mContext)
        array = pharmacy

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

        val cameraUpdate = CameraUpdate.scrollAndZoomTo(locationOverlay.position, 14.0)
        naverMap.moveCamera(cameraUpdate)

        val infoWindow = InfoWindow()
        infoWindow.adapter = object : InfoWindow.DefaultTextAdapter(mContext) {
            override fun getText(infoWindow: InfoWindow): CharSequence {
                return infoWindow.marker?.tag as CharSequence? ?: ""
            }
        }

        // markers = mutableListOf<Marker>()

        if (array.isNullOrEmpty())
            Log.d("array", "array 가 비었습니다.")

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

                setOnClickListener {
                    infoWindow.open(this)
                    true
                }

            }
        }

        if (array.isNullOrEmpty())
            Log.d("array", "array 가 비었습니다.")
//        markers.forEach{ marker ->
//            marker.map = naverMap
//        }

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

        marker = Marker()
        marker.position=LatLng(37.5479841,127.073755)
        return inflater.inflate(R.layout.fragment_mask, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView = map_view
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
    }

    // fragment 생성 이후 호출되는 함수.
//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//        mapView = map_view
//        mapView.onCreate(savedInstanceState)
//        mapView.getMapAsync(this)
//        Log.d("order", "onActivityCreated")
//    }

    // Fragment 를 Activity 에 attach 할 때 호출
    // 여기서는 context 를 구하기 위해서 구현함.
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
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
        //mapView.getMapAsync(this)
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
        array?.clear()
        markers.clear()
        Log.d("order", "onDestroy")
    }

//    override fun onLowMemory() {
//        super.onLowMemory()
//        mapView.onLowMemory()
//    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }

    fun getPharmacyData(latitude:String, longitude:String, mcontext: Context) {

        class GetPharmacy: AsyncTask<Void, Void, Void>() {

            // 새로운 스레드가 발생하여 일반 스레드에서 처리가 됨.
            override fun doInBackground(vararg params: Void?): Void? {

                Log.d("order", "doInBackground 시")

                // 어린이대공원역사거리 37.5479841,127.073755
                // 중곡역 37.565535,127.081892
                // https://8oi9s0nnth.apigw.ntruss.com/corona19-masks/v1/storesByGeo/json?lat=34&lng=125&m=5000
                var temp: String=""
                try {
                    //val stream = URL("https://8oi9s0nnth.apigw.ntruss.com/corona19-masks/v1/storesByGeo/json?lat=37.540661&lng127.0714121&m=500").openStream()
                    //val stream = URL("https://8oi9s0nnth.apigw.ntruss.com/corona19-masks/v1/storesByGeo/json?lat=37.5479841&lng127.073755&m=1000").openStream()
                    //val stream = URL("https://8oi9s0nnth.apigw.ntruss.com/corona19-masks/v1/storesByGeo/json?lat=37.565535&lng127.081892&m=1000").openStream()
                    val stream = URL("https://8oi9s0nnth.apigw.ntruss.com/corona19-masks/v1/storesByGeo/json?lat=37.565535&lng=127.073755&m=1000").openStream()
                    val read = BufferedReader(InputStreamReader(stream, "UTF-8"))
                    //temp = read.readLine()
                    var line:String?=read.readLine()
                    while(line!=null){
                        temp+=(line);
                        line = read.readLine()
                    }
                }
                catch (e : Exception){
                    Log.e("error", e.toString())
                }

                val json = JSONObject(temp)
                try{
                    var str = json.get("message").toString()
                    pharmacy.add(Pharmacy("none", 0.0, 0.0, "none", "none", "none", "none"))
                    return null
                }
                catch (e: java.lang.Exception) {
                    Log.e("Error", e.toString())
                }

                val count = json.get("count").toString().toInt()
                if (count != 0) {

                    val upperArray = json.getJSONArray("stores")

                    for(i in 0..(count - 1)) {
                        val upperObjet = upperArray.getJSONObject(i)
                        Log.d("CHECK", upperObjet.toString())
                        pharmacy.add(Pharmacy(
                            upperObjet.getString("addr"),
                            upperObjet.getString("lat").toDouble(),
                            upperObjet.getString("lng").toDouble(),
                            upperObjet.getString("name"),
                            upperObjet.getString("remain_stat"),
                            upperObjet.getString("stock_at"),
                            upperObjet.getString("type")
                        ))
                    }

                } else {
                    pharmacy.add(Pharmacy("none", 0.0, 0.0, "none", "none", "none", "none"))
                }

                Log.e("pharmacy", pharmacy.toString())

                Log.d("order", "doInBackground 끝!!")
                return null
            }

            override fun onPostExecute(result: Void?) {
                super.onPostExecute(result)

            }

        }

        var getPharmacy = GetPharmacy()
        getPharmacy.execute()

        marker.map = navermap

        markers.forEach{ markers ->
            markers.map = navermap
        }
    }
}