package com.example.coronaapp

import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView


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
                addFragment(fragment)
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

        Singleton()

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
    }


}






