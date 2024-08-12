package com.example.weatherapp.model

import com.google.gson.annotations.SerializedName

data class LocationData(
    @SerializedName("lon")
    val longitude: Float? = null,

    @SerializedName("lat")
    val latitude: Float? = null,

    @SerializedName("name")
    val place: String? = null,

    @SerializedName("country")
    val country: String? = null,

    @SerializedName("state")
    val state: String? = null

)
