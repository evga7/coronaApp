package com.example.coronaapp

import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.coronaapp.help.FragmentHelp
import com.example.coronaapp.help.fragment.devfrag
import com.example.coronaapp.korea.FragmentKorea
import com.example.coronaapp.korea.koreaAsync.koreaAsyncMainData
import com.example.coronaapp.world.FragmentWorld
import com.example.coronaapp.world.WorldCrawling
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_korea.*
import nl.joery.animatedbottombar.AnimatedBottomBar
import java.io.IOException


class MainActivity : AppCompatActivity() {

    private var content: FrameLayout? = null
    private var lastClickedTime: Long = 0L

    var currentfragment=Fragment()
    var mBackWait:Long = 0

    override fun onBackPressed() {
        if (Singleton.backframent==0) {
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
                    currentfragment = FragmentKorea()
                    Singleton.backframent=0
                    addFragment(currentfragment)
                    // e.g. show a dialog

                }
                if (newTab.id == R.id.world)
                {
                    currentfragment = FragmentWorld()
                    Singleton.backframent=0
                    if(Singleton.coronaList == null){
                        try {
                            Log.d("크롤링","onCreate")
                            WorldCrawling(this@MainActivity,this@MainActivity,currentfragment).execute("https://www.worldometers.info/coronavirus/")
                        }catch (e : IOException) {
                            e.printStackTrace()
                        }
                    }
                    else{
                        addFragment(currentfragment)
                    }

                }
                if (newTab.id == R.id.mask)
                {
                    Singleton.backframent=0
                    currentfragment = FragmentMask()
                    addFragment(currentfragment)
                }
                if (newTab.id == R.id.help)
                {
                    Singleton.backframent=0
                    currentfragment = FragmentHelp()
                    addFragment(currentfragment)
                }
                return true
            }

        })


        val fragment = FragmentKorea.Companion.newInstance()

        if (Singleton.coList == null){
            currentfragment=FragmentKorea()
            koreaAsyncMainData(this,this,fragment).execute("http://ncov.mohw.go.kr")
        }

        //addFragment(fragment)

    }

    private fun addFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            //.setCustomAnimations(R.anim.design_bottom_sheet_slide_in, R.anim.design_bottom_sheet_slide_out)
            .replace(R.id.frameLayout, fragment, fragment.javaClass.simpleName).commit()


    }

}






