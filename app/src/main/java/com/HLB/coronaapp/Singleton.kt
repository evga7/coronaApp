package com.HLB.coronaapp

import android.location.LocationManager
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import com.HLB.coronaapp.Mask.FragmentMask
import com.HLB.coronaapp.Mask.GpsLocation
import com.HLB.coronaapp.Mask.Pharmacy
import com.HLB.coronaapp.korea.FragmentKorea
import com.HLB.coronaapp.world.Information
import com.example.coronaapp.R
import com.naver.maps.geometry.LatLng
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL


class Singleton {

    // 동반자 객체, 코틀린에는 static 이 없음 대신 그 대안으로 object 를 제공함.
    companion object {

        var coronaList: ArrayList<Information>? = null
        var coronaFlag:Boolean = false
        var countrySum:String? = null
        var totalCasesSum:String? = null
        var totalDeathsSum:String? = null
        var totalRecoveredSum:String? = null
        var worldDayInfo:String? = null
        var coList: ArrayList<FragmentKorea.Item>?=null
        var coList2: ArrayList<FragmentKorea.Item>?=null
        var coList3: ArrayList<FragmentKorea.CityItem> ?=null
        var backframent = 0
        lateinit var userLatLng: LatLng
        val pharmacy = ArrayList<Pharmacy>()
        val fragmentMask = FragmentMask()
        lateinit var locationManager: LocationManager
        lateinit var Activity: AppCompatActivity
        var search: Boolean = true

        // 사용자 기기의 위치 정보를 받아올 객체 인스턴스
        private var gpsLocation: GpsLocation? = null

        var nDialog: Boolean = true

        //공공데이터 정보를 얻어옴.
        fun getPharmacyData(latitude:Double, longitude:Double) {

            var lat: Double = latitude
            var lng: Double = longitude

            if(isGpsOn()) {
                // 사용자 위치 얻기
                if(!search) {
                    gpsLocation =
                        GpsLocation(Activity)
                    userLatLng = LatLng(
                        gpsLocation!!.latitude, gpsLocation!!.longitude)
                    lat = userLatLng.latitude
                    lng = userLatLng.longitude
                    // Log.d("isGpsOn&&!search", " GPS 정보를 가져옵니다!!!!!!!!!!! ${lat}, ${lng}")
                }
            }

            class GetPharmacy: AsyncTask<Void, Void, Void>() {

                // 새로운 스레드가 발생하여 일반 스레드에서 처리가 됨.
                override fun doInBackground(vararg params: Void?): Void? {

                    // Log.d("order", "doInBackground 시작!!!!!!!!!!!!!!!!!!!!!!!!!!!!! ${lat}, ${lng}")

                    var temp: String=""
                    try {
                        // Log.d("try", " 정보를 가져옵니다!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! ${lat}, ${lng}")
                        val stream = URL("https://8oi9s0nnth.apigw.ntruss.com/corona19-masks/v1/storesByGeo/json?lat="+lat.toString()+"&lng="+lng.toString()+"&m=1600").openStream()
                        val read = BufferedReader(InputStreamReader(stream, "UTF-8"))
                        var line:String?=read.readLine()
                        while(line!=null){
                            temp+=(line)
                            line = read.readLine()
                        }
                    }
                    catch (e : Exception){
                        // Log.e("error", e.toString())
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
                        // Log.e("Error", e.toString())
                    }

                    val count = json.get("count").toString().toInt()
                    if (count != 0) {

                        val upperArray = json.getJSONArray("stores")

                        for(i in 0..(count - 1)) {
                            val upperObjet = upperArray.getJSONObject(i)
                            // Log.d("CHECK", upperObjet.toString())
                            pharmacy.add(
                                Pharmacy(
                                    upperObjet.getString("addr"),
                                    upperObjet.getString("lat").toDouble(),
                                    upperObjet.getString("lng").toDouble(),
                                    upperObjet.getString("name"),
                                    //upperObjet.getString("remain_stat"), // Caused by: org.json.JSONException: No value for remain_stat 해결방안
                                    upperObjet.optString("remain_stat", "남은 재고 정보 없음"),
                                    //upperObjet.getString("stock_at"),
                                    upperObjet.optString("stock_at", "입고 시간 정보 없음"),
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

                    // Log.e("pharmacy", pharmacy.toString())
                    // Log.d("order", "doInBackground 끝!!!!!!!!!!!!!!!!!!!!!!!!!!!!!  ${lat}, ${lng}")
                    return null
                }

                // doInBackground 작업이 끝나면 실행되는 메서드.
                override fun onPostExecute(result: Void?) {
                    super.onPostExecute(result)
                    fragmentMask.setLatLng(
                        userLatLng
                    )
                    fragmentMask.setPharmacyArray(
                        pharmacy
                    )

                    // Log.d("order", "doInBackground 끝!!!!!!!!!!!!!!!!!!!!!!!!!!!!!  ${lat}, ${lng}")

                    if (!search) {
                        Activity.supportFragmentManager.beginTransaction()
                            //.setCustomAnimations(R.anim.design_bottom_sheet_slide_in, R.anim.design_bottom_sheet_slide_out)
                            .replace(
                                R.id.frameLayout,
                                fragmentMask, fragmentMask.javaClass.simpleName)
                            //.commit() // 네비게이션 누르고 바로 최근 앱 버튼을 눌렀을 때 에러가 발생할 수 있음.
                            .commitAllowingStateLoss() // 위의 에러를 이 줄로써 해결함.
                    }
                    else {
                        fragmentMask.onMapReady(
                            fragmentMask.getNaverMap())
                        search = false
                    }
                }
            }

            val getPharmacy = GetPharmacy()
            getPharmacy.execute()
        }

        // 현재 GPS 가 켜져 있는지 아닌지 확인
        fun isGpsOn() : Boolean {

            if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                return false
            }

            return true
        }

    }
}