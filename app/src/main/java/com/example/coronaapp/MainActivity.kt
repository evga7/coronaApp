package com.example.coronaapp

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

    private lateinit var userLatLng: LatLng
    // private var gpsTracker: GpsTracker? = null
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

                // 사용자 위치 얻기 (미구현)
                // userLatLng = LatLng(37.565535,127.081892)
                // userLatLng = LatLng(37.540661, 127.0714121)
                // userLatLng = LatLng(37.5661525,127.0832786)
                userLatLng = LatLng(37.5479841,127.073755)

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
        Log.d("order", "addFragment 시작")
        supportFragmentManager.beginTransaction()
            //.setCustomAnimations(R.anim.design_bottom_sheet_slide_in, R.anim.design_bottom_sheet_slide_out)
            .replace(R.id.frameLayout, fragment, fragment.javaClass.simpleName)
            .commit()
        Log.d("order", "addFragment 끝")
    }

    fun getPharmacyData(latitude:String, longitude:String) {

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
}

