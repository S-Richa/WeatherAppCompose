package com.example.weatherapp.api

import com.example.weatherapp.model.LocationData
import com.example.weatherapp.model.Weather
import com.example.weatherapp.model.WeatherData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherWebAPI {

    @GET("data/2.5/weather/")
    fun getWeatherData(
        @Query("lat") latitude: Float,
        @Query("lon") longitude: Float,
        @Query("appid") apiKey: String,
        @Query("units") units: String
    ): Call<WeatherData<Weather>>

    @GET("geo/1.0/direct")
    fun getLocationFromAddress(
        @Query("q") searchString: String,
        @Query("appid") apiKey: String
    ): Call<ArrayList<LocationData>>
}


