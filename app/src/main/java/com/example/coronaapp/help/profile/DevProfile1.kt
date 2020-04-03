package com.example.coronaapp.help.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.coronaapp.R
import com.vansuita.materialabout.builder.AboutBuilder
import com.vansuita.materialabout.views.AboutView

class DevProfile1: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view1: AboutView

        view1 = AboutBuilder.with(this.context)
            .setPhoto(R.mipmap.profile_picture)
            .setCover(R.mipmap.profile_cover)
            .setName("홍원표")
            .setSubTitle("Mobile Developer")
            .setBrief("ㅎㅇ")
            .setAppIcon(R.mipmap.ic_launcher)
            .setAppName(R.string.app_name)
            .addGooglePlayStoreLink("8002078663318221363")
            .addGitHubLink("user")
            .addFacebookLink("user")
            .addFiveStarsAction()
            .setWrapScrollView(true)
            .setLinksAnimated(true)
            .setShowAsCard(true)
            .build();

        return view1
    }
}