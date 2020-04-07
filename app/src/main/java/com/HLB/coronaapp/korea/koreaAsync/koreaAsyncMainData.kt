package com.HLB.coronaapp.korea.koreaAsync

import android.app.AlertDialog
import android.content.Context
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.HLB.coronaapp.R
import com.HLB.coronaapp.singleton.Singleton
import com.HLB.coronaapp.korea.FragmentKorea
import com.HLB.coronaapp.progresscircle.CustomProgressCircle
import kotlinx.android.synthetic.main.update_dialog.view.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements

class koreaAsyncMainData(act: AppCompatActivity, context: Context, frg: Fragment): AsyncTask<String, String, ArrayList<FragmentKorea.Item>>(){ //input, progress update type, result type

    val weburl = "http://ncov.mohw.go.kr"

    val progressCircle = CustomProgressCircle()
    val dialogContext : Context = context
    val currentActivity:AppCompatActivity = act
    val fragment:Fragment = frg

    override fun onPreExecute() {
        super.onPreExecute()
        progressCircle.show(dialogContext)
    }

    override fun doInBackground(vararg params: String?): ArrayList<FragmentKorea.Item> {
        val doc: Document = Jsoup.connect("$weburl").get()
        val elts: Elements = doc.select("div.live_left")
        val livenum =doc.select("div.liveNumOuter")
        val chartD= elts.select("div.c_chart.c_chart_is")
        val sumInfoTit=elts.select("ul.suminfo").select("span.tit")
        val sumInfoNum=elts.select("ul.suminfo").select("span.num")
        val mainInfoText=livenum.select("span.livedate").text()
        val infectedNum=livenum.select("div.livenum").select("li")
        val todayInfectAndCure =livenum.select("ul.liveNum_today.mgt8")
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
                chartD.select("p.numinfo1").select("span.num_rnum").text(),
                chartD.select("p.numinfo1").select("span.num_percentage").text())
            )
        temp.add(
            FragmentKorea.Item(
                chartD.select("p.numinfo2").select("span.num_tit").text(),
                chartD.select("p.numinfo2").select("span.num_rnum").text(),
                chartD.select("p.numinfo2").select("span.num_percentage").text())
        )
        temp.add(
                FragmentKorea.Item(
                    chartD.select("p.numinfo3").select("span.num_tit").text(),
                    chartD.select("p.numinfo3").select("span.num_rnum").text(),
                    chartD.select("p.numinfo3").select("span.num_percentage").text())
                )
        for(i in 0..sumInfoNum.size-1)
        {
            temp.add(FragmentKorea.Item(sumInfoTit[i].text(),sumInfoNum[i].text(),""))
        }
        temp.add(FragmentKorea.Item(todayInfectAndCure.select("span.tit1").text(),todayInfectAndCure.select("span.data1").text(),""))
        temp.add(FragmentKorea.Item(todayInfectAndCure.select("span.tit2").text(),todayInfectAndCure.select("span.data2").text(),""))
        Singleton.coList=temp


        val elts2: Elements = doc.select("div#main_maplayout")
        val cityName=elts2.select("span.name")
        val cityNum = elts2.select("span.num")
        val citybefore = elts2.select("span.before")
        val temp2 : ArrayList<FragmentKorea.Item> = arrayListOf()
        for (i in 0..cityName.size-1)
        {
            var tempItem =
                FragmentKorea.Item(cityName[i].text(), cityNum[i].text(),citybefore[i].text())
            temp2.add(tempItem)
        }
        Singleton.coList2=temp2

        val temp3 : ArrayList<FragmentKorea.CityItem> = arrayListOf()
        for (i in 1..18)
        {
            val elts3: Elements = doc.select("div#map_city"+i)
            val cityInfo = elts3.select("ul.cityinfo")
            val num = cityInfo.select("span.num")
            val before_num = cityInfo.select("span.sub_num.red")
            val percentage = elts3.select("p.citytit")
            temp3.add(FragmentKorea.CityItem(elts3.select("h4.cityname").text(),num[0].text(),before_num.text(),num[1].text(),num[2].text(),num[3].text(),percentage.text()))
        }
        Singleton.coList3=temp3
        return temp
    }

    override fun onPostExecute(result: ArrayList<FragmentKorea.Item>) {
        //문서제목 출력
        super.onPostExecute(result)
        progressCircle.dialog.dismiss()
        var i =0
        var flag=0
        for (i in 0..12)
        {
            if (i==4)
            {
                if (Singleton.coList?.get(4)!!.title=="")
                {
                    flag=1
                    break
                }
            }
            else if ((i<4||(i>4&&i<8)))
            {
                if (Singleton.coList?.get(i)!!.title==""|| Singleton.coList?.get(i)!!.num==""|| Singleton.coList?.get(i)!!.before=="") {
                    flag = 1
                    break
                }
            }
            else if (i>7)
            {
                if (Singleton.coList?.get(i)!!.title==""|| Singleton.coList?.get(i)!!.num=="")
                {
                    flag=1
                    break
                }
            }
        }
        if (Singleton.coList?.size!=13||Singleton.coList2?.size!=18||Singleton.coList3?.size!=18)
        {
            flag=1;
        }
        if (flag==0) {
            for (i in 0..Singleton.coList2!!.size - 1) {
                if (Singleton.coList2?.get(i)!!.title == "" || Singleton.coList2?.get(i)!!.num == "" || Singleton.coList2?.get(
                        i
                    )!!.before == ""
                ) {
                    flag = 1
                    break
                }
            }
        }
        if (flag==0) {
            for (i in 0..Singleton.coList3!!.size - 1) {
                if (i == 17) {
                    if (Singleton.coList3?.get(i)!!.city == "" || Singleton.coList3?.get(i)!!.num == "" || Singleton.coList3?.get(
                            i
                        )!!.before == "" ||
                        Singleton.coList3?.get(i)!!.before == "" || Singleton.coList3?.get(i)!!.dead == "" || Singleton.coList3?.get(
                            i
                        )!!.unIsolated == "" ||
                        Singleton.coList3?.get(i)!!.cityPencentage == ""
                    ) {
                        flag = 1
                        break
                    }
                }
                if (Singleton.coList3?.get(i)!!.city == "" || Singleton.coList3?.get(i)!!.num == "" || Singleton.coList3?.get(
                        i
                    )!!.before == "" ||
                    Singleton.coList3?.get(i)!!.before == "" || Singleton.coList3?.get(i)!!.dead == "" || Singleton.coList3?.get(
                        i
                    )!!.unIsolated == "" ||
                    Singleton.coList3?.get(i)!!.incidenceRate == "" || Singleton.coList3?.get(i)!!.cityPencentage == ""
                ) {
                    flag = 1
                    break
                }
            }
        }
        if (flag==1)
        {
            val dialogView =currentActivity.layoutInflater.inflate(R.layout.update_dialog, null)

            val builder = AlertDialog.Builder(dialogContext).setView(dialogView)
            builder.show()
            dialogView.updateOkButton.setOnClickListener {
                System.exit(0)
            }

        }
        else {
            currentActivity.supportFragmentManager.beginTransaction()
                //.setCustomAnimations(R.anim.design_bottom_sheet_slide_in, R.anim.design_bottom_sheet_slide_out)
                .replace(R.id.frameLayout, fragment, fragment.javaClass.simpleName)
                .commitAllowingStateLoss()
        }

    }
}