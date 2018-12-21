package org.thinc.comprog.letsmeet.data.model

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Location(
    val lat: Double = 0.0,
    val lon: Double = 0.0,
    val name: String = "undecided"
) : Parcelable {
    fun toMap(): Map<String, Any> = hashMapOf(
        "lat" to lat,
        "lon" to lon,
        "name" to name
    )
    fun toLatLng(): LatLng = LatLng(lat,lon)
}