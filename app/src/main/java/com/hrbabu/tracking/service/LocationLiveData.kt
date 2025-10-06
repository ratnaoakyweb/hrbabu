package com.hrbabu.tracking.service

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

object LocationLiveData {
    private val _location = MutableLiveData<Location>()
    val location: LiveData<Location> get() = _location

    private var lastLocation: Location? = null

    fun updateLocation(location: Location) {
        lastLocation = location
        _location.postValue(location)
    }

    fun getLastLocation(): Location? = lastLocation
}
