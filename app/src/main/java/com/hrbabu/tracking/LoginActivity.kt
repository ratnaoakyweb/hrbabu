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
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.hrbabu.tracking.databinding.ActivityLoginBinding
import com.hrbabu.tracking.helpers.LoginActivityHelper

class LoginActivity : BaseActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var helper: LoginActivityHelper

    var email: String = ""
    var password: String = ""

    companion object {
        private const val REQ_FOREGROUND_LOCATION = 100
        private const val REQ_BACKGROUND_LOCATION = 101
        private const val REQ_POST_NOTIFICATIONS = 102

        // 👇 Add your flag here
        private const val ENABLE_ADVANCED_PERMISSIONS = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initHelper()
        createNotificationChannel()

//        binding.tvForgotPassword.visibility = View.GONE

        // Main login click
        binding.btnLogin.setOnClickListener {
            if (hasAllPermissions()) {
                // 👇 Only check notifications if flag is enabled
                if (ENABLE_ADVANCED_PERMISSIONS) {
                    checkAndRequestNotificationPermission { checkGpsAndProceed() }
                } else {
                    checkGpsAndProceed()
                }
            } else {
                EnableLocationDialog {
                    checkAndRequestForegroundPermission()
                }.show(supportFragmentManager, "EnableLocationDialog")
            }
        }

        // Auto redirect if already logged in
        getLoginResponse()?.let {
            startService()
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }


        val versionName = getAppVersionName(this)

        binding.tvAppVersion.text = "App Version: $versionName"

    }

    // -------------------------------------------------------------------------
    // Initialization
    // -------------------------------------------------------------------------
    private fun initHelper() {
        helper = LoginActivityHelper(this).apply {
            init(this@LoginActivity)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "location_channel",
                "Location Tracking",
                NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java)
                ?.createNotificationChannel(channel)
        }
    }

    // -------------------------------------------------------------------------
    // Permissions
    // -------------------------------------------------------------------------
    private fun hasAllPermissions(): Boolean {
        val fine = hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        val coarse = hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION)

        // 👇 Only check background permission if flag is enabled
        val background = if (ENABLE_ADVANCED_PERMISSIONS && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            hasPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        } else true

        return fine && coarse && background
    }

    private fun hasPermission(permission: String): Boolean =
        ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

    private fun checkAndRequestForegroundPermission() {
        val fine = hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        val coarse = hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION)

        if (!fine || !coarse) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                REQ_FOREGROUND_LOCATION
            )
        } else {
            // 👇 Only check background if enabled
            if (ENABLE_ADVANCED_PERMISSIONS) {
                checkAndRequestBackgroundPermission()
            } else {
                checkGpsAndProceed()
            }
        }
    }

    private fun checkAndRequestBackgroundPermission() {
        if (ENABLE_ADVANCED_PERMISSIONS &&
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
            !hasPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        ) {
            showBackgroundPermissionDialog()
        } else {
            checkGpsAndProceed()
        }
    }

    private fun checkAndRequestNotificationPermission(onGranted: () -> Unit) {
        if (ENABLE_ADVANCED_PERMISSIONS &&
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            !hasPermission(Manifest.permission.POST_NOTIFICATIONS)
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                REQ_POST_NOTIFICATIONS
            )
        } else {
            onGranted()
        }
    }

    // -------------------------------------------------------------------------
    // GPS
    // -------------------------------------------------------------------------
    private fun checkGpsAndProceed() {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if (!gpsEnabled) {
            showGpsEnableDialog()
        } else {
            proceedWithLogin()
        }
    }

    // -------------------------------------------------------------------------
    // Login
    // -------------------------------------------------------------------------
    private fun proceedWithLogin() {
        email = binding.etEmail.text?.toString()?.trim().orEmpty()
        password = binding.etPassword.text?.toString()?.trim().orEmpty()

        when {
            email.isEmpty() -> showToast("Enter Email")
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> showToast("Enter a valid Email")
            password.isEmpty() -> showToast("Enter Password")
            else -> helper.hitApi(LoginActivityHelper.SIGNIN)
        }
    }

    // -------------------------------------------------------------------------
    // Permission Result
    // -------------------------------------------------------------------------
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            REQ_FOREGROUND_LOCATION -> {
                if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    if (ENABLE_ADVANCED_PERMISSIONS) {
                        checkAndRequestBackgroundPermission()
                    } else {
                        checkGpsAndProceed()
                    }
                } else handleDenied("Foreground location permission required!")
            }

            REQ_BACKGROUND_LOCATION -> {
                if (grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED) {
                    checkGpsAndProceed()
                } else handleDenied("Background location permission is required for full tracking.")
            }

            REQ_POST_NOTIFICATIONS -> checkGpsAndProceed()
        }
    }

    // -------------------------------------------------------------------------
    // Dialogs & Utilities
    // -------------------------------------------------------------------------
    private fun showGpsEnableDialog() {
        AlertDialog.Builder(this)
            .setTitle("Enable GPS")
            .setMessage("Please enable GPS to track attendance.")
            .setPositiveButton("Enable") { _, _ ->
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

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

    private fun handleDenied(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}
