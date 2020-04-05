package com.example.coronaapp.help.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.coronaapp.R
import com.example.coronaapp.help.profile.DevProfile1
import com.example.coronaapp.help.profile.DevProfile2
import com.example.coronaapp.help.profile.DevProfile3
import kotlinx.android.synthetic.main.developer.view.*

class licensesfrag : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var devview = inflater.inflate(R.layout.licenses, container, false)

        return devview
    }


}