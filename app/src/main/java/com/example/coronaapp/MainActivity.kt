package com.example.coronaapp

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.AsyncTask
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.content.PermissionChecker
import androidx.fragment.app.Fragment
import com.example.coronaapp.Mask.FragmentMask
import com.example.coronaapp.Mask.GpsTracker
import com.example.coronaapp.Mask.Pharmacy
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.naver.maps.geometry.LatLng
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import java.net.URL
import java.util.ArrayList

class MainActivity : AppCompatActivity() {

    private var content: FrameLayout? = null

    // 확인할, 확인이 필요한 권한의 목록 생성
    var permission_list = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    private var gpsTracker: GpsTracker? = null
    private lateinit var locationManager: LocationManager

    private val ItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->

        when(item.itemId){
            R.id.korea->{
                val fragment = FragmentKorea.Companion.newInstance()
                addFragment(fragment)
                return@OnNavigationItemSelectedListener true
            }

            R.id.world->{
                val fragment = FragmentWorld()
                addFragment(fragment)
                return@OnNavigationItemSelectedListener true
            }

            R.id.mask->{

                // 사용자 위치 얻기
                gpsTracker = GpsTracker(this@MainActivity)
                Singleton.userLatLng = LatLng(gpsTracker!!.latitude, gpsTracker!!.longitude)

                // 최초 좌표 확인 ==> GPS On/Off 상태를 반환해주는 메서드 필요!!
                if(Singleton.checkKoreaLatLng(Singleton.userLatLng)) {
                    // 사용자 인근 마스크 판매점 얻고 맵에 그림.
                    Log.d("최초좌표확인", "사용자가 GPS를 켰습니다.")
                    Toast.makeText(this, "사용자가 GPS를 켰습니다.", Toast.LENGTH_LONG).show()
                    Singleton.getPharmacyData(Singleton.userLatLng.latitude.toString(), Singleton.userLatLng.longitude.toString(), this)
                }
                else {
                    // 강남역 좌표, GPS 가 켜져 있지 않을 경우
                    Log.d("최초좌표확인", "사용자가 GPS를 껐습니다.")
                    Singleton.userLatLng = LatLng(37.49796323, 127.02779767)
                    Toast.makeText(this, "사용자가 GPS를 껐습니다.", Toast.LENGTH_LONG).show()
                    Singleton.fragmentMask.setLatLng(Singleton.userLatLng)
                    Singleton.fragmentMask.setPharmacyArray(null)
                    addFragment(Singleton.fragmentMask)
                }

                return@OnNavigationItemSelectedListener true
            }

            R.id.help->{
                val fragment = FragmentHelp()
                addFragment(fragment)
                return@OnNavigationItemSelectedListener true
            }

        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 위치가 켜져 있지 않은 경우 위치 설정창으로 넘김. ==> 다이얼로그로 바꿔야할 것 같음
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            intent.addCategory(Intent.CATEGORY_DEFAULT)
            startActivity(intent)
        }

        content = findViewById(R.id.frameLayout)

        val navigation = findViewById<BottomNavigationView>(R.id.navigationView)
        navigation.setOnNavigationItemSelectedListener(ItemSelectedListener)

        val fragment = FragmentKorea.newInstance()
        addFragment(fragment)

        //사용자에게 위치 권한 설정을 물어봄.
        checkPermission()
    }

    private fun addFragment(fragment: Fragment) {

        supportFragmentManager.beginTransaction()
            //.setCustomAnimations(R.anim.design_bottom_sheet_slide_in, R.anim.design_bottom_sheet_slide_out)
            .replace(R.id.frameLayout, fragment, fragment.javaClass.simpleName)
            .commit()
        Log.d("order", "addFragment 끝")
    }

    // 사용자에게 권한을 확인할 함수. onCreate 에서 호출, 마시멜로우 이상부터 확인해야함.
    private fun checkPermission() {
        // 실행한 기기의 안드로이드 버전이 마시멜로우 보다 낮으면 검사를 하지 않음.
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return
        }

        for(permission : String in permission_list) {

            val chk = PermissionChecker.checkCallingOrSelfPermission(this@MainActivity, permission)

            if(chk == PackageManager.PERMISSION_DENIED) {
                requestPermissions(permission_list,0)
                break
            }
        }
    }



}

