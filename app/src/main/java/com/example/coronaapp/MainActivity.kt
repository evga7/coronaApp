package com.example.coronaapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
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
    private lateinit var userLatLng: LatLng
    private var gpsTracker: GpsTracker? = null
    val pharmacy = ArrayList<Pharmacy>()
    val fragmentMask = FragmentMask()

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

                Log.d("order", "addFragment 시작")

                //사용자에게 위치 권한 설정을 물어봄.
                checkPermission()

                // 사용자 위치 얻기
                gpsTracker = GpsTracker(this@MainActivity)
                userLatLng = LatLng(gpsTracker!!.latitude, gpsTracker!!.longitude)

                // 최초 좌표 확인.
                checkKoreaLatLng()

                // 사용자 인근 마스크 판매점 얻고 맵에 그림.
                getPharmacyData(userLatLng.latitude.toString(), userLatLng.longitude.toString())

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

        content = findViewById(R.id.frameLayout)

        val navigation = findViewById<BottomNavigationView>(R.id.navigationView)
        navigation.setOnNavigationItemSelectedListener(ItemSelectedListener)

        val fragment = FragmentKorea.Companion.newInstance()
        addFragment(fragment)
    }

    private fun addFragment(fragment: Fragment) {

        supportFragmentManager.beginTransaction()
            //.setCustomAnimations(R.anim.design_bottom_sheet_slide_in, R.anim.design_bottom_sheet_slide_out)
            .replace(R.id.frameLayout, fragment, fragment.javaClass.simpleName)
            .commit()
        Log.d("order", "addFragment 끝")
    }

    private fun getPharmacyData(latitude:String, longitude:String) {

        class GetPharmacy: AsyncTask<Void, Void, Void>() {

            // 새로운 스레드가 발생하여 일반 스레드에서 처리가 됨.
            override fun doInBackground(vararg params: Void?): Void? {

                var temp: String=""
                try {
                    val stream = URL("https://8oi9s0nnth.apigw.ntruss.com/corona19-masks/v1/storesByGeo/json?lat="+latitude+"&lng="+longitude+"&m=1500").openStream()
                    val read = BufferedReader(InputStreamReader(stream, "UTF-8"))
                    var line:String?=read.readLine()
                    while(line!=null){
                        temp+=(line)
                        line = read.readLine()
                    }
                }
                catch (e : Exception){
                    Log.e("error", e.toString())
                }

                val json = JSONObject(temp)
                try{
                    var str = json.get("message").toString()
                    pharmacy.add(
                        Pharmacy(
                            "none",
                            0.0,
                            0.0,
                            "none",
                            "none",
                            "none",
                            "none"
                        )
                    )
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
                        pharmacy.add(
                            Pharmacy(
                                upperObjet.getString("addr"),
                                upperObjet.getString("lat").toDouble(),
                                upperObjet.getString("lng").toDouble(),
                                upperObjet.getString("name"),
                                upperObjet.getString("remain_stat"),
                                upperObjet.getString("stock_at"),
                                upperObjet.getString("type")
                            )
                        )
                    }

                } else {
                    pharmacy.add(
                        Pharmacy(
                            "none",
                            0.0,
                            0.0,
                            "none",
                            "none",
                            "none",
                            "none"
                        )
                    )
                }

                Log.e("pharmacy", pharmacy.toString())
                Log.d("order", "doInBackground 끝!!")
                return null
            }

            // doInBackground 작업이 끝나면 실행되는 메서드.
            override fun onPostExecute(result: Void?) {
                super.onPostExecute(result)
                fragmentMask.setLatLng(userLatLng)
                fragmentMask.setPharmacyArray(pharmacy)
                addFragment(fragmentMask)
            }
        }

        val getPharmacy = GetPharmacy()
        getPharmacy.execute()
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

    // 대한민국의 위도 및 경도를 벗어났을 경우 초기화
    private fun checkKoreaLatLng() {

        // 대한민국의 위도 및 경도 범위
        if (userLatLng.latitude >= 33.0 && userLatLng.latitude <= 43.0)
            if (userLatLng.longitude >= 124.0 && userLatLng.longitude <= 132.0)
                return

        userLatLng = LatLng(37.497848, 127.0267397)
    }
}

