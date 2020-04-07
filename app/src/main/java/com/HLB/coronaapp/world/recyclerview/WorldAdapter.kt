package com.HLB.coronaapp.world.recyclerview

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.HLB.coronaapp.world.worldData.Information
import com.HLB.coronaapp.R
import kotlinx.android.synthetic.main.world_list.view.*

class WorldAdapter(val worldItems:ArrayList<Information>) : RecyclerView.Adapter<WorldAdapter.WorldViewHolder>() {

    override fun getItemCount(): Int = worldItems.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = WorldViewHolder(parent)

    //items -> textview
    override fun onBindViewHolder(holder: WorldViewHolder, pos: Int) {

        //한번만 바인
        holder.setIsRecyclable(false)
        holder.bind(worldItems[pos])

        // + 부분부터 색깔 변환
        if ('+' in holder.totalCases.text) {
            val totalCasesPlus = holder.totalCases.text.indexOf('+')
            SpannableString(holder.totalCases.text).let { spannableCases ->
                spannableCases.setSpan(ForegroundColorSpan(Color.RED), totalCasesPlus, holder.totalCases.text.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                holder.totalCases.setText(spannableCases, TextView.BufferType.SPANNABLE)
            }
        }

        if ('+' in holder.totalDeaths.text) {
            val totalDeathsPlus = holder.totalDeaths.text.indexOf('+')
            SpannableString(holder.totalDeaths.text).let { spannableDeaths ->
                spannableDeaths.setSpan(ForegroundColorSpan(Color.RED), totalDeathsPlus, holder.totalDeaths.text.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                holder.totalDeaths.setText(spannableDeaths, TextView.BufferType.SPANNABLE)
            }
        }

        // RecyclerView color add
        if(pos % 2 == 1)
        {
            holder.itemView.coronaTable.setBackgroundColor(Color.parseColor("#E1F5FE"))
        }
        else{
            holder.itemView.coronaTable.setBackgroundColor(Color.parseColor("#B3E5FC"))
        }

    }


    inner class WorldViewHolder(parent: ViewGroup?) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent?.context).inflate(R.layout.world_list, parent, false)) {

        val number = itemView.number
        val country = itemView.country
        val totalCases= itemView.totalCases
        val totalDeaths= itemView.totalDeaths
        val totalRecovered = itemView.totalRecovered

        fun bind(listInfo: Information){
            number?.text = listInfo.num.toString()
            country?.text = listInfo.country
            totalCases?.text = listInfo.totalCases
            totalDeaths?.text = listInfo.totalDeaths
            totalRecovered?.text = listInfo.totalRecovered
        }

    }

}



