package com.example.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import android.provider.Settings
import com.google.android.gms.location.*
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity() {

    lateinit var mFusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

    }

    override fun onResume() {
        super.onResume()
        checkLocatioPermission()
    }

    fun checkLocatioPermission() = runWithPermissions(  Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION) {
        if (isLocationEnabled()) {
            mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                var location: Location? = task.result
                if (location == null) {
                    requestNewLocationData()
                } else {
                    getLocationData(location)
                }
            }
        } else {
            Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show()
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        }
    }

    private fun getLocationData(location: Location) {
        val geocoder : Geocoder = Geocoder(this, Locale.getDefault())
        var addressList : List<Address> = ArrayList<Address>()

        addressList = geocoder.getFromLocation(location.latitude,location.longitude,1)

        if(addressList.size > 0){
            val address = addressList.get(0).getAddressLine(0)
            val city = addressList.get(0).locality
            val state = addressList.get(0).adminArea
            val country = addressList.get(0).countryName
            val postalCode = addressList.get(0).postalCode
            val knownName = addressList.get(0).featureName // Only if available else return NULL

            tvAddress.setText("$address, \n\n$city, $state, $country, $postalCode, \n\n\n$knownName")
        }

        tvLong.setText(location.latitude.toString())
        tvLong.setText(location.latitude.toString())

        tvLat.text = location.latitude.toString()
        tvLong.text = location.longitude.toString()
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        var mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient!!.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper())
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            var mLastLocation: Location = locationResult.lastLocation
            getLocationData(mLastLocation)
        }
    }

    private fun isLocationEnabled(): Boolean {
        var locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }
}