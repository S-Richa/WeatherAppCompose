package com.example.weatherapp.model

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class WeatherData<T>(
    @SerializedName("coord")
    val coordinate: Coordinate? = null,

    @SerializedName("wind")
    val wind: Wind? = null,

    @SerializedName("main")
    val temperature: Temperature? = null,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("weather")
    val weatherList: ArrayList<T>? = null
) : Parcelable {
    @SuppressLint("NewApi")
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(Coordinate::class.java.classLoader, Coordinate::class.java),
        parcel.readParcelable(Wind::class.java.classLoader, Wind::class.java),
        parcel.readParcelable(Temperature::class.java.classLoader, Temperature::class.java),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readArrayList(ArrayList::class.java.classLoader, ArrayList::class.java) as? ArrayList<T>
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(coordinate, flags)
        parcel.writeParcelable(wind, flags)
        parcel.writeParcelable(temperature, flags)
        parcel.writeString(name)
        parcel.writeValue(id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<WeatherData<Any>> {
        override fun createFromParcel(parcel: Parcel): WeatherData<Any> {
            return WeatherData(parcel)
        }

        override fun newArray(size: Int): Array<WeatherData<Any>?> {
            return arrayOfNulls(size)
        }
    }
}