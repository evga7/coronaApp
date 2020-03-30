package com.example.coronaapp.korea.koreaAsync

import android.os.AsyncTask
import com.example.coronaapp.korea.FragmentKorea
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements

class koreaAsyncCityData: AsyncTask<String, String, ArrayList<FragmentKorea.CityItem>>(){ //input, progress update type, result type
    //private var result : String = ""
    val weburl = "http://ncov.mohw.go.kr"
    val TAG = "Main Activity"
    override fun onPreExecute() {
        super.onPreExecute()
    }

    override fun doInBackground(vararg params: String?): ArrayList<FragmentKorea.CityItem> {
        val doc: Document = Jsoup.connect("$weburl").get()
        val temp : ArrayList<FragmentKorea.CityItem> = arrayListOf()
        for (i in 1..18)
        {
            val elts: Elements = doc.select("div#map_city"+i)
            val cityInfo = elts.select("ul.cityinfo")
            //val tit = cityInfo.select("span.tit")
            val num = cityInfo.select("span.num")
            //val before_tit = cityInfo.select("span.sub_tit.red")
            val before_num = cityInfo.select("span.sub_num.red")
            val percentage = elts.select("p.citytit")
            temp.add(FragmentKorea.CityItem(elts.select("h4.cityname").text(),num[0].text(),before_num.text(),num[1].text(),num[2].text(),num[3].text(),percentage.text()))
        }




        return temp
        //return doc.title()
    }

    override fun onPostExecute(result: ArrayList<FragmentKorea.CityItem>) {
        //문서제목 출력
        super.onPostExecute(result)
    }
}