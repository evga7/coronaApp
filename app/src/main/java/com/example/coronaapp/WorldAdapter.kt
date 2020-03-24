package com.example.coronaapp

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TableRow
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_world.view.*
import kotlinx.android.synthetic.main.world_list.view.*

class WorldAdapter(val worldItems:ArrayList<Information>) : RecyclerView.Adapter<WorldAdapter.WorldViewHolder>() {

    var items: MutableList<Information> = mutableListOf(
        Information("국가","확진자","사망자","회복"),
        Information("china","80,928"+"\n"+"+34","3,245"+"\n"+"+34","70,420")
        ,Information("요맨","111"+"\n"+"+34","300"+"\n"+"+34","100"),
        Information("china","80,928"+"\n"+"+34","3,245"+"\n"+"+34","70,420")
        ,Information("요맨","111"+"\n"+"+34","300"+"\n"+"+34","100"),
        Information("china","80,928"+"\n"+"+34","3,245"+"\n"+"+34","70,420")
        ,Information("요맨","111"+"\n"+"+34","300"+"\n"+"+34","100"),
        Information("china","80,928"+"\n"+"+34","3,245"+"\n"+"+34","70,420")
        ,Information("요맨","111"+"\n"+"+34","300"+"\n"+"+34","100")
    )

    override fun getItemCount(): Int = worldItems.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = WorldViewHolder(parent)


    //items -> textview
    override fun onBindViewHolder(holder: WorldViewHolder, pos: Int) {
        val recyclerviewColor = arrayListOf<String>("#B1BCBE")

        // 한번만 바인딩
        holder.setIsRecyclable(false)
        holder.bind(worldItems[pos])

        // + 부분부터 색깔 변환
        if ('+' in holder.totalCases.text){
            val totalCasesPlus = holder.totalCases.text.indexOf('+')
            val spannableCases = SpannableString(holder.totalCases.text)
            spannableCases.setSpan(ForegroundColorSpan(Color.RED), totalCasesPlus, holder.totalCases.text.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            holder.totalCases.setText(spannableCases, TextView.BufferType.SPANNABLE)
        }

        if ('+' in holder.totalDeaths.text){
            val totalDeathsPlus = holder.totalDeaths.text.indexOf('+')
            val spannableDeaths = SpannableString(holder.totalDeaths.text)
            spannableDeaths.setSpan(ForegroundColorSpan(Color.RED), totalDeathsPlus, holder.totalDeaths.text.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            holder.totalDeaths.setText(spannableDeaths, TextView.BufferType.SPANNABLE)
        }

    }


    inner class WorldViewHolder(parent: ViewGroup?) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent?.context).inflate(R.layout.world_list, parent, false)) {

        val country = itemView.country
        val totalCases= itemView.totalCases
        val totalDeaths= itemView.totalDeaths
        val totalRecovered = itemView.totalRecovered

        fun bind(listInfo:Information){
            country?.text = listInfo.country
            totalCases?.text = listInfo.totalCases
            totalDeaths?.text = listInfo.totalDeaths
            totalRecovered?.text = listInfo.totalRecovered
        }

    }

}
