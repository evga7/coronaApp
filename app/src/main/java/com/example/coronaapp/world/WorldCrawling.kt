package com.example.coronaapp.world

import android.content.Context
import android.os.AsyncTask
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.coronaapp.R
import com.example.coronaapp.Singleton
import org.jsoup.Jsoup
import java.io.IOException
import kotlin.collections.ArrayList

class WorldCrawling(act:AppCompatActivity, context: Context,frg:Fragment) : AsyncTask<String, String, ArrayList<Information>>() {
    var infoList: ArrayList<Information> = arrayListOf()
    val progressCircle = CustomProgressCircle()
    val dialogContext : Context = context
    val currentActivity:AppCompatActivity = act
    val fragment:Fragment = frg

    override fun onPreExecute() {
        super.onPreExecute()
        progressCircle.show(dialogContext)
    }

    override fun doInBackground(vararg params: String?): ArrayList<Information> {

        try{
            val doc = Jsoup.connect(params[0]).get()
            //val data = doc.select("#main_table_countries > tbody > tr")
            //val data = doc.select("#main_table_countries_yesterday > tbody > tr")
            val data = doc.select("#main_table_countries_today > tbody > tr")

            var country :String
            var totalCases : String
            var newCases : String
            var totalDeaths :String
            var newDeaths : String
            var totalRecovered : String

            // country cnt
            var countCnt :Int = 1

            for (datum in data){

                country = datum.select("td")[0].text().trim()
                totalCases = datum.select("td")[1].text().trim()
                newCases = datum.select("td")[2].text().trim()
                totalDeaths = datum.select("td")[3].text().trim()
                newDeaths = datum.select("td")[4].text().trim()
                totalRecovered = datum.select("td")[5].text().trim()

                // 영어 -> 한글
                country = CountryTrans(country)

                if(totalCases.length == 0){
                     totalCases += '0'
                }
                if (totalDeaths.length == 0){
                    totalDeaths += '0'
                }
                if (totalRecovered.length == 0){
                    totalRecovered += '0'
                }
                if (newCases.length == 0){
                    newCases += "+0"
                }
                if (newDeaths.length == 0){
                    newDeaths += "+0"
                }

                val total = Information(null,country,totalCases + '\n' + newCases,totalDeaths + '\n' + newDeaths, totalRecovered)

                infoList.add(total)

                countCnt++
            }

            // total data addtotalCases
            val totalCasesSum:String
            val totalDeathsSum:String
            val totalRecoveredSum:String

            infoList[infoList.size - 1].totalCases.split("\n").let{ it->
                totalCasesSum = it[0]
            }

            infoList[infoList.size - 1].totalDeaths.split("\n").let { it->
                totalDeathsSum = it[0]
            }

            totalRecoveredSum = infoList[infoList.size - 1].totalRecovered

            //total remove
            infoList.remove(infoList[infoList.size - 1])

            val splitData  = { c: String->
                val case = c.split('\n')

                if (case[0].length > 4){
                    val a = case[0].split(',')
                    a[0] + a[1]
                }else{
                    case[0]
                }
            }

            // totalCase reverse sort
            infoList.sortByDescending { splitData(it.totalCases).toInt() }

            // numberling
           for (i in 0 until infoList.size){
                infoList[i].num = i+1
            }

            infoList.add(Information(0,(countCnt - 2).toString(),totalCasesSum,totalDeathsSum,totalRecoveredSum))

        }catch (e : IOException) {
            Log.d("안됨","안딤")
            e.printStackTrace()
        }

        return infoList
    }

    override fun onProgressUpdate(vararg values: String?) {
        super.onProgressUpdate(*values)
    }

    override fun onPostExecute(result: ArrayList<Information>) {
        super.onPostExecute(result)
        Singleton.coronaList = result

        progressCircle.dialog.dismiss()

        currentActivity.supportFragmentManager.beginTransaction()
            //.setCustomAnimations(R.anim.design_bottom_sheet_slide_in, R.anim.design_bottom_sheet_slide_out)
            .replace(R.id.frameLayout, fragment, fragment.javaClass.simpleName)
            .commit()
    }

}

