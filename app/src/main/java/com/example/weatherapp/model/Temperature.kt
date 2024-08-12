package com.example.weatherapp.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class Temperature(
    @SerializedName("temp")
    val temperature: Float? = null,

    @SerializedName("feels_like")
    val feels_like: Float? = null,

    @SerializedName("temp_min")
    val temperature_min: Float? = null,

    @SerializedName("temp_max")
    val temperature_max: Float? = null,

    @SerializedName("humidity")
    val humidity: Int? = null,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Float::class.java.classLoader) as? Float,
        parcel.readValue(Float::class.java.classLoader) as? Float,
        parcel.readValue(Float::class.java.classLoader) as? Float,
        parcel.readValue(Float::class.java.classLoader) as? Float,
        parcel.readValue(Int::class.java.classLoader) as? Int
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(temperature)
        parcel.writeValue(feels_like)
        parcel.writeValue(temperature_min)
        parcel.writeValue(temperature_max)
        parcel.writeValue(humidity)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Temperature> {
        override fun createFromParcel(parcel: Parcel): Temperature {
            return Temperature(parcel)
        }

        override fun newArray(size: Int): Array<Temperature?> {
            return arrayOfNulls(size)
        }
    }
}
