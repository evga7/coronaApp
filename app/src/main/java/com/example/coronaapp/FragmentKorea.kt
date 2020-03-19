package com.example.coronaapp

import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_korea.view.*
import org.eazegraph.lib.charts.BarChart
import org.eazegraph.lib.models.BarModel
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements

class FragmentKorea : Fragment() {

    val weburl = "http://ncov.mohw.go.kr"
    val TAG = "Main Activity"
    var coList: ArrayList<Item> = arrayListOf()
    val infoMainText = String()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    class MyAsyncTask: AsyncTask<String, String, ArrayList<Item>>(){ //input, progress update type, result type
        //private var result : String = ""
        val weburl = "http://ncov.mohw.go.kr"
        val TAG = "Main Activity"
        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun doInBackground(vararg params: String?): ArrayList<Item> {
            val doc: Document = Jsoup.connect("$weburl").get()
            val elts: Elements = doc.select("div.live_left")
            val livenum = elts.select("ul.liveNum")
            val chartD= elts.select("div.chart_d")
            val mainInfoText =elts.select("span.livedate").text()
            val infectedNum=livenum.select("li")
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
            val temp : ArrayList<Item> = arrayListOf()
            infectedNum.forEachIndexed{index,elem->
                val title = infectedNum[index].select("strong.tit").text()
                val num = infectedNum[index].select("span.num").text()
                val before = infectedNum[index].select("span.before").text()
                var mNews = Item(title, num,before)
                temp.add(mNews)
            }
            temp.add(Item(mainInfoText.toString(),"",""))
            temp.add(Item(chartD.select("p.numinfo1").select("span.num_tit").text(),chartD.select("p.numinfo1").select("span.num_rnum").text().substringBefore("명"),chartD.select("p.numinfo1").select("span.num_rnum").text().substringAfter("명")))
            temp.add(Item(chartD.select("p.numinfo2").select("span.num_tit").text(),chartD.select("p.numinfo2").select("span.num_rnum").text().substringBefore("명"),chartD.select("p.numinfo2").select("span.num_rnum").text().substringAfter("명")))
            temp.add(Item(chartD.select("p.numinfo3").select("span.num_tit").text(),chartD.select("p.numinfo3").select("span.num_rnum").text().substringBefore("명"),chartD.select("p.numinfo3").select("span.num_rnum").text().substringAfter("명")))
            return temp
            //return doc.title()
        }

        override fun onPostExecute(result: ArrayList<Item>) {
            //문서제목 출력

            super.onPostExecute(result)
        }
    }


    companion object {
        fun newInstance(): FragmentKorea {
            val FragmentKorea = FragmentKorea()
            FragmentKorea.coList=MyAsyncTask().execute("http://ncov.mohw.go.kr").get()
            Log.d("fragLog",FragmentKorea.coList.toString())
            val args = Bundle()
            FragmentKorea.arguments = args
            return FragmentKorea
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var koreaFragView= inflater.inflate(R.layout.fragment_korea, container, false)
        coList
        koreaFragView.infoText.setText(coList[4].title)
        koreaFragView.infectedText1.setText(coList[0].title)
        koreaFragView.infectedText2.setText(coList[0].num.substring(4))
        koreaFragView.infectedText3.setText(coList[0].before.substring(5))
        koreaFragView.cureText1.setText(coList[1].title)
        koreaFragView.cureText2.setText(coList[1].num)
        koreaFragView.cureText3.setText(coList[1].before)
        koreaFragView.careText1.setText(coList[2].title)
        koreaFragView.careText2.setText(coList[2].num)
        koreaFragView.careText3.setText(coList[2].before)
        koreaFragView.deadText1.setText(coList[3].title)
        koreaFragView.deadText2.setText(coList[3].num)
        koreaFragView.deadText3.setText(coList[3].before)
        val barChar : BarChart
        barChar=koreaFragView.coronaChart
        barChar.clearChart()
        for (i in 5..7)
        barChar.addBar(BarModel(coList[i].title+" "+coList[i].before,coList[i].num.replace(",","").toFloat(), 0xFF56B7F1.toInt()))
        barChar.startAnimation()

        return koreaFragView
    }
    data class Item(val title: String, val num:String, val before: String)
}

// 프레그먼트는 메모리 확보를 위해 파기 될 수 있는데 이때 보통
// onSaveInstanceState()를 통해서 데이터를 보관하게 됨
// 하지만 newInstance 를 통하여 세팅되면 재생성 시 번들을 통하여
// 다시 세팅되어 넘어오게 되는데 그렇게 되게 하기 위하여
// 우선 작업?처리?를 아래와 같이 해놓은 것임.