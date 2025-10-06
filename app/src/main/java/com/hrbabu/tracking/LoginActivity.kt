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
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Patterns
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.hrbabu.tracking.databinding.ActivityLoginBinding
import com.hrbabu.tracking.helpers.LoginActivityHelper

class LoginActivity : BaseActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var loginActivityHelper: LoginActivityHelper

     var email: String = ""
     var password: String = ""

    companion object {
        private const val REQ_FOREGROUND_LOCATION = 100
        private const val REQ_BACKGROUND_LOCATION = 101
        private const val REQ_POST_NOTIFICATIONS = 102

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initHelper()

        // Create notification channel for location service
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "location_channel",
                "Location Tracking",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        // Login button → start permission check
        binding.btnLogin.setOnClickListener {
            // ✅ Check if all permissions and GPS are already okay
            if (hasAllPermissions()) {
                checkAndRequestNotificationPermission {
                    checkGpsAndProceed()
                }


            } else {
                // 🔄 Show the Enable Location dialog only if something is missing
                EnableLocationDialog {
                    checkAndRequestForegroundPermission()
                }.show(supportFragmentManager, "EnableLocationDialog")
            }
        }

        binding.tvForgotPassword.setOnClickListener {
            startActivity(Intent(this, ApplyLeaveActivity::class.java))
        }


        // If already logged in, skip to MainActivity
        if (getLoginResponse() != null) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
    }

    private fun initHelper() {
        loginActivityHelper = LoginActivityHelper(this)
        loginActivityHelper.init(thisActivity = this)
    }
    private fun hasAllPermissions(): Boolean {
        val fine = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarse = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val background = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
        } else true
        return fine && coarse && background
    }
    // -------------------------------------------------------------------------
    // Step 1 → Foreground Location
    // -------------------------------------------------------------------------
    private fun checkAndRequestForegroundPermission() {
        val fine = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarse = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)

        if (fine != PackageManager.PERMISSION_GRANTED || coarse != PackageManager.PERMISSION_GRANTED) {
            // Ask only if not permanently denied
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION) ||
                ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            ) {
                showPermissionSettingsDialog("Location permission is required to track attendance.")
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ),
                    REQ_FOREGROUND_LOCATION
                )
            }
        } else {
            // Foreground already granted
            checkAndRequestBackgroundPermission()
        }
    }

    // -------------------------------------------------------------------------
    // Step 2 → Background Location
    // -------------------------------------------------------------------------
    private fun checkAndRequestBackgroundPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val bg = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            if (bg != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                    showPermissionSettingsDialog("Please enable Background Location for full tracking.")
                } else {
                    showBackgroundPermissionDialog()
                }
            } else {
                checkGpsAndProceed()
            }
        } else {
            checkGpsAndProceed()
        }
    }

    // -------------------------------------------------------------------------
    // Step 3 → GPS Enable
    // -------------------------------------------------------------------------
    private fun checkGpsAndProceed() {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if (!gpsEnabled) {
            AlertDialog.Builder(this)
                .setTitle("Enable GPS")
                .setMessage("Please enable GPS to track attendance.")
                .setPositiveButton("Enable") { _, _ ->
                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
                .setNegativeButton("Cancel", null)
                .show()
        } else {
            proceedWithLogin()
        }
    }

    // -------------------------------------------------------------------------
    // Login API
    // -------------------------------------------------------------------------
    private fun proceedWithLogin() {
        email = binding.etEmail.text?.toString()?.trim() ?: ""
        password = binding.etPassword.text?.toString()?.trim() ?: ""

        when {
            email.isEmpty() -> Toast.makeText(this, "Enter Email", Toast.LENGTH_SHORT).show()
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> Toast.makeText(this, "Enter a valid Email", Toast.LENGTH_SHORT).show()
            password.isEmpty() -> Toast.makeText(this, "Enter Password", Toast.LENGTH_SHORT).show()
            else -> loginActivityHelper.hitApi(LoginActivityHelper.SIGNIN)
        }
    }

    // -------------------------------------------------------------------------
    // Permissions Result
    // -------------------------------------------------------------------------
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            REQ_FOREGROUND_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    checkAndRequestBackgroundPermission()
                } else {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                        // User selected “Don’t ask again”
                        showPermissionSettingsDialog("You have permanently denied location permission. Please enable it from settings.")
                    } else {
                        Toast.makeText(this, "Foreground location permission required!", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            REQ_BACKGROUND_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkGpsAndProceed()
                } else {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                        showPermissionSettingsDialog("You have permanently denied background location permission. Please enable it from settings.")
                    } else {
                        Toast.makeText(this, "Background location permission is required for full tracking.", Toast.LENGTH_LONG).show()
                        checkGpsAndProceed()
                    }
                }
            }
            REQ_POST_NOTIFICATIONS -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkGpsAndProceed()
                } else {
                    Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show()
                    checkGpsAndProceed() // Continue without notifications
                }
            }
        }
    }

    // -------------------------------------------------------------------------
    // Dialogs
    // -------------------------------------------------------------------------
    private fun showBackgroundPermissionDialog() {
        AlertDialog.Builder(this)
            .setTitle("Allow Background Location")
            .setMessage("To track attendance seamlessly, allow location access even when the app is closed.")
            .setPositiveButton("Allow") { _, _ ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                        REQ_BACKGROUND_LOCATION
                    )
                }
            }
            .setNegativeButton("Cancel") { _, _ -> checkGpsAndProceed() }
            .show()
    }

    private fun showPermissionSettingsDialog(message: String) {
        AlertDialog.Builder(this)
            .setTitle("Permission Required")
            .setMessage(message)
            .setPositiveButton("Open Settings") { _, _ ->
                val intent = Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", packageName, null)
                )
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        } else true
    }
    private fun checkAndRequestNotificationPermission(onGranted: () -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val notificationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            if (notificationPermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    REQ_POST_NOTIFICATIONS
                )
            } else {
                onGranted()
            }
        } else {
            onGranted()
        }
    }

}
