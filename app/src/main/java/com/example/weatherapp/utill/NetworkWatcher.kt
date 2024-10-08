package com.example.weatherapp.utill

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import androidx.annotation.IntRange
import androidx.annotation.RequiresPermission
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine

class NetworkWatcher
@RequiresPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)
private constructor(application: Context) {
    private val connectivityManager =
        application.getSystemService(Context.CONNECTIVITY_SERVICE)
                as ConnectivityManager

    var isOnline = false
        get() {
            updateFields()
            return field
        }

    private var isOverWifi = false
        get() {
            updateFields()
            return field
        }

    private var isOverCellular = false
        get() {
            updateFields()
            return field
        }

    private var isOverEthernet = false
        get() {
            updateFields()
            return field
        }

    companion object {
        @Volatile
        private var INSTANCE: NetworkWatcher? = null

        fun getInstance(application: Context): NetworkWatcher {
            synchronized(this) {
                if (INSTANCE == null) {
                    INSTANCE = NetworkWatcher(application)
                }
                return INSTANCE!!
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun updateFields() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            val networkAvailability =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)

            if (networkAvailability != null &&
                networkAvailability.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                networkAvailability.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            ) {
                isOnline = true

                isOverWifi =
                    networkAvailability.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)

                isOverCellular =
                    networkAvailability.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)

                isOverEthernet =
                    networkAvailability.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
            } else {
                isOnline = false
                isOverWifi = false
                isOverCellular = false
                isOverEthernet = false
            }
        } else {

            val info = connectivityManager.activeNetworkInfo
            if (info != null && info.isConnected) {
                isOnline = true

                val wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                isOverWifi = wifi != null && wifi.isConnected

                val cellular = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                isOverCellular = cellular != null && cellular.isConnected

                val ethernet = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET)
                isOverEthernet = ethernet != null && ethernet.isConnected

            } else {
                isOnline = false
                isOverWifi = false
                isOverCellular = false
                isOverEthernet = false
            }
        }
    }

    fun watchNetwork(): Flow<Boolean> = watchWifi()
        .combine(watchCellular()) { wifi, cellular -> wifi || cellular }
        .combine(watchEthernet()) { wifiAndCellular, ethernet -> wifiAndCellular || ethernet }

    private fun watchWifi(): Flow<Boolean> = callbackFlowForType(NetworkCapabilities.TRANSPORT_WIFI)

    private fun watchCellular(): Flow<Boolean> =
        callbackFlowForType(NetworkCapabilities.TRANSPORT_CELLULAR)

    private fun watchEthernet(): Flow<Boolean> =
        callbackFlowForType(NetworkCapabilities.TRANSPORT_ETHERNET)

    private fun callbackFlowForType(@IntRange(from = 0, to = 7) type: Int) = callbackFlow {

        trySend(false)

        val networkRequest = NetworkRequest.Builder()
            .addTransportType(type)
            .build()

        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                trySend(true)
            }

            override fun onUnavailable() {
                trySend(false)
            }

            override fun onLost(network: Network) {
                trySend(false)
            }
        }

        connectivityManager.registerNetworkCallback(networkRequest, callback)

        awaitClose { connectivityManager.unregisterNetworkCallback(callback) }
    }
}