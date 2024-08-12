package com.example.weatherapp

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.example.weatherapp.databinding.ActivityWeatherDashboardBinding
import com.example.weatherapp.model.LocationData
import com.example.weatherapp.model.Weather
import com.example.weatherapp.model.WeatherData
import com.example.weatherapp.utill.NetworkWatcher
import com.example.weatherapp.viewmodel.WeatherDataViewModel
import kotlin.math.roundToInt

class WeatherDashboardActivity : AppCompatActivity(), SearchView.OnQueryTextListener {

    private val peopleListViewModel: WeatherDataViewModel by viewModels()
    private val binding : ActivityWeatherDashboardBinding =ActivityWeatherDashboardBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        peopleListViewModel.showWeatherProgress.observe(this) {
            showProgress(it)
        }

        peopleListViewModel.failedMessage.observe(this) {
            binding.tvNoUpdate.visibility = View.VISIBLE
            binding.groupView.visibility = View.GONE
            it?.let { Toast.makeText(this, it, Toast.LENGTH_SHORT).show() }
        }

        peopleListViewModel.weatherData.observe(this) {
            it?.let { showData(it) }
        }

        peopleListViewModel.locationData.observe(this) {
            it?.let { showLocationData(it) }
        }

        peopleListViewModel.weatherIcon.observe(this) {
            it?.let {  binding.imgWeather.setImageBitmap(it) }
        }
    }

    private fun showProgress(isShow: Boolean) {
        if (isShow) {
            binding.progressBarFetchData.visibility = View.VISIBLE
            binding.tvNoUpdate.visibility = View.GONE
        } else {
            binding.progressBarFetchData.visibility = View.GONE
        }
    }

    private fun showLocationData(locationData: LocationData) {
        locationData.run {
            binding.tvNoUpdate.visibility = View.GONE
            binding.groupView.visibility = View.VISIBLE
            val location: StringBuilder = StringBuilder(place ?: "")
            state?.let { location.append(", $it") }
            country?.let { location.append(", $it") }
            binding.tvCityCountry.text = location
        }
    }

    private fun showData(weatherData: WeatherData<Weather>) {
        weatherData.run {
            binding.groupView.visibility = View.VISIBLE
            setData(this)

        }
    }

    private fun setData(weatherData: WeatherData<Weather>) {


        weatherData.temperature?.run {
            binding.tvTempValue.text = temperature?.roundToInt().toString()
            binding.tvTempFeels.text =
                String.format(getString(R.string.temp_feel) + " " + feels_like?.roundToInt().toString() + getString(R.string.temp_unit))
            binding.tvHumidity.text = String.format(getString(R.string.humidity) + " " + humidity.toString()) + getString(R.string.percent)
            binding.tvMinMaxTemp.text = String.format(temperature_min?.roundToInt().toString() + " ~ " + temperature_max?.roundToInt().toString() + getString(R.string.temp_unit))
        }
        weatherData.wind?.run {
            windSpeed?.let {
                val speed: Double
                if (windSpeed > 0) {
                    speed = convertMetersPerSecToMilesPerHour(windSpeed)
                    binding.tvWind.text = String.format(getString(R.string.wind) + " %.2f ".format(speed) + getString(R.string.milesperhour))
                }
            }
        }
        weatherData.weatherList?.let {
            binding.tvWeatherDetail.text = it[0].weatherDescription
            if (NetworkWatcher.getInstance(applicationContext).isOnline)
                peopleListViewModel.getWeatherIcon(it[0].icon)
        }
    }

    private var searchItem: MenuItem? = null
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.search_menu, menu)
        searchItem = menu!!.findItem(R.id.actionSearch)

        val searchView: SearchView = searchItem?.actionView as SearchView
        searchView.queryHint = getString(R.string.hint_search)
        searchView.isSubmitButtonEnabled = true
        searchView.setOnQueryTextListener(this)
        return true
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (NetworkWatcher.getInstance(applicationContext).isOnline) {
            query?.run {
                peopleListViewModel.getLocationFromAddress(this)
            }
            searchItem?.collapseActionView()
        } else
            Toast.makeText(this@WeatherDashboardActivity, getString(R.string.network_error), Toast.LENGTH_SHORT).show()
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return false
    }

    private fun convertMetersPerSecToMilesPerHour(metersPerSec: Float): Double {
        return metersPerSec * 2.23694
    }
}


