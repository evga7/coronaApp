package com.example.coronaapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.world_list.view.*

class WorldAdapter(val worldItems:ArrayList<Information>) : RecyclerView.Adapter<WorldAdapter.WorldViewHolder>() {

    var items: MutableList<Information> = mutableListOf(
        Information("china","80,928"+"\n"+"123","+34","+8","3,245","70,420"),
                Information("aa","bb","cc","dd","ee","ff"),
    Information("aa","bb","cc","dd","ee","ff"),
    Information("aa","bb","cc","dd","ee","ff"),
    Information("aa","bb","cc","dd","ee","ff")

    )

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = WorldViewHolder(parent)

    //items -> textview
    override fun onBindViewHolder(holder: WorldViewHolder, pos: Int) {
        holder.bind(items[pos])
    }


    inner class WorldViewHolder(parent: ViewGroup?) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent?.context).inflate(R.layout.world_list, parent, false)) {
        val country = itemView.country
        val totalCases= itemView.totalCases
        val newCases= itemView.newCases
        val totalDeaths= itemView.totalDeaths
        val newDeaths = itemView.newDeaths
        val totalRecovered = itemView.totalRecovered

        fun bind(listInfo:Information){
            country?.text = listInfo.country
            totalCases?.text = listInfo.totalCases
            newCases?.text = listInfo.newCases
            totalDeaths?.text = listInfo.totalDeaths
            newDeaths?.text = listInfo.newDeaths
            totalRecovered?.text = listInfo.totalRecovered
        }

    }

}
