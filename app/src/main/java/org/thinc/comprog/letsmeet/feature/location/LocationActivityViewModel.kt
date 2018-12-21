package org.thinc.comprog.letsmeet.feature.location

import android.annotation.SuppressLint
import android.app.Activity
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import org.thinc.comprog.letsmeet.data.PartyRepository
import org.thinc.comprog.letsmeet.data.model.Location
import androidx.core.content.ContextCompat.startActivity
import android.content.Intent
import android.net.Uri


class LocationActivityViewModel : ViewModel() {

    var userLocation: Location? = null
    lateinit var partyRepository: PartyRepository
    lateinit var partyLocation: Location

    fun initPartyMarker(mMap: GoogleMap, partyLocation: Location) {
        mMap.addMarker(
            MarkerOptions().position(partyLocation.toLatLng()).title("Party Location").icon(
                BitmapDescriptorFactory.defaultMarker(50.0f)
            )
        )
    }

    fun initUserMarker(mMap: GoogleMap, userLocation: Location) {
        mMap.addMarker(
            MarkerOptions().position(userLocation.toLatLng()).title("Your Location").icon(
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)
            )
        )
    }

    fun zoomParty(mMap: GoogleMap, partyLocation: Location) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(partyLocation.toLatLng(), 15.5f))
    }

    fun zoomUser(mMap: GoogleMap, partyLocation: Location, userLocation: Location) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(boundUserParty(partyLocation, userLocation), 150))
    }

    private fun boundUserParty(partyLocation: Location, userLocation: Location): LatLngBounds =
        LatLngBounds( // southwest & northeast
            LatLng( // coordinate (p.lat,p.lon) and (2*u.lat-p.lat,2*u.lon-p.lon)
                Math.min(partyLocation.lat, 2 * userLocation.lat - partyLocation.lat),
                Math.min(partyLocation.lon, 2 * userLocation.lon - partyLocation.lon)
            ),
            LatLng(
                Math.max(partyLocation.lat, 2 * userLocation.lat - partyLocation.lat),
                Math.max(partyLocation.lon, 2 * userLocation.lon - partyLocation.lon)
            )
        )

    fun updateMap(mMap: GoogleMap, partyLocation: Location, userLocation: Location) {
        mMap.clear()
        initPartyMarker(mMap, partyLocation)
        initUserMarker(mMap, userLocation)
        zoomUser(mMap, partyLocation, userLocation)
    }

    @SuppressLint("MissingPermission")
    fun updateMap(mMap: GoogleMap, partyLocation: Location, locationManager: LocationManager) {
        val locationListener = object : LocationListener {
            override fun onLocationChanged(location: android.location.Location) {
                userLocation = Location(location.latitude, location.longitude)
                updateMap(mMap, partyLocation, userLocation!!)
            }

            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0f, locationListener)
    }

    fun initActiveUsersMarker(mMap: GoogleMap, activeUsersLatLng: List<LatLng>) {
        TODO("2nd phase krub")
    }

    private fun Location.toLatLng(): LatLng = LatLng(this.lat, this.lon)
}