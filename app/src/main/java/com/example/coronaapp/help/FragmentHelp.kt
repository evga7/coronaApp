package com.example.coronaapp.help

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.coronaapp.R
import kotlinx.android.synthetic.main.fragment_help.view.*

class FragmentHelp : Fragment()  {

    private val mFragmentManager = fragmentManager
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var helpview=inflater.inflate(R.layout.fragment_help, container, false)

        helpview.devlayout.setOnClickListener{



            val fragment =devfrag()

            val fragmentManager = fragmentManager
            val transaction =
                fragmentManager!!.beginTransaction()

            transaction.replace(R.id.frameLayout,fragment,fragment.javaClass.simpleName)?.commit()
            transaction.addToBackStack(null)

        }


        return helpview
    }

}