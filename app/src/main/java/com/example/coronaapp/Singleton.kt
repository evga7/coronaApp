package com.example.coronaapp

import android.os.AsyncTask
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.coronaapp.Mask.FragmentMask
import com.example.coronaapp.Mask.Pharmacy
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.NaverMap
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import java.net.URL
import java.util.ArrayList

class Singleton {

    companion object {

        lateinit var userLatLng: LatLng
        val pharmacy = ArrayList<Pharmacy>()
        val fragmentMask = FragmentMask()

        // 대한민국의 위도 및 경도를 벗어났을 경우 초기화하는 함수 - Mask
        fun checkKoreaLatLng(userLatLng: LatLng) : Boolean {

            // 대한민국의 위도 및 경도 범위
            if (userLatLng.latitude >= 33.0 && userLatLng.latitude <= 43.0)
                if (userLatLng.longitude >= 124.0 && userLatLng.longitude <= 132.0)
                    return true

            return false
        }

        fun getPharmacyData(latitude:String, longitude:String, Activity: AppCompatActivity) {

            class GetPharmacy: AsyncTask<Void, Void, Void>() {

                // 새로운 스레드가 발생하여 일반 스레드에서 처리가 됨.
                override fun doInBackground(vararg params: Void?): Void? {

                    var temp: String=""
                    try {
                        Log.d("try", " 정보를 가져옵니다!!! ")
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
                    Activity.supportFragmentManager.beginTransaction()
                        //.setCustomAnimations(R.anim.design_bottom_sheet_slide_in, R.anim.design_bottom_sheet_slide_out)
                        .replace(R.id.frameLayout, fragmentMask, fragmentMask.javaClass.simpleName)
                        .commit()
                }
            }

            val getPharmacy = GetPharmacy()
            getPharmacy.execute()
        }

    }

}