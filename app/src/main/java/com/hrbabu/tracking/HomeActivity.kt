package com.hrbabu.tracking

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.hrbabu.tracking.databinding.ActivityHomeBinding
import com.hrbabu.tracking.service.LocationService

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

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
//        binding.recyclerTasks.adapter = TaskAdapter(tasks)

        // Punch-in toggle
        binding.switchPunchIn.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.tvStatus.text = "Working"
                binding.tvStatus.setTextColor(Color.parseColor("#4CAF50"))
                startLocationService()
            } else {
                binding.tvStatus.text = "Not Working"
                binding.tvStatus.setTextColor(Color.parseColor("#FF5252"))
                stopLocationService()
            }
        }
    }

    /**
     * Start Foreground Location Service
     */
    private fun startLocationService() {
        val intent = Intent(this, LocationService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }

    /**
     * Stop Foreground Location Service
     */
    private fun stopLocationService() {
        val intent = Intent(this, LocationService::class.java)
        stopService(intent)
    }
}
data class Task(
    val title: String,
    val subTitle: String,
    val priority: String,
    val progress: String,
    val duration: String
)
