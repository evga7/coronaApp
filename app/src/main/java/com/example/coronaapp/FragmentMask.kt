package com.example.coronaapp

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.core.widget.TextViewCompat
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_mask.view.*


class FragmentMask : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        var v = inflater.inflate(R.layout.fragment_mask, container, false)
//
//        var newRow = TableRow(activity)
//        var newText = TextView(activity)
//
//        var tableList = mutableListOf<TableRow>()
//        var texts = mutableListOf<TextView>()
//        val Params = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,TableRow.LayoutParams.WRAP_CONTENT)
//        val textParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT)
//        val title = arrayListOf<String>("국가","확진자","새로운 확진자","새로운 사망자","총 사망자","회복")
//
//        Params.column = 1
//        //newRow.setBackgroundColor(Color.parseColor("#FFFFFF"))
//
//
//        for (i in 0 until 6){
//            tableList.add(TableRow(activity))
//            texts.add(TextView(activity))
//            texts[i].text = title[i]
//        }
//
//        for (i in 0 until 1) {
//            for (j in 0 until 6) {
//                //texts[i].layoutParams = textParams
//                TextViewCompat.setTextAppearance(texts[j], R.style.coronaText)
//                tableList[i].addView(texts[j], Params)
//                //newRow.addView(texts[j], Params)
//            }
//            tableList[i].setBackgroundColor(Color.parseColor("#FFFFFF"))
//            //v.coronaTable.addView(newRow)
//            v.coronaTable.addView(tableList[i])
//        }
//
//
//        //v.coronaTable.addView(newRow)

        return v

        //return inflater.inflate(R.layout.fragment_mask, container, false)
    }


}

