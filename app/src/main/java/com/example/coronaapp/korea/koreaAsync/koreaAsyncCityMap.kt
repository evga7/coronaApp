package com.example.coronaapp.korea.koreaAsync

import android.os.AsyncTask
import com.example.coronaapp.korea.FragmentKorea
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements

class koreaAsyncCityMap: AsyncTask<String, String, ArrayList<FragmentKorea.Item>>(){ //input, progress update type, result type
    //private var result : String = ""
    val weburl = "http://ncov.mohw.go.kr"
    val TAG = "Main Activity"
    override fun onPreExecute() {
        super.onPreExecute()
    }

    override fun doInBackground(vararg params: String?): ArrayList<FragmentKorea.Item> {
        val doc: Document = Jsoup.connect("$weburl").get()
        val elts: Elements = doc.select("div#main_maplayout")
        val cityName=elts.select("span.name")
        val cityNum = elts.select("span.num")
        val citybefore = elts.select("span.before")
        val temp : ArrayList<FragmentKorea.Item> = arrayListOf()
        for (i in 0..cityName.size-1)
        {
            var tempItem =
                FragmentKorea.Item(cityName[i].text(), cityNum[i].text(),citybefore[i].text())
            temp.add(tempItem)
        }
        return temp
        //return doc.title()
    }

    override fun onPostExecute(result: ArrayList<FragmentKorea.Item>) {
        //문서제목 출력

        super.onPostExecute(result)
    }
}