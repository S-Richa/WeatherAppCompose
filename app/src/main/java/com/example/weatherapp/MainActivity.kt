package com.example.weatherapp

import android.app.Application
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.weatherapp.model.WeatherUiState
import com.example.weatherapp.ui.theme.WeatherAppComposeTheme
import com.example.weatherapp.utill.NetworkWatcher
import com.example.weatherapp.viewmodel.WeatherDataViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

class MainActivity : ComponentActivity() {
    private val peopleListViewModel: WeatherDataViewModel by viewModels()
    private var networkWatcher=NetworkWatcher.getInstance(applicationContext)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { ShowCurrentWeather(peopleListViewModel, networkWatcher) }
    }

//    fun onQueryTextSubmit(query: String?): Boolean {
//        if (NetworkWatcher.getInstance(applicationContext).isOnline) {
//            query?.run {
//                peopleListViewModel.getLocationFromAddress(this)
//            }
//            searchItem?.collapseActionView()
//        } else
//            Toast.makeText(this@WeatherDashboardActivity, getString(R.string.network_error), Toast.LENGTH_SHORT).show()
//        return false
//    }
}

@Composable
fun ShowCurrentWeather(peopleListViewModel: WeatherDataViewModel, networkWatcher: NetworkWatcher) {
    var isSearchIconVisible= remember { mutableStateOf(true) }
    var searchText= remember { mutableStateOf("Indore") }
    var isProgressVisible= remember { mutableStateOf(false) }
    val uiState: WeatherUiState by peopleListViewModel.uiState.collectAsStateWithLifecycle()
    WeatherAppComposeTheme {
    Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
        if (isSearchIconVisible.value) { DefaultAppbar(){
            isSearchIconVisible.value=false
        } }else{ SearchBar(
            searchValue = searchText.value,
            onTextChange = { searchText.value=it}
        ) {
            isSearchIconVisible.value=true
            isProgressVisible.value=true
            // call Service
            if (networkWatcher.isOnline)
                searchText?.run {
                peopleListViewModel.getLocationFromAddress(this.value)
            }
        }}

    } ) { innerPadding ->
        Greeting(
            name = "Android",
            modifier = Modifier.padding(innerPadding),
            isProgressVisible.value
        )

    }}
}

@Composable
fun CurrentDayWeather(uiState: WeatherUiState, peopleListViewModel: WeatherDataViewModel ) {
    val weatherIconState: Bitmap by peopleListViewModel.weatherIconState.collectAsStateWithLifecycle()
 Column {
     uiState.weatherData?.weatherList?.let {
         peopleListViewModel.getWeatherIcon(it[0].icon)
         CurrentWeatherImage(weatherIconState)
         Text(text = it[0].weatherDescription.toString())
//         binding.tvWeatherDetail.text = it[0].weatherDescription
             peopleListViewModel.getWeatherIcon(it[0].icon)
     }

     Text(text = "Indore")
 }
}

@Composable
fun CurrentWeatherImage(weatherIconState: Bitmap){
    Image(bitmap = weatherIconState.asImageBitmap(), contentDescription = "weathericon")
}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier, isProgressVisible:Boolean) {

        Box(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(Color.Red),) {
            Text(
                text = "Hello $name!",
                modifier = modifier,

                )
            if(isProgressVisible){
            CircularProgressIndicator(
                modifier = Modifier
                    .width(64.dp)
                    .height(64.dp)
                    .align(Alignment.Center),
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )}
        }
//    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultAppbar(searchIconClicked:()->Unit){
    var isSearchIconVisible= remember { mutableStateOf(true) }
    TopAppBar(title = { Row (modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, ){
        Text(text = "Current Weather")

            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
                horizontalArrangement = Arrangement.Absolute.Right,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(id = com.google.android.libraries.places.R.drawable.quantum_ic_search_grey600_24),
                    contentDescription = "image",
                    Modifier.clickable {
                        searchIconClicked()
                        isSearchIconVisible.value=false
                    })
            }

    }
    })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(searchValue : String, onTextChange : (value: String)->Unit, onSearchClick:(value:String)->Unit){
    val keyboardController = LocalSoftwareKeyboardController.current
    Surface( modifier = Modifier
        .fillMaxWidth()
        .height(64.dp)) {
        TextField(value = searchValue, onValueChange = {
            onTextChange(it) },
            placeholder ={ Text(text = "Search for a city")},
            trailingIcon = { Icon(painter = painterResource(id = com.google.android.libraries.places.R.drawable.quantum_ic_search_grey600_24), contentDescription = "dummy")},
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {
                onSearchClick(searchValue)
                keyboardController?.hide()
            })
        )
    }
}

@Composable
fun ProgressBar(){
    CircularProgressIndicator(
        modifier = Modifier
            .width(64.dp)
            .height(64.dp),
        color = MaterialTheme.colorScheme.secondary,
        trackColor = MaterialTheme.colorScheme.surfaceVariant,
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    WeatherAppComposeTheme {
       ShowCurrentWeather(WeatherDataViewModel(Application()), NetworkWatcher.getInstance(Application()))
    }
}
@Preview(showBackground = true)
@Composable
fun SearchBarPreview() {
    WeatherAppComposeTheme {
        SearchBar("indore",{}){}
    }
}
@Preview(showBackground = true)
@Composable
fun ProgressBarPreview() {
    WeatherAppComposeTheme {
       ProgressBar()
    }
}

@Preview(showBackground = true)
@Composable
fun CurrentDayWeatherPreview() {
    WeatherAppComposeTheme {
        CurrentDayWeather(uiState = WeatherUiState(), WeatherDataViewModel(Application()))
    }
}

