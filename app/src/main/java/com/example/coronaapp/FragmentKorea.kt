package com.example.coronaapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class FragmentKorea : Fragment() {
    // first
    // 프레그먼트 재생성 시 세팅 해놓은 번들을 통해 다시 넘어오게 할 수 있음.


    companion object {
        fun newInstance(): FragmentKorea {
            val FragmentKorea = FragmentKorea()
            val args = Bundle()
            FragmentKorea.arguments = args
            return FragmentKorea
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_korea, container, false)
    }


}

// 프레그먼트는 메모리 확보를 위해 파기 될 수 있는데 이때 보통
// onSaveInstanceState()를 통해서 데이터를 보관하게 됨
// 하지만 newInstance 를 통하여 세팅되면 재생성 시 번들을 통하여
// 다시 세팅되어 넘어오게 되는데 그렇게 되게 하기 위하여
// 우선 작업?처리?를 아래와 같이 해놓은 것임.