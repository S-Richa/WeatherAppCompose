package com.example.weatherapp.api

interface WeatherAPI {

    suspend fun getWeatherData(
        latitude: Float,
        longitude: Float,
        apiKey: String,
        units: String,
        success: (ResponseData, Int) -> Unit,
        failure: (FetchError) -> Unit
    )

    suspend fun getLocationFromAddress(
        searchString: String,
        apiKey: String,
        success: (ResponseData, Int) -> Unit,
        failure: (FetchError) -> Unit
    )

    companion object {
        private const val BASEURL = "https://api.openweathermap.org/"
        fun create(): WeatherAPI {
            return RetrofitAPI(baseUrl = BASEURL)
        }
    }
}

typealias ResponseData = Any
typealias FetchError = Any

