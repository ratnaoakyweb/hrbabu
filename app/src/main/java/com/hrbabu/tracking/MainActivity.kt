package com.hrbabu.tracking

import EnableLocationDialog
import android.Manifest
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.hrbabu.tracking.databinding.ActivityMainBinding
import com.hrbabu.tracking.service.LocationService

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    companion object {
        private const val REQ_FOREGROUND_LOCATION = 100
        private const val REQ_BACKGROUND_LOCATION = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Notification channel for Foreground Service
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "location_channel",
                "Location Tracking",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        // Show location enable dialog when login clicked
        binding.btnLogin.setOnClickListener {
            EnableLocationDialog {
                requestForegroundLocationPermission()
            }.show(supportFragmentManager, "EnableLocationDialog")
        }
    }

    /**
     * Step 1 → Request Foreground Location (FINE + COARSE)
     */
    private fun requestForegroundLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            REQ_FOREGROUND_LOCATION
        )
    }

    /**
     * Step 2 → Request Background Location separately (Android 10+)
     */
    private fun requestBackgroundLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                REQ_BACKGROUND_LOCATION
            )
        } else {
            // No need for background location on older Android versions
            checkGpsAndStartService()
        }
    }

    /**
     * Handle Permissions Result
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            REQ_FOREGROUND_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    // Foreground granted → proceed to background permission
                    showBackgroundPermissionDialog()
                } else {
                    Toast.makeText(this, "Foreground location permission required!", Toast.LENGTH_SHORT).show()
                }
            }

            REQ_BACKGROUND_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Now we are safe to start tracking
                    checkGpsAndStartService()
                } else {
                    Toast.makeText(
                        this,
                        "Background location is needed for full attendance tracking.",
                        Toast.LENGTH_LONG
                    ).show()
                    // Optionally: still allow only foreground tracking
                    checkGpsAndStartService()
                }
            }
        }
    }

    /**
     * Explain to user why Background location is needed
     */
    private fun showBackgroundPermissionDialog() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            AlertDialog.Builder(this)
                .setTitle("Allow Background Location")
                .setMessage("To track attendance seamlessly, allow location access even when the app is closed.")
                .setPositiveButton("Allow") { _, _ -> requestBackgroundLocationPermission() }
                .setNegativeButton("Cancel") { _, _ ->
                    // User declined background, but we can still start foreground tracking
                    checkGpsAndStartService()
                }
                .show()
        } else {
            // Older Android, no background needed
            checkGpsAndStartService()
        }
    }

    /**
     * Check GPS enabled before starting service
     */
    private fun checkGpsAndStartService() {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

        if (!gpsEnabled) {
            Toast.makeText(this, "Please enable GPS to track attendance.", Toast.LENGTH_LONG).show()
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        } else {
            startActivity(Intent(this, HomeActivity::class.java))
            //startLocationService()
        }
    }
}

