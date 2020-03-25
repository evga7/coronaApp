package com.example.coronaapp.korea.koreaAsync

import android.os.AsyncTask
import com.example.coronaapp.korea.FragmentKorea
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements

class koreaAsyncMainData: AsyncTask<String, String, ArrayList<FragmentKorea.Item>>(){ //input, progress update type, result type
    //private var result : String = ""
    val weburl = "http://ncov.mohw.go.kr"
    val TAG = "Main Activity"
    override fun onPreExecute() {
        super.onPreExecute()
    }

    override fun doInBackground(vararg params: String?): ArrayList<FragmentKorea.Item> {
        val doc: Document = Jsoup.connect("$weburl").get()
        val elts: Elements = doc.select("div.live_left")
        val livenum =doc.select("div.liveNumOuter")
        val chartD= elts.select("div.chart_d")
        val sumInfoTit=elts.select("ul.suminfo").select("span.tit")
        val sumInfoNum=elts.select("ul.suminfo").select("span.num")
        val mainInfoText=livenum.select("span.livedate").text()
        val infectedNum=livenum.select("div.livenum").select("li")
        //Log.d("testtttttttt",infectedNum.text())
        /*
        elts.forEachIndexed{ index, elem ->
            //val liveDate = elem.select("span.livedate")
            //val infectedNum=livenum.select("span.num")
            Log.d("테스트!!!!!!!!!!!!!!",infectedNum.toString())
            //Log.d("테스트!!!!!!!!!!!!!!!",liveDate.toString())//추출한 자료를 가지고 데이터 객체를 만들어 ArrayList에 추가해 준다.
            //var mNews = Item(title, a_href, "http:" + thumb_img)
            //newsList.add(mNews)
        }
         */
        val temp : ArrayList<FragmentKorea.Item> = arrayListOf()
        infectedNum.forEachIndexed{index,elem->
            val title = infectedNum[index].select("strong.tit").text()
            val num = infectedNum[index].select("span.num").text()
            val before = infectedNum[index].select("span.before").text()
            var mNews =
                FragmentKorea.Item(title, num, before)
            temp.add(mNews)
        }
        temp.add(
            FragmentKorea.Item(
                mainInfoText.toString(),
                "",
                ""
            )
        )
        temp.add(
            FragmentKorea.Item(
                chartD.select("p.numinfo1").select("span.num_tit").text(),
                chartD.select("p.numinfo1").select("span.num_rnum").text().substringBefore("명"),
                chartD.select("p.numinfo1").select("span.num_rnum").text().substringAfter("명")
            )
        )
        temp.add(
            FragmentKorea.Item(
                chartD.select("p.numinfo2").select("span.num_tit").text(),
                chartD.select("p.numinfo2").select("span.num_rnum").text().substringBefore("명"),
                chartD.select("p.numinfo2").select("span.num_rnum").text().substringAfter("명")
            )
        )
        temp.add(
            FragmentKorea.Item(
                chartD.select("p.numinfo3").select("span.num_tit").text(),
                chartD.select("p.numinfo3").select("span.num_rnum").text().substringBefore("명"),
                chartD.select("p.numinfo3").select("span.num_rnum").text().substringAfter("명")
            )
        )
        for(i in 0..sumInfoNum.size-1)
        {
            temp.add(FragmentKorea.Item(sumInfoTit[i].text(),sumInfoNum[i].text(),""))
        }
        return temp
        //return doc.title()
    }

    override fun onPostExecute(result: ArrayList<FragmentKorea.Item>) {
        //문서제목 출력

        super.onPostExecute(result)
    }
    data class Item(val title: String, val num:String, val before: String)
}