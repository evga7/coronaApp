package com.example.coronaapp

import android.app.ActionBar
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.coronaapp.koreaAsync.koreaAsync1
import com.example.coronaapp.koreaAsync.koreaAsync2
import kotlinx.android.synthetic.main.fragment_korea.*
import kotlinx.android.synthetic.main.fragment_korea.view.*
import org.eazegraph.lib.charts.BarChart
import org.eazegraph.lib.models.BarModel

class FragmentKorea : Fragment() {

    val weburl = "http://ncov.mohw.go.kr"
    val TAG = "Main Activity"
    var coList: ArrayList<Item> = arrayListOf()
    var coList2: ArrayList<Item> = arrayListOf()
    val infoMainText = String()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }




    companion object {
        fun newInstance(): FragmentKorea {
            val FragmentKorea = FragmentKorea()
            FragmentKorea.coList= koreaAsync1().execute("http://ncov.mohw.go.kr").get()
            FragmentKorea.coList2= koreaAsync2().execute("http://ncov.mohw.go.kr").get()
            Log.d("fragLog",FragmentKorea.coList.toString())
            val args = Bundle()
            FragmentKorea.arguments = args
            return FragmentKorea
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var koreaFragView= inflater.inflate(R.layout.fragment_korea, container, false)
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
            barChar.addBar(BarModel("",coList[i].before.substringAfter('(').substringBefore('%').toFloat(), 0xFF56B7F1.toInt()))
        koreaFragView.coChartText1.setText("  "+coList[5].title+"\n"+coList[5].num+"명")
        koreaFragView.coChartText2.setText(coList[6].title+"\n    "+coList[6].num+"명")
        koreaFragView.coChartText3.setText(coList[7].title+"\n"+coList[7].num+"명")

        val textView = TextView(koreaFragView.context)
        var tempStr= String()
        textView.setTextColor(Color.GREEN)
        for (i in 0..16) {
            tempStr = coList2[i].title + "\n" + coList2[i].num + "\n" + coList2[i].before
            val sp = SpannableStringBuilder(tempStr)
            sp.setSpan(
                ForegroundColorSpan(Color.BLUE),
                coList2[i].title.length,
                coList2[i].title.length + coList2[i].num.length + 1,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            val dd=koreaFragView.resources.getIdentifier("cityButton"+(i+1),"id",context?.packageName.toString())
            koreaFragView.findViewById<Button>(dd).setText(sp)
        }

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