package com.example.coronaapp

import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_world.view.*
import org.jsoup.Jsoup
import java.io.IOException

class FragmentWorld : Fragment() {
    val url = "https://www.worldometers.info/coronavirus/"
    var coronaList = ArrayList<Information>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            coronaList = WorldCrawling().execute(url).get()
        }catch (e : IOException) {
            e.printStackTrace();
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        var a = inflater.inflate(R.layout.fragment_world, container, false)
        //a.worldtext.text = coronaList.toString()

        return a
        //return inflater.inflate(R.layout.fragment_world, container, false)

    }

}

class WorldCrawling : AsyncTask<String, String, ArrayList<Information>>() { // 세번째 doinbackground 반환타입, onPostExecute 매개변수
    var infoList: ArrayList<Information> = arrayListOf()

    override fun onPreExecute() {
        super.onPreExecute()
    }

    override fun doInBackground(vararg params: String?): ArrayList<Information> {

        try{
            //val doc = Jsoup.connect("https://www.worldometers.info/coronavirus/").get()
            val doc = Jsoup.connect(params[0]).get()
            val data = doc.select("#main_table_countries > tbody > tr")

            var country :String
            var totalCases : String
            var newCases : String
            var totalDeaths : String
            var newDeaths : String
            var totalRecovered : String

            for (datum in data){
                country = datum.select("td")[0].text().trim()
                totalCases = datum.select("td")[1].text().trim()
                newCases = datum.select("td")[2].text().trim()
                totalDeaths = datum.select("td")[3].text().trim()
                newDeaths = datum.select("td")[4].text().trim()
                totalRecovered = datum.select("td")[5].text().trim()

                var total = Information(country,totalCases,newCases,newDeaths,totalDeaths,totalRecovered)
                infoList.add(total)
                //Log.d(totalCases,"모")
            }

        }catch (e : IOException) {
            Log.d("안됨","안딤")
            e.printStackTrace()
        }
        return infoList
    }

    override fun onProgressUpdate(vararg values: String?) { // UI 업데이
        super.onProgressUpdate(*values)
    }

    override fun onPostExecute(result: ArrayList<Information>) {
        super.onPostExecute(result)
    }

}

data class Information(val country:String, val totalCases:String, val newCases : String, val totalDeaths : String, val newDeaths : String, val totalRecovered : String)


