package com.example.coronaapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.world_list.view.*

class WorldAdapter(val worldItems:ArrayList<Information>) : RecyclerView.Adapter<WorldAdapter.WorldViewHolder>() {

    var items: MutableList<Information> = mutableListOf(
        Information("china","80,928"+"\n"+"+34","3,245"+"\n"+"+34","70,420")
        ,Information("요맨","111"+"\n"+"+34","300"+"\n"+"+34","100"),
        Information("china","80,928"+"\n"+"+34","3,245"+"\n"+"+34","70,420")
        ,Information("요맨","111"+"\n"+"+34","300"+"\n"+"+34","100"),
        Information("china","80,928"+"\n"+"+34","3,245"+"\n"+"+34","70,420")
        ,Information("요맨","111"+"\n"+"+34","300"+"\n"+"+34","100"),
        Information("china","80,928"+"\n"+"+34","3,245"+"\n"+"+34","70,420")
        ,Information("요맨","111"+"\n"+"+34","300"+"\n"+"+34","100")

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
