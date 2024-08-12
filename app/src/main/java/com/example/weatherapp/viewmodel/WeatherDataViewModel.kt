package com.example.weatherapp.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.R
import com.example.weatherapp.api.FetchError
import com.example.weatherapp.api.ResponseData
import com.example.weatherapp.api.WeatherAPI
import com.example.weatherapp.model.LocationData
import com.example.weatherapp.model.Weather
import com.example.weatherapp.model.WeatherData
import com.example.weatherapp.model.WeatherUiState
import com.example.weatherapp.utill.ADDRESS_CALL
import com.example.weatherapp.utill.WEATHER_CALL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import java.net.URL

class WeatherDataViewModel(application: Application) : AndroidViewModel(application) {

    private val _failedMessage = MutableLiveData<String>(null)
    val failedMessage: LiveData<String> get() = _failedMessage

    private val _weatherData = MutableLiveData<WeatherData<Weather>>(null)
    val weatherData: LiveData<WeatherData<Weather>> get() = _weatherData

    private val _locationData = MutableLiveData<LocationData>(null)
    val locationData: LiveData<LocationData> get() = _locationData

    private val _weatherIcon = MutableLiveData<Bitmap>(null)
    val weatherIcon: LiveData<Bitmap> get() = _weatherIcon

    private val _showWeatherProgress = MutableLiveData(false)
    val showWeatherProgress: LiveData<Boolean> get() = _showWeatherProgress

    private val _uiState: MutableStateFlow<WeatherUiState> =
        MutableStateFlow(WeatherUiState())
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    private val _weatherIconState :MutableStateFlow<Bitmap?> = MutableStateFlow<Bitmap?>(null)
    val weatherIconState: StateFlow<Bitmap?> get() = _weatherIconState

    private fun getApiKey(): String {
        return getApplication<Application>().resources.getString(R.string.api_key)
    }

    private fun getUnit(): String {
        return getApplication<Application>().resources.getString(R.string.unit)
    }

    private fun getString(resourceString: Int): String {
        return getApplication<Application>().resources.getString(resourceString)
    }

    private fun getWeatherData(
        latitude: Float,
        longitude: Float,
        apiKey: String
    ) {
        _showWeatherProgress.postValue(true)
        viewModelScope.launch {
            WeatherAPI.create().getWeatherData(
                latitude, longitude, apiKey, getUnit(),
                this@WeatherDataViewModel::success,
                this@WeatherDataViewModel::failure
            )
        }
    }

    fun getLocationFromAddress(searchString: String) {
        _showWeatherProgress.postValue(true)
        viewModelScope.launch {
            WeatherAPI.create().getLocationFromAddress(
                searchString, getApiKey(),
                this@WeatherDataViewModel::success,
                this@WeatherDataViewModel::failure
            )
        }
    }

    fun getWeatherIcon(icon: String?) {
        icon?.run {
            viewModelScope.launch {
                val d = viewModelScope.async(Dispatchers.IO) {
                    BitmapFactory.decodeStream(
                        URL("https://openweathermap.org/img/wn/$icon@2x.png").openConnection()
                            .getInputStream()
                    )
                }
                _weatherIcon.postValue(d.await())
            }
        }
    }

    private fun success(responseData: ResponseData, call: Int) {
        when (call) {
            ADDRESS_CALL -> {
                val locationDataArray = responseData as ArrayList<LocationData>
                if (locationDataArray.size > 0) {
                    getWeatherData(locationDataArray[0].latitude!!, locationDataArray[0].longitude!!, getApiKey())
                    _locationData.postValue(locationDataArray[0])
                } else failure(getString(R.string.valid_location_error))
            }
            WEATHER_CALL -> {
                _weatherData.postValue(responseData as WeatherData<Weather>)
                _uiState.value=WeatherUiState(responseData as WeatherData<Weather>)
                _showWeatherProgress.postValue(false)
            }
        }
    }

    private fun failure(error: FetchError) {
        _showWeatherProgress.postValue(false)
        _failedMessage.postValue(error as String)
    }
}