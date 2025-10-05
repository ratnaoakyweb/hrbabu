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
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.hrbabu.tracking.databinding.ActivityMainBinding
import com.hrbabu.tracking.helpers.MainActivityHelper
import com.hrbabu.tracking.service.LocationService

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var mainActivityHelper: MainActivityHelper
    var email: String = ""
    var password: String = ""


    companion object {
        private const val REQ_FOREGROUND_LOCATION = 100
        private const val REQ_BACKGROUND_LOCATION = 101
    }

    fun initHelper() {
        mainActivityHelper = MainActivityHelper(this)
        mainActivityHelper.init(thisActivity = this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initHelper()

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

        binding.tvForgotPassword.setOnClickListener {
            startActivity(Intent(this,ApplyLeaveActivity::class.java))
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }


    private fun proceedWithLogin() {
        email = binding.etEmail.text?.toString()?.trim() ?: ""
        password = binding.etPassword.text?.toString()?.trim() ?: ""

        when {
            email.isEmpty() -> {
                Toast.makeText(this, "Enter Email", Toast.LENGTH_SHORT).show()
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                Toast.makeText(this, "Enter a valid Email", Toast.LENGTH_SHORT).show()
            }
            password.isEmpty() -> {
                Toast.makeText(this, "Enter Password", Toast.LENGTH_SHORT).show()
            }
            else -> {

                mainActivityHelper.hitApi(MainActivityHelper.SIGNIN)
            }
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
                    // Foreground granted → explain why we need background
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
