package com.HLB.coronaapp

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.PermissionChecker
import androidx.fragment.app.Fragment
import com.HLB.coronaapp.help.FragmentHelp
import com.HLB.coronaapp.korea.FragmentKorea
import com.HLB.coronaapp.korea.koreaAsync.koreaAsyncMainData
import com.HLB.coronaapp.singleton.Singleton
import com.HLB.coronaapp.world.FragmentWorld
import com.HLB.coronaapp.world.worldAsync.WorldCrawling
import com.HLB.coronaapp.R
import com.naver.maps.geometry.LatLng
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.update_dialog.view.*
import nl.joery.animatedbottombar.AnimatedBottomBar
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private var content: FrameLayout? = null

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    val connectState = NetworkConnectionState(this@MainActivity)

    var currentId = 0
    var currentfragment=Fragment()
    var mBackWait:Long = 0
    private var permission_list = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    override fun onBackPressed() {
        if (Singleton.backframent ==0) {
            if (System.currentTimeMillis() - mBackWait >= 2000) {
                mBackWait = System.currentTimeMillis()
                Toast.makeText(this, "뒤로가기 버튼을 한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
            } else {
                System.exit(0)
            }
        }
        else
        {
            super.onBackPressed()
        }
    }




    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val cm: ConnectivityManager = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? = cm.activeNetworkInfo ?: null
        if (networkInfo==null)
        {
            val dialogView =this.layoutInflater.inflate(R.layout.update_dialog, null)
            val builder = android.app.AlertDialog.Builder(this).setView(dialogView)
            builder.show()
            dialogView.infoDialogText.setText("인터넷이 접속되어있어야 가능합니다\n인터넷에 접속해주세요.")
            dialogView.updateOkButton.setOnClickListener {
                System.exit(0)
            }
        }
        else {
            if (Singleton.coList == null) {
                val fragment = FragmentKorea.Companion.newInstance()
                currentfragment = FragmentKorea()
                koreaAsyncMainData(
                    this,
                    this,
                    fragment
                ).execute("http://ncov.mohw.go.kr")
            }
            Log.d("onSaveInstanceState", "${savedInstanceState} !!!!!!!!!!!!!!!!!!!!!!!!!!!")

            // locationManager 를 이용하려면 메인액티비티에서 getSystemService 를 받아와야 함.
            Singleton.locationManager =
                getSystemService(Context.LOCATION_SERVICE) as LocationManager
            Singleton.Activity = this

            // 위치가 켜져 있지 않은 경우 위치 설정창으로 넘김 ==> 마스크 쪽을 옮길 수도 있음.
            if (!Singleton.isGpsOn()) {
                showLocationDialog()
            }

            content = findViewById(R.id.frameLayout)

            navigationView.setOnTabInterceptListener(object :
                AnimatedBottomBar.OnTabInterceptListener {
                override fun onTabIntercepted(
                    lastIndex: Int,
                    lastTab: AnimatedBottomBar.Tab?,
                    newIndex: Int,
                    newTab: AnimatedBottomBar.Tab
                ): Boolean {
                    if (newTab.id == R.id.korea) {
                        currentfragment = FragmentKorea.Companion.newInstance()
                        Singleton.backframent = 0
                        addFragment(currentfragment)
                        currentId=0
                    }
                    if (newTab.id == R.id.world) {
                        currentId=1
                        currentfragment = FragmentWorld()
                        Singleton.backframent = 0
                        if (Singleton.coronaList == null) {
                            try {
                                WorldCrawling(
                                    this@MainActivity,
                                    this@MainActivity,
                                    currentfragment
                                ).execute("https://www.worldometers.info/coronavirus/")
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                        } else {
                            addFragment(currentfragment)
                        }

                    }
                    if (newTab.id == R.id.mask) {

                        connectState.register()
                        connectState.unregister()
                        Singleton.backframent = 0
                        if (Singleton.isGpsOn()) {
                            currentId=2// GPS 가 켜져있는 경우
                            // 사용자 인근 마스크 판매점 얻고 맵에 그림
                            Singleton.search = false
                            Singleton.getPharmacyData(
                                0.0,
                                0.0, this@MainActivity
                            )
                        } else { // GPS 가 켜져 있지 않은 경우
                            showLocationDialog()
                        }

                    }
                    if (newTab.id == R.id.help) {

                        Singleton.backframent = 0
                        currentfragment = FragmentHelp()
                        addFragment(currentfragment)
                        currentId=3
                    }
                    return true
                }

            })


            //addFragment(fragment)
            //사용자에게 위치 권한 설정을 물어봄.
            checkPermission()
        }

    }

    private fun addFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            //.setCustomAnimations(R.anim.design_bottom_sheet_slide_in, R.anim.design_bottom_sheet_slide_out)
            .replace(R.id.frameLayout, fragment, fragment.javaClass.simpleName).commit()
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

    // GPS 가 켜져 있지 않을 경우에 설정을 물어볼 다이얼로그
    private fun showLocationDialog() {

        val inflater = this@MainActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.main_notice_dialog, null)

        val locationDialog = AlertDialog.Builder(this@MainActivity)
                locationDialog.setMessage("\n\n마스크 재고 현황을 확인하기 위해서는\n" +
                    "\"위치 정보\"를 사용으로 설정해주셔야 합니다." +
                    "\n\n\n \"위치 정보\"를 설정해주시겠습니까? ")
            .setPositiveButton("예") { dialog, which ->
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                intent.addCategory(Intent.CATEGORY_DEFAULT)
                startActivity(intent)
            }
            .setNeutralButton("아니요") { dialog, which ->
            }
            .create()

        // 여백 눌러도 창 안없어지게
        locationDialog.setCancelable(false)
        locationDialog.setView(view)
        locationDialog.show()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d("onSaveInstanceState","${outState} !!!!!!!!!!!!!!!!!!!!!!!!!!!")
    }



}

