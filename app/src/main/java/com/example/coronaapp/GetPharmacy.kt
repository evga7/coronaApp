package com.example.coronaapp

import android.os.AsyncTask
import android.util.Log
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import java.net.URL

class GetPharmacy : AsyncTask<Void, Void, Void>() {

    private var pharmacy = ArrayList<Pharmacy>()

    fun getPharmacyList(): ArrayList<Pharmacy> {

        return pharmacy
    }

    // 새로운 스레드가 발생하여 일반 스레드에서 처리가 됨.
    override fun doInBackground(vararg params: Void?): Void? {

        Log.d("order", "doInBackground 시")


        // 어린이대공원역사거리 37.5479841,127.073755
        // 중곡역 37.565535,127.081892
        // https://8oi9s0nnth.apigw.ntruss.com/corona19-masks/v1/storesByGeo/json?lat=34&lng=125&m=5000
        var temp: String=""
        try {
            //val stream = URL("https://8oi9s0nnth.apigw.ntruss.com/corona19-masks/v1/storesByGeo/json?lat=37.540661&lng127.0714121&m=500").openStream()
            //val stream = URL("https://8oi9s0nnth.apigw.ntruss.com/corona19-masks/v1/storesByGeo/json?lat=37.5479841&lng127.073755&m=1000").openStream()
            //val stream = URL("https://8oi9s0nnth.apigw.ntruss.com/corona19-masks/v1/storesByGeo/json?lat=37.565535&lng127.081892&m=1000").openStream()
            val stream = URL("https://8oi9s0nnth.apigw.ntruss.com/corona19-masks/v1/storesByGeo/json?lat=37.565535&lng=127.081892&m=1000").openStream()
            val read = BufferedReader(InputStreamReader(stream, "UTF-8"))
            //temp = read.readLine()
            var line:String?=read.readLine()
            while(line!=null){
                temp+=(line);
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

    // 사용되지 않음.
    override fun onPostExecute(result: Void?) {
        super.onPostExecute(result)
    }
}