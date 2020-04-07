package com.HLB.coronaapp.help.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.HLB.coronaapp.R


class licensesfrag : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var devview = inflater.inflate(R.layout.licenses, container, false)

        return devview
    }


}