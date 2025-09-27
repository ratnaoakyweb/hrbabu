package com.hrbabu.tracking

import EnableLocationDialog
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import com.hrbabu.tracking.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.btnLogin.setOnClickListener {


                EnableLocationDialog {
                    turnOnLocation()
                }.show(supportFragmentManager, "EnableLocationDialog")
            }
        }

    private fun turnOnLocation() {
        // Here request location permission or enable GPS
        // Example:
        // ActivityCompat.requestPermissions(
        //     this,
        //     arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
        //     1001
        // )
    }
    }



