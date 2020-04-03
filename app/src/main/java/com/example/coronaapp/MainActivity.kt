package com.example.coronaapp

import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.coronaapp.help.FragmentHelp
import com.example.coronaapp.korea.FragmentKorea
import com.example.coronaapp.korea.koreaAsync.koreaAsyncMainData
import com.example.coronaapp.world.FragmentWorld
import com.example.coronaapp.world.WorldCrawling
import kotlinx.android.synthetic.main.activity_main.*
import nl.joery.animatedbottombar.AnimatedBottomBar
import java.io.IOException


class MainActivity : AppCompatActivity() {

    private var content: FrameLayout? = null
    private var lastClickedTime: Long = 0L





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        content = findViewById(R.id.frameLayout)

        //Singleton()

        navigationView.setOnTabInterceptListener(object : AnimatedBottomBar.OnTabInterceptListener {
            override fun onTabIntercepted(
                lastIndex: Int,
                lastTab: AnimatedBottomBar.Tab?,
                newIndex: Int,
                newTab: AnimatedBottomBar.Tab
            ): Boolean {
                if (newTab.id == R.id.korea) {
                    val fragment = FragmentKorea()
                    addFragment(fragment)
                    // e.g. show a dialog

                }
                if (newTab.id == R.id.world)
                {
                    val fragment = FragmentWorld()

                    if(Singleton.coronaList == null){
                        try {
                            Log.d("크롤링","onCreate")
                            WorldCrawling(this@MainActivity,this@MainActivity,fragment).execute("https://www.worldometers.info/coronavirus/")
                        }catch (e : IOException) {
                            e.printStackTrace()
                        }
                    }
                    else{
                        addFragment(fragment)
                    }

                }
                if (newTab.id == R.id.mask)
                {
                    val fragment = FragmentMask()
                    addFragment(fragment)
                }
                if (newTab.id == R.id.help)
                {
                    val fragment = FragmentHelp()
                    addFragment(fragment)
                }
                return true
            }

        })


        val fragment = FragmentKorea.Companion.newInstance()

        if (Singleton.coList == null){
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






