package com.example.coronaapp.korea

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.text.Layout
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.coronaapp.R
import com.example.coronaapp.Singleton
import com.example.coronaapp.korea.koreaAsync.koreaAsyncMainData
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import kotlinx.android.synthetic.main.dialog.view.*
import kotlinx.android.synthetic.main.fragment_korea.view.*


class FragmentKorea : Fragment() {

    val weburl = "http://ncov.mohw.go.kr"
    val TAG = "Main Activity"

    val infoMainText = String()


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
    }




    companion object {
        fun newInstance(): FragmentKorea {
            val FragmentKorea = FragmentKorea()

            val args = Bundle()
            FragmentKorea.arguments = args

//            if (Singleton.coList==null)
//                koreaAsyncMainData().execute("http://ncov.mohw.go.kr").get()
            /*
            if (Singleton.coList2==null)
                koreaAsyncCityMap().execute("http://ncov.mohw.go.kr").get()
            if (Singleton.coList3==null)
                koreaAsyncCityData().execute("http://ncov.mohw.go.kr").get()

             */
            return FragmentKorea
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var koreaFragView= inflater.inflate(R.layout.fragment_korea, container, false)
        koreaFragView.infoText.setText(Singleton.coList!![4].title)
        koreaFragView.infectedText1.setText(Singleton.coList!![0].title)
        koreaFragView.infectedText2.setText(Singleton.coList!![0].num?.substring(4))
        koreaFragView.infectedText3.setText(Singleton.coList!![0].before?.substring(5))
        koreaFragView.cureText1.setText(Singleton.coList!![1].title)
        koreaFragView.cureText2.setText(Singleton.coList!![1].num)
        koreaFragView.cureText3.setText(Singleton.coList!![1].before)
        koreaFragView.careText1.setText(Singleton.coList!![2].title)
        koreaFragView.careText2.setText(Singleton.coList!![2].num)
        koreaFragView.careText3.setText(Singleton.coList!![2].before)
        koreaFragView.deadText1.setText(Singleton.coList!![3].title)
        koreaFragView.deadText2.setText(Singleton.coList!![3].num)
        koreaFragView.deadText3.setText(Singleton.coList!![3].before)
        koreaFragView.todayInfedctText.setText(Singleton.coList!![11].title)
        koreaFragView.todayInfedctNumText.setText(Singleton.coList!![11].num)
        koreaFragView.todayCureText.setText(Singleton.coList!![12].title)
        koreaFragView.todayCureNumText.setText(Singleton.coList!![12].num)
        var piechart : PieChart
        piechart=koreaFragView.Piechart
        piechart.setUsePercentValues(true)
        var yValue: ArrayList<PieEntry> = arrayListOf()

        for (i in 5..7) {
            val st = Singleton.coList!![i].title + " " + Singleton.coList!![i].num + " 명 " + Singleton.coList!![i].before
            Singleton.coList?.get(i)?.before?.substringBefore('%')?.toFloat()?.let {
                PieEntry(
                    it,st
                )
            }?.let { yValue.add(it) }
        }
        val pieData = PieDataSet(yValue,null)
        var colors : ArrayList<Int> = arrayListOf<Int>()
        colors.add(Color.parseColor("#D6CE8E"))
        colors.add(Color.parseColor("#CB7474"))
        colors.add(Color.parseColor("#BCDD95"))
        pieData.setColors(colors)

        val pieda=PieData(pieData)

        piechart.setEntryLabelTextSize(0f)
        pieda.setValueTextColor(Color.BLACK)
        pieda.setValueTextSize(0f)

        piechart.description.setEnabled(false)
        pieData.setSliceSpace(1f)

        val l = piechart.legend
        l.setWordWrapEnabled(true)
        piechart.setData(pieda)
        l.setTextSize(11f)
        piechart.animateY(2000,Easing.EaseOutQuad)


        koreaFragView.currentInfoText1.setText(Singleton.coList!![8].title.substringAfter(" "))
        koreaFragView.currentInfoText2.setText(Singleton.coList!![8].num)

        koreaFragView.currentInfoText3.setText(Singleton.coList!![9].title.substringAfter(" "))
        koreaFragView.currentInfoText4.setText(Singleton.coList!![9].num)

        koreaFragView.currentInfoText5.setText(Singleton.coList!![10].title.substringAfter(" "))
        koreaFragView.currentInfoText6.setText(Singleton.coList!![10].num)

        val textView = TextView(koreaFragView.context)
        var tempStr : String
        textView.setTextColor(Color.GREEN)
        var j = 1
        for (i in 0..17) {
            val textId1 = koreaFragView.resources.getIdentifier("cityText" + j,"id", context?.packageName.toString())
            val textId2 = koreaFragView.resources.getIdentifier("cityText" + (j+1),"id", context?.packageName.toString())
            val textId3 = koreaFragView.resources.getIdentifier("cityText" + (j+2),"id", context?.packageName.toString())
            koreaFragView.findViewById<TextView>(textId1).setText(Singleton.coList2?.get(i)?.title)
            koreaFragView.findViewById<TextView>(textId2).setText(Singleton.coList2?.get(i)?.num)
            koreaFragView.findViewById<TextView>(textId3).setText(Singleton.coList2?.get(i)?.before)
            j+=3
        }



        for (i in 0..17) {
            val layoutId = koreaFragView.resources.getIdentifier(
                "cityButton" + (i + 1),
                "id",
                context?.packageName.toString()
            )
            koreaFragView.findViewById<LinearLayout>(layoutId).setOnClickListener{
                val dialogView = layoutInflater.inflate(R.layout.dialog, null)

                dialogView.dialogText.setText(Singleton.coList3!![i].city)
                dialogView.dialogInfectNum.setText(Singleton.coList3!![i].num+"명")

                dialogView.dialogBeforeNum.setText(Singleton.coList3!![i].before)


                dialogView.dialogDeadNum.setText(Singleton.coList3!![i].dead+"명")


                dialogView.dialogUnIsolatedNum.setText(Singleton.coList3!![i].unIsolated+"명")


                dialogView.dialogIncidenceNum.setText(Singleton.coList3!![i].incidenceRate+"명")


                var pieChart : PieChart
                pieChart=dialogView.dialogPiechart
                pieChart.setEntryLabelTextSize(0f)
                var yValue: ArrayList<PieEntry> = arrayListOf()
                yValue.add(PieEntry(Singleton.coList3!![i].cityPencentage.substringBefore('%').toFloat(),""))
                yValue.add(PieEntry(100- Singleton.coList3!![i].cityPencentage.substringBefore('%').toFloat(),""))
                var subText1 = Singleton.coList3!![i].cityPencentage.substringBefore(' ')
                var subText2 = Singleton.coList3!![i].cityPencentage.substringAfter(' ').substringBefore(' ')
                var subText3 = Singleton.coList3!![i].cityPencentage.substringAfter(' ').substringAfter(' ')
                pieChart.setCenterText(subText2+'\n'+subText3+'\n'+subText1)
                val pieData = PieDataSet(yValue,null)
                pieChart.setUsePercentValues(true)
                var colors : ArrayList<Int> = arrayListOf<Int>()
                colors.add(Color.parseColor("#4C62DF"))
                colors.add(Color.parseColor("#B0A4A4"))
                pieChart.setHighlightPerTapEnabled(true)
                pieData.setColors(colors)

                val pieda=PieData(pieData)
                pieChart.animateY(2000,Easing.EaseOutQuad)
                pieda.setValueTextColor(Color.BLACK)
                pieda.setValueTextSize(0f)

                pieChart.description.setEnabled(false)

                pieChart.legend.setEnabled(false)
                pieChart.setData(pieda)


                val builder = AlertDialog.Builder(this.context).setView(dialogView)
                var buildershow = builder.show()
                dialogView.dialogCloseImage.setOnClickListener{
                    buildershow.dismiss()         }

            }

        }



        return koreaFragView
    }
    data class Item(val title: String, val num:String, val before: String)
    data class CityItem(val city: String, val num:String, val before: String,val dead : String, val unIsolated: String,val incidenceRate:String,val cityPencentage:String )

}

// 프레그먼트는 메모리 확보를 위해 파기 될 수 있는데 이때 보통
// onSaveInstanceState()를 통해서 데이터를 보관하게 됨
// 하지만 newInstance 를 통하여 세팅되면 재생성 시 번들을 통하여
// 다시 세팅되어 넘어오게 되는데 그렇게 되게 하기 위하여
// 우선 작업?처리?를 아래와 같이 해놓은 것임.