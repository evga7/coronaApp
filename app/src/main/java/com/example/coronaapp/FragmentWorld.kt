package com.example.coronaapp

import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_world.view.*
import org.jsoup.Jsoup
import java.io.IOException

class FragmentWorld : Fragment() {
    val url = "https://www.worldometers.info/coronavirus/"
    //var coronaList = ArrayList<Information>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(Singleton.coronaList == null){
            try {
                Log.d("크롤링","onCreate")
                Singleton.coronaList = WorldCrawling().execute(url).get()
            }catch (e : IOException) {
                e.printStackTrace()
            }
        }

    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val rootView = inflater.inflate(R.layout.fragment_world, container, false)

        // tablelayout total data add
        if (Singleton.coronaFlag == false){
            Singleton.coronaFlag = true
            Singleton.countrySum = Singleton.coronaList?.get(Singleton.coronaList?.size!! - 1)!!.country
            Singleton.totalCasesSum = Singleton.coronaList?.get(Singleton.coronaList?.size!! - 1)!!.totalCases
            Singleton.totalDeathsSum = Singleton.coronaList?.get(Singleton.coronaList?.size!! - 1)!!.totalDeaths
            Singleton.totalRecoveredSum = Singleton.coronaList?.get(Singleton.coronaList?.size!! - 1)!!.totalRecovered
            Singleton.coronaList?.remove(Singleton.coronaList?.get(Singleton.coronaList?.size!!-1)!!)
        }
        rootView.a.text = Singleton.countrySum
        rootView.b.text = Singleton.totalCasesSum
        rootView.c.text = Singleton.totalDeathsSum
        rootView.d.text = Singleton.totalRecoveredSum


        // RecyclerView
        val worldRecyclerView = rootView.findViewById(R.id.worldrecyclerview) as RecyclerView
        worldRecyclerView.layoutManager = LinearLayoutManager(activity)
        worldRecyclerView.adapter = WorldAdapter(Singleton.coronaList!!)
        worldRecyclerView.addItemDecoration(
            DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
        )


        return rootView
    }

}

class WorldCrawling : AsyncTask<String, String, ArrayList<Information>>() { // 세번째 doinbackground 반환타입, onPostExecute 매개변수
    var infoList: ArrayList<Information> = arrayListOf()

    override fun onPreExecute() {
        super.onPreExecute()
    }

    override fun doInBackground(vararg params: String?): ArrayList<Information> {

        try{
            val doc = Jsoup.connect(params[0]).get()
            //val data = doc.select("#main_table_countries > tbody > tr")
            val data = doc.select("#main_table_countries_yesterday > tbody > tr")

            var country :String
            var totalCases : String
            var newCases : String
            var totalDeaths :String
            var newDeaths : String
            var totalRecovered : String


            // country cnt
            var countCnt :Int = 1

            //infoList.add(Information("국가","확진자","사망자","회복"))

            for (datum in data){
                country = datum.select("td")[0].text().trim()
                totalCases = datum.select("td")[1].text().trim()
                newCases = datum.select("td")[2].text().trim()
                totalDeaths = datum.select("td")[3].text().trim()
                newDeaths = datum.select("td")[4].text().trim()
                totalRecovered = datum.select("td")[5].text().trim()

                val total = Information(country,totalCases + '\n' + newCases,totalDeaths + '\n' + newDeaths,totalRecovered)
                infoList.add(total)

                countCnt++
            }

            // total data add
            val totalCasesSum:String
            val totalDeathsSum:String
            val totalRecoveredSum:String

            val caseArr = infoList[infoList.size - 1].totalCases.split("\n")
            val case1 = caseArr[0].split(',')
            val case2 = caseArr[1].split(',')
            totalCasesSum = ((case1[0] + case1[1]).toInt() + (case2[0] + case2[1]).toInt()).toString()

            val deathArr = infoList[infoList.size - 1].totalDeaths.split("\n")
            val death1 = deathArr[0].split(',')
            val death2 = deathArr[1].split(',')
            totalDeathsSum = ((death1[0] + death1[1]).toInt() + (death2[0] + death2[1]).toInt()).toString()

            totalRecoveredSum = infoList[infoList.size - 1].totalRecovered

            infoList.add(Information(countCnt.toString(),totalCasesSum,totalDeathsSum,totalRecoveredSum))

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

