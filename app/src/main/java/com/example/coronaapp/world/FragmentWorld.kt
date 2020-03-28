package com.example.coronaapp.world

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.coronaapp.MainActivity
import com.example.coronaapp.R
import com.example.coronaapp.Singleton
import kotlinx.android.synthetic.main.fragment_world.view.*
import java.io.IOException

class FragmentWorld : Fragment() {
    val url = "https://www.worldometers.info/coronavirus/"
    //var coronaList = ArrayList<Information>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(Singleton.coronaList == null){
            try {
                val fra = FragmentWorld()
                Log.d("크롤링","onCreate")
                Singleton.coronaList = fra.context?.let { WorldCrawling(it).execute(url).get() }
            }catch (e : IOException) {
                e.printStackTrace()
            }
        }

    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val rootView = inflater.inflate(R.layout.fragment_world, container, false)

        // tablelayout total data add
        if (Singleton.coronaFlag == false){
            Singleton.coronaFlag = true
            Singleton.countrySum = Singleton.coronaList?.get(Singleton.coronaList?.size!! - 1)!!.country
            Singleton.totalCasesSum = Singleton.coronaList?.get(Singleton.coronaList?.size!! - 1)!!.totalCases
            Singleton.totalDeathsSum = Singleton.coronaList?.get(Singleton.coronaList?.size!! - 1)!!.totalDeaths
            Singleton.totalRecoveredSum = Singleton.coronaList?.get(Singleton.coronaList?.size!! - 1)!!.totalRecovered
            Singleton.coronaList?.remove(Singleton.coronaList?.get(Singleton.coronaList?.size!!-1)!!)
        }
        rootView.a.text = Singleton.countrySum
        rootView.b.text = Singleton.totalCasesSum
        rootView.c.text = Singleton.totalDeathsSum
        rootView.d.text = Singleton.totalRecoveredSum


        // RecyclerView
        val worldRecyclerView = rootView.findViewById(R.id.worldrecyclerview) as RecyclerView
        worldRecyclerView.layoutManager = LinearLayoutManager(activity)
        worldRecyclerView.adapter = WorldAdapter(Singleton.coronaList!!)
        worldRecyclerView.addItemDecoration(
            DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
        )


        return rootView
    }

}

