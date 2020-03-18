package com.example.coronaapp

import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_korea.*
import kotlinx.android.synthetic.main.fragment_korea.view.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements

class FragmentKorea : Fragment() {

    val weburl = "http://ncov.mohw.go.kr"
    val TAG = "Main Activity"
    var coList: ArrayList<Item> = arrayListOf()

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
            val elts: Elements = doc.select("div.container")
            val livenum = elts.select("ul.liveNum")
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
            val FragmentKorea=FragmentKorea()
            val temp : ArrayList<Item> = arrayListOf()
            val title = infectedNum.select("strong.tit").text()
            val num = infectedNum.select("span.num").text()
            val before = infectedNum.select("span.before").text()
            var mNews = Item(title, num,before)
            temp.add(mNews)
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
        var a= inflater.inflate(R.layout.fragment_korea, container, false)
        val FragmentKorea = FragmentKorea()
        Log.d("Ddddddddddddddddddddd",FragmentKorea.coList.toString())

        //a.koreatext.setText(FragmentKorea.coList.toString())
       // a.koreatext.setText("dd")
        return a
    }
    data class Item(val title: String, val num:String, val before: String)
}

// 프레그먼트는 메모리 확보를 위해 파기 될 수 있는데 이때 보통
// onSaveInstanceState()를 통해서 데이터를 보관하게 됨
// 하지만 newInstance 를 통하여 세팅되면 재생성 시 번들을 통하여
// 다시 세팅되어 넘어오게 되는데 그렇게 되게 하기 위하여
// 우선 작업?처리?를 아래와 같이 해놓은 것임.