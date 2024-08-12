package com.example.weatherapp

import android.app.Application
import com.example.weatherapp.utill.NetworkWatcher
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class WeatherApp : Application() {
    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate() {
        super.onCreate()
        GlobalScope.launch {
            NetworkWatcher.getInstance(this@WeatherApp).watchNetwork().collect {
            }
        }
    }

}