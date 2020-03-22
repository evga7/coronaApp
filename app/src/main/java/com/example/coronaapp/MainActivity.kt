package com.example.coronaapp

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
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
                // 사용자 위치 얻기 (미구현)
                var userLatLng: LatLng = LatLng(37.565535,127.081892)
                val fragment = FragmentMask(userLatLng)
                addFragment(fragment)
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
        Log.d("order", "addFragment 시작")
        supportFragmentManager.beginTransaction()
            //.setCustomAnimations(R.anim.design_bottom_sheet_slide_in, R.anim.design_bottom_sheet_slide_out)
            .replace(R.id.frameLayout, fragment, fragment.javaClass.simpleName)
            .commit()
        Log.d("order", "addFragment 끝")
    }
}

