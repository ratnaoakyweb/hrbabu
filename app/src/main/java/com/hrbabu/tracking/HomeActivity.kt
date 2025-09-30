package com.hrbabu.tracking

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.hrbabu.tracking.database.AppDatabase
import com.hrbabu.tracking.database.PunchEvent
import com.hrbabu.tracking.databinding.ActivityHomeBinding
import com.hrbabu.tracking.service.LocationService
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.*
import androidx.core.graphics.toColorInt

class HomeActivity : BaseActivity() {

    private lateinit var binding: ActivityHomeBinding

    companion object {
        private const val REQUEST_CHECK_SETTINGS = 3001
    }

    private var pendingLocation: android.location.Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val tasks = listOf(
            Task("Payment Gateway Integration", "E-Commerce Platform", "Moderate", "50%", "45 min"),
            Task("Contact Form Integration", "E-Commerce Platform", "Moderate", "50%", "45 min"),
            Task("Grid Integration", "E-Commerce Platform", "Moderate", "50%", "45 min")
        )

        binding.recyclerTasks.layoutManager = LinearLayoutManager(this)
        // binding.recyclerTasks.adapter = TaskAdapter(tasks)

        // Punch-in toggle
        binding.switchPunchIn.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.tvStatus.text = "Working"
                binding.tvStatus.setTextColor("#4CAF50".toColorInt())

                captureAccurateLocation { location ->
                    if (location != null) {
                        pendingLocation = location
                        openCameraLauncher.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
                    } else {
//                        Toast.makeText(this, "Unable to get GPS location", Toast.LENGTH_LONG).show()
//                        binding.switchPunchIn.isChecked = false

                        pendingLocation =  android.location.Location(LocationManager.GPS_PROVIDER).apply {
                            latitude = 0.0
                            longitude = 0.0
                        }
                        openCameraLauncher.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))

                    }
                }
                startLocationService()
            } else {
                binding.tvStatus.text = "Not Working"
                binding.tvStatus.setTextColor(Color.parseColor("#FF5252"))
                stopLocationService()
            }
        }
    }

    /**
     * Foreground location service
     */
    private fun startLocationService() {
        val intent = Intent(this, LocationService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }

    private fun stopLocationService() {
        val intent = Intent(this, LocationService::class.java)
        stopService(intent)
    }

    /**
     * GPS capture with system dialog if disabled
     */
    private fun captureAccurateLocation(callback: (android.location.Location?) -> Unit) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 2000L
        )
            .setWaitForAccurateLocation(true)
            .setMaxUpdates(1)
            .build()

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .setAlwaysShow(true)

        val settingsClient = LocationServices.getSettingsClient(this)
        val task = settingsClient.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                callback(null)
                return@addOnSuccessListener
            }

            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                object : LocationCallback() {
                    override fun onLocationResult(result: LocationResult) {
                        fusedLocationClient.removeLocationUpdates(this)
                        callback(result.lastLocation)
                    }
                },
                Looper.getMainLooper()
            )
        }

        task.addOnFailureListener { e ->
            if (e is ResolvableApiException) {
                try {
                    e.startResolutionForResult(this, REQUEST_CHECK_SETTINGS)
                } catch (_: Exception) {
                    callback(null)
                }
            } else {
                callback(null)
            }
        }
    }

    /**
     * Camera launcher
     */
    private val openCameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val bitmap = result.data?.extras?.get("data") as? Bitmap
                if (bitmap != null && pendingLocation != null) {
                    val imagePath = saveImageToCache(bitmap)

                    // Reverse geocode
                    val geocoder = android.location.Geocoder(this, Locale.getDefault())
                    val addresses =
                        geocoder.getFromLocation(pendingLocation!!.latitude, pendingLocation!!.longitude, 1)
                    val address = addresses?.firstOrNull()?.getAddressLine(0) ?: ""

                    // Save to DB
                    val punchEvent = PunchEvent(
                        latitude = pendingLocation!!.latitude,
                        longitude = pendingLocation!!.longitude,
                        address = address,
                        imagePath = imagePath,
                        eventType = "PunchIn",
                        createdAt = System.currentTimeMillis(),
                        isSynced = false
                    )

                    lifecycleScope.launch {
                        AppDatabase.getDatabase(this@HomeActivity).punchEventDao()
                            .insert(punchEvent)
                        Toast.makeText(this@HomeActivity, "Punch-In saved locally", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                binding.switchPunchIn.isChecked = false
                Toast.makeText(this, "Camera cancelled", Toast.LENGTH_SHORT).show()
            }
        }

    /**
     * Save bitmap to cache
     */
    private fun saveImageToCache(bitmap: Bitmap): String {
        val file = File(cacheDir, "punch_${System.currentTimeMillis()}.jpg")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
        }
        return file.absolutePath
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == RESULT_OK) {
                captureAccurateLocation { location ->
                    if (location != null) {
                        pendingLocation = location
                        openCameraLauncher.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
                    }
                }
            } else {
                binding.switchPunchIn.isChecked = false
                Toast.makeText(this, "GPS is required for Punch-In", Toast.LENGTH_LONG).show()
            }
        }
    }
}

data class Task(
    val title: String,
    val subTitle: String,
    val priority: String,
    val progress: String,
    val duration: String
)
