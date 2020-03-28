package com.example.coronaapp

import android.content.Context
import com.example.coronaapp.world.Information

class Singleton {
    companion object {
        var coronaList: ArrayList<Information>? = null
        var coronaFlag:Boolean = false
        var countrySum:String? = null
        var totalCasesSum:String? = null
        var totalDeathsSum:String? = null
        var totalRecoveredSum:String? = null
    }
}