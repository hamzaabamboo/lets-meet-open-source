package org.thinc.comprog.letsmeet.feature.location

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import dagger.android.support.DaggerAppCompatActivity
import org.thinc.comprog.letsmeet.R
import org.thinc.comprog.letsmeet.data.model.Location
import javax.inject.Inject


class LocationActivity : DaggerAppCompatActivity(), OnMapReadyCallback {

    @Inject
    lateinit var viewModel: LocationActivityViewModel

    private var mMap: GoogleMap? = null
    private var userLocation: Location? = null
    private lateinit var partyLocation: Location
    private lateinit var locationManager: LocationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val permissionAsked = savedInstanceState?.getBoolean("permissionAsked")
            if (!(permissionAsked != null && permissionAsked))
                ActivityCompat.requestPermissions(
                    this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 0
                )
        }
        partyLocation = intent.getParcelableExtra("location") as Location
        viewModel.partyLocation = partyLocation
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.location_map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
    }

    fun openInMaps(v: View) {
        val gmmIntentUri = Uri.parse("google.navigation:q=${partyLocation.lat},${partyLocation.lon}")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(mapIntent)
    }
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        viewModel.initPartyMarker(mMap!!, partyLocation)
        viewModel.zoomParty(mMap!!, partyLocation)
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            when (userLocation) {
                null -> viewModel.updateMap(mMap!!, partyLocation, locationManager)
                else -> viewModel.updateMap(mMap!!, partyLocation, userLocation!!) //saved instance state
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.contentEquals(intArrayOf(PERMISSION_GRANTED))) {
            val partyLocation: Location = intent.getParcelableExtra("location") as Location
            viewModel.updateMap(mMap!!, partyLocation, locationManager)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable("userLocation", viewModel.userLocation)
        outState.putParcelable("partyLocation", partyLocation)
        outState.putBoolean("permissionAsked", true)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        userLocation = savedInstanceState?.getParcelable("userLocation")
        partyLocation = savedInstanceState?.getParcelable("partyLocation")!!
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}