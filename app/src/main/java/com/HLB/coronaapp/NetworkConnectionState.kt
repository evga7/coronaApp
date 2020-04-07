package com.HLB.coronaapp

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.HLB.coronaapp.singleton.Singleton


@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class NetworkConnectionState(context: Context) : ConnectivityManager.NetworkCallback() {

    private var context : Context? = null
    private var networkRequest: NetworkRequest? = null
    private var connectivityManager : ConnectivityManager? = null

    init {
        this.context = context
        this.networkRequest = // 모바일 네트워크 상태
            NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI).build()
    }

    fun register() {
        connectivityManager = context!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager!!.registerNetworkCallback(networkRequest!!, this)
    }

    fun unregister() {
        connectivityManager!!.unregisterNetworkCallback(this)
    }

    override fun onAvailable(network: Network) {
        super.onAvailable(network)
        Singleton.isNetworkConnected = false
    }

    override fun onUnavailable() {
        super.onUnavailable()
        Singleton.isNetworkConnected = true
    }

    override fun onLost(network: Network) {
        super.onLost(network)

    }
}

