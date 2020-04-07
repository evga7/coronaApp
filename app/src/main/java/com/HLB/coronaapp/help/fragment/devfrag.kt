package com.HLB.coronaapp.help.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.coronaapp.R
import com.HLB.coronaapp.singleton.Singleton
import com.HLB.coronaapp.help.profile.DevProfile1
import com.HLB.coronaapp.help.profile.DevProfile2
import com.HLB.coronaapp.help.profile.DevProfile3
import kotlinx.android.synthetic.main.developer.view.*

class devfrag : Fragment() {

    var mBackWait:Long = 0
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var devview = inflater.inflate(R.layout.developer, container, false)
        devview.devHong.setOnClickListener{
            val fragment = DevProfile1()
            val fragmentManager = fragmentManager
            val transaction =
                fragmentManager!!.beginTransaction()
            Singleton.backframent=1
            transaction.replace(R.id.frameLayout,fragment,fragment.javaClass.simpleName)?.commit()
            transaction.addToBackStack(null)
        }
        devview.devLee.setOnClickListener{
            val fragment = DevProfile2()
            val fragmentManager = fragmentManager
            val transaction =
                fragmentManager!!.beginTransaction()
            Singleton.backframent=1
            transaction.replace(R.id.frameLayout,fragment,fragment.javaClass.simpleName)?.commit()
            transaction.addToBackStack(null)
        }
        devview.devByun.setOnClickListener{
            val fragment = DevProfile3()
            val fragmentManager = fragmentManager
            val transaction =
                fragmentManager!!.beginTransaction()
            Singleton.backframent=1
            transaction.replace(R.id.frameLayout,fragment,fragment.javaClass.simpleName)?.commit()
            transaction.addToBackStack(null)
        }

        return devview
    }


}

