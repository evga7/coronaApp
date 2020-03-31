package com.example.coronaapp

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.coronaapp.korea.FragmentKorea
import com.example.coronaapp.korea.koreaAsync.koreaAsyncMainData
import com.example.coronaapp.world.CustomProgressCircle
import com.example.coronaapp.world.FragmentWorld
import com.example.coronaapp.world.WorldCrawling
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.IOException


class MainActivity : AppCompatActivity() {

    private var content: FrameLayout? = null
    private var lastClickedTime: Long = 0L

    private val ItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->

//        // duplicate click prevent
//        if (SystemClock.elapsedRealtime() - lastClickedTime < 1000){
//            return@OnNavigationItemSelectedListener false
//        }
//
//        lastClickedTime = SystemClock.elapsedRealtime()

        when(item.itemId){
            R.id.korea->{
                val fragment = FragmentKorea.Companion.newInstance()
                addFragment(fragment)
                return@OnNavigationItemSelectedListener true
            }

            R.id.world->{

                val fragment = FragmentWorld()

                if(Singleton.coronaList == null){
                    try {
                        Log.d("크롤링","onCreate")
                        WorldCrawling(this,this,fragment).execute("https://www.worldometers.info/coronavirus/")
                    }catch (e : IOException) {
                        e.printStackTrace()
                    }
                }
                else{
                    addFragment(fragment)
                }

                //addFragment(fragment)
                Log.d("worldclick","worldclcc")
                return@OnNavigationItemSelectedListener true
            }

            R.id.mask->{
                val fragment = FragmentMask()
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

        //Singleton()

        val navigation = findViewById<BottomNavigationView>(R.id.navigationView)
        navigation.setOnNavigationItemSelectedListener(ItemSelectedListener)

        val fragment = FragmentKorea.Companion.newInstance()

        if (Singleton.coList==null){
            koreaAsyncMainData(this,this,fragment).execute("http://ncov.mohw.go.kr")
        }

        //addFragment(fragment)

    }

    private fun addFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            //.setCustomAnimations(R.anim.design_bottom_sheet_slide_in, R.anim.design_bottom_sheet_slide_out)
            .replace(R.id.frameLayout, fragment, fragment.javaClass.simpleName)
        .commit()
    }
}






