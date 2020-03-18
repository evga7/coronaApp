package com.example.coronaapp

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.UiThread
import androidx.fragment.app.Fragment
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
//// OnMapReadyCallback 을 등록하면 비동기로 NaverMap 객체를 얻을 수 있음.
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import kotlinx.android.synthetic.main.fragment_mask.*

class FragmentMask : Fragment(), OnMapReadyCallback {

    private lateinit var mapView: MapView

    // Layout 을 inflate 하는 곳, View 객체를 얻어서 초기화
    // XML 로 만들어 놓은 View 를 inflater 를 활용하여 생성할 수 있음.
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_mask, container, false)
    }

    // fragment 생성 이후 호출되는 함수.
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mapView = map_view
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
    }

    // NaverMap 객체가 준비되면 onMapReady 콜백 메서드가 호출됨.
    @UiThread
    override fun onMapReady(naverMap: NaverMap) {
        val pharmacy = LatLng(37.540661, 127.0714121)
        val cameraUpdate = CameraUpdate.scrollTo(pharmacy)

        val infoWindow = InfoWindow()
        infoWindow.adapter = object : InfoWindow.DefaultTextAdapter (requireContext()) {
            override fun getText(infoWindow: InfoWindow): CharSequence {
                return "햇빛 약국"
            }
        }

        val marker = Marker()

        infoWindow.open(marker)

        marker.position = pharmacy
        marker.map = naverMap

        naverMap.moveCamera(cameraUpdate)
    }

    // Fragment 화면에 표시될 때 호출
   override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    // Fragment 화면에 로딩이 끝났을 때 호
    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    // 화면이 중지되면 호출되는 함수.
    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//        mapView.onSaveInstanceState(outState)
//    }

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
    }

//    override fun onLowMemory() {
//        super.onLowMemory()
//        mapView.onLowMemory()
//    }

}