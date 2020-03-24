package com.example.coronaapp

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