package com.devtedi.tedi.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.lifecycle.LiveData

/**
 *
 * Kelas ini merupakan implementasi dari LiveData, khusus untuk meng-observe kondisi konektivitas device pengguna
 *
 * @constructor untuk buat instance dari ObjectDetectionFragment.
 */
class InternetConnectivityLiveData(context: Context) : LiveData<InternetConnectivityLiveData.Status>() {

    private lateinit var networkCallback: NetworkCallback
    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val validNetworks: MutableSet<Network> = hashSetOf()

    private fun updateValidNetworks() {
        postValue(if (validNetworks.size > 0) Status.Connected else Status.NotConnected)
    }

    override fun onActive() {
        networkCallback = initNetworkCallback()
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()

        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
        super.onActive()
    }

    private fun initNetworkCallback(): NetworkCallback {
        return object : NetworkCallback() {
            override fun onAvailable(network: Network) {
                val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
                val isConnectedToInternet = networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)

                if (isConnectedToInternet == true) {
                    validNetworks.add(network)
                }

                updateValidNetworks()
            }

            override fun onLost(network: Network) {
                validNetworks.remove(network)
                updateValidNetworks()
            }
        }
    }

    override fun onInactive() {
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    enum class Status {
        Connected,
        NotConnected
    }
}