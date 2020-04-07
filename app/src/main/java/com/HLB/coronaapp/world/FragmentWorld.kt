package com.HLB.coronaapp.world

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.coronaapp.R
import com.HLB.coronaapp.singleton.Singleton
import com.HLB.coronaapp.world.recyclerview.WorldAdapter
import kotlinx.android.synthetic.main.fragment_world.view.*

class FragmentWorld : Fragment() {
    val url = "https://www.worldometers.info/coronavirus/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val rootView = inflater.inflate(R.layout.fragment_world, container, false)

        // tablelayout total data add
        if (Singleton.coronaFlag == false){
            Singleton.coronaFlag = true
            Singleton.countrySum = Singleton.coronaList?.get(
                Singleton.coronaList?.size!! - 1)!!.country
            Singleton.totalCasesSum = Singleton.coronaList?.get(
                Singleton.coronaList?.size!! - 1)!!.totalCases
            Singleton.totalDeathsSum = Singleton.coronaList?.get(
                Singleton.coronaList?.size!! - 1)!!.totalDeaths
            Singleton.totalRecoveredSum = Singleton.coronaList?.get(
                Singleton.coronaList?.size!! - 1)!!.totalRecovered
            Singleton.coronaList?.remove(
                Singleton.coronaList?.get(
                    Singleton.coronaList?.size!!-1)!!)
        }

        rootView.a.text = Singleton.countrySum
        rootView.b.text = Singleton.totalCasesSum
        rootView.c.text = Singleton.totalDeathsSum
        rootView.d.text = Singleton.totalRecoveredSum
        rootView.worldDayInfo.text = Singleton.worldDayInfo

        // RecyclerView
        val worldRecyclerView = rootView.findViewById(R.id.worldrecyclerview) as RecyclerView
        worldRecyclerView.run {
            layoutManager = LinearLayoutManager(activity)
            adapter =
                WorldAdapter(Singleton.coronaList!!)

        }

        return rootView
    }

}

