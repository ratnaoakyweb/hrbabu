package com.hrbabu.tracking.service

import android.Manifest
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.gms.location.*
import java.text.SimpleDateFormat
import java.util.*

class LocationService : Service() {

    private lateinit var fusedLocationProvider: FusedLocationProviderClient
    private var notificationBuilder: NotificationCompat.Builder? = null
    private val notificationId = 1
    private var currentLocation : Location? = null
    override fun onCreate() {
        super.onCreate()
        fusedLocationProvider = LocationServices.getFusedLocationProviderClient(this)
        createNotification()
        requestLocationUpdates()
    }

    private fun requestLocationUpdates() {
        val request = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 5000L
        ).build()

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) return

        fusedLocationProvider.requestLocationUpdates(
            request,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            for (location in result.locations) {
                val lat = location.latitude
                val lng = location.longitude
                val time = SimpleDateFormat("hh:mm:ss a", Locale.getDefault()).format(Date())
                Log.d("LocationService", "Lat: $lat, Lng: $lng at $time")
                currentLocation = location
                updateNotification(lat, lng, time)
            }
        }
    }

    private fun createNotification() {
        notificationBuilder = NotificationCompat.Builder(this, "location_channel")
            .setContentTitle("Tracking Attendance")
            .setContentText("Getting location…")
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setOngoing(true)
            .setOnlyAlertOnce(true) // prevents repeated sound/vibration on updates

        startForeground(notificationId, notificationBuilder!!.build())
    }

    private fun updateNotification(lat: Double, lng: Double, time: String) {
        notificationBuilder?.setContentText(
            "Last: $lat, $lng at $time"
        )
        LocationLiveData.updateLocation(currentLocation!!) // update LiveData

        NotificationManagerCompat.from(this).notify(notificationId, notificationBuilder!!.build())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotification()
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
