package com.example.weatherapp.api

import com.example.weatherapp.model.LocationData
import com.example.weatherapp.model.Weather
import com.example.weatherapp.model.WeatherData
import com.example.weatherapp.utill.ADDRESS_CALL
import com.example.weatherapp.utill.WEATHER_CALL
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitAPI(baseUrl: String) : WeatherAPI {

    private var retrofitAPI: WeatherWebAPI
    private val callTimeout = 0L
    private val connectTimeout = 5L
    private val readTimeout = 5L
    private val writeTimeout = 5L
    private val successCode = 200

    init {
        val httpClient = OkHttpClient.Builder()
            .connectTimeout(connectTimeout, TimeUnit.MINUTES)
            .readTimeout(readTimeout, TimeUnit.MINUTES)
            .writeTimeout(writeTimeout, TimeUnit.MINUTES)

        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(baseUrl)
            .client(httpClient.build())
            .build()

        retrofitAPI = retrofit.create(WeatherWebAPI::class.java)
    }

    override suspend fun getWeatherData(
        latitude: Float,
        longitude: Float,
        apiKey: String,
        units: String,
        success: (ResponseData, Int) -> Unit,
        failure: (FetchError) -> Unit
    ) {
        retrofitAPI.getWeatherData(latitude, longitude, apiKey, units).enqueue(object : Callback<WeatherData<Weather>> {
            override fun onResponse(
                call: Call<WeatherData<Weather>>,
                response: Response<WeatherData<Weather>>
            ) {
                if (response.isSuccessful && response.code() == successCode) {
                    val responseData = response.body()
                    responseData?.let {
                        success(responseData, WEATHER_CALL)
                    }
                } else failure(response.message())
            }

            override fun onFailure(call: Call<WeatherData<Weather>>, t: Throwable) {
                failure(t.message.toString())
            }
        })
    }

    override suspend fun getLocationFromAddress(
        searchString: String,
        apiKey: String,
        success: (ResponseData, Int) -> Unit,
        failure: (FetchError) -> Unit
    ) {
        retrofitAPI.getLocationFromAddress(searchString, apiKey).enqueue(object : Callback<ArrayList<LocationData>> {
            override fun onResponse(
                call: Call<ArrayList<LocationData>>,
                response: Response<ArrayList<LocationData>>
            ) {
                if (response.isSuccessful && response.code() == successCode) {
                    val responseData = response.body()
                    responseData?.let {
                        success(responseData, ADDRESS_CALL)
                    }
                } else failure(response.message())
            }

            override fun onFailure(call: Call<ArrayList<LocationData>>, t: Throwable) {
                failure(t.message.toString())
            }
        })
    }
}

