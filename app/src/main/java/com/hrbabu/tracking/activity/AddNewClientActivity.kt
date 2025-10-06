package com.hrbabu.tracking.activity

import android.location.Geocoder
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hrbabu.tracking.BaseActivity
import com.hrbabu.tracking.databinding.ActivityAddClientBinding
import com.hrbabu.tracking.service.LocationLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.Locale

class AddNewClientActivity : BaseActivity() {
        private lateinit var binding: ActivityAddClientBinding

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = ActivityAddClientBinding.inflate(layoutInflater)
            setContentView(binding.root)

            LocationLiveData.getLastLocation()?.let {
                val lat = it.latitude
                val lng = it.longitude
                binding.etLocationLat.setText(lat.toString())
                binding.etLocationLong.setText(lng.toString())
                val geocoder = android.location.Geocoder(this, Locale.getDefault())
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val geocoder = Geocoder(this@AddNewClientActivity)
                        val addresses = geocoder.getFromLocation(lat, lng, 1)

                        withContext(Dispatchers.Main) {
                            if (!addresses.isNullOrEmpty()) {
                                binding.etAddress.setText(addresses[0].getAddressLine(0))
                            }
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                        withContext(Dispatchers.Main) {
                            binding.etAddress.setText("Address not available")
                        }
                    }
                }
            }


            binding.btnSaveClient.setOnClickListener {
                // Collect entered data
                val clientName = binding.etClientName.text.toString()
                val email = binding.etEmail.text.toString()
                val phone = binding.etPhone.text.toString()

                finish()
            }
        }

        override fun onSupportNavigateUp(): Boolean {
            finish()
            return true
        }
    }


