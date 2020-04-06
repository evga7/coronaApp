package com.example.coronaapp

import androidx.fragment.app.Fragment
import com.example.coronaapp.korea.FragmentKorea
import com.example.coronaapp.world.Information

class Singleton {
    companion object {
        var coronaList: ArrayList<Information>? = null
        var coronaFlag:Boolean = false
        var countrySum:String? = null
        var totalCasesSum:String? = null
        var totalDeathsSum:String? = null
        var totalRecoveredSum:String? = null
        var worldDayInfo:String? = null
        var coList: ArrayList<FragmentKorea.Item>?=null
        var coList2: ArrayList<FragmentKorea.Item>?=null
        var coList3: ArrayList<FragmentKorea.CityItem> ?=null
        var backframent = 0
    }
}