package com.hrbabu.tracking.activity

import android.location.Geocoder
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hrbabu.tracking.BaseActivity
import com.hrbabu.tracking.databinding.ActivityAddClientBinding
import com.hrbabu.tracking.helpers.ActivityAddClientHelper
import com.hrbabu.tracking.helpers.ActivityAddClientHelper.Companion.ADD_NEW_CLIENT
import com.hrbabu.tracking.service.LocationLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.Locale

class AddNewClientActivity : BaseActivity() {
        private lateinit var binding: ActivityAddClientBinding
        //ActivityAddClientHelper
        private lateinit var activityAddClientHelper: ActivityAddClientHelper
        var clientName : String = ""
        var email : String = ""
        var phone : String = ""
        var address : String = ""
        var locationLat : String = "0L"
        var locationLong : String = "0L"
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = ActivityAddClientBinding.inflate(layoutInflater)
            setContentView(binding.root)

            binding.toolbar.title = "Add New Client"
            binding.toolbar.setTitleTextColor(resources.getColor(com.hrbabu.tracking.R.color.white))
            binding.toolbar.navigationIcon?.setTint(resources.getColor(com.hrbabu.tracking.R.color.white))
            // back click
            binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }


            activityAddClientHelper = ActivityAddClientHelper(this)
            activityAddClientHelper.init(this)
            LocationLiveData.getLastLocation()?.let {

                locationLat = it.latitude.toString()
                locationLong = it.longitude.toString()
                binding.etLocationLat.setText(locationLat)
                binding.etLocationLong.setText(locationLong)
//                val geocoder = android.location.Geocoder(this, Locale.getDefault())
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val geocoder = Geocoder(this@AddNewClientActivity)
                        val addresses = geocoder.getFromLocation(locationLat.toDouble(), locationLong.toDouble(), 1)

                        withContext(Dispatchers.Main) {
                            if (!addresses.isNullOrEmpty()) {
                                address = addresses[0].getAddressLine(0)
                                binding.etAddress.setText(address)
                            }else{
                                address = "Address not available"
                                binding.etAddress.setText(address)
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
                clientName = binding.etClientName.text.toString()
                email = binding.etEmail.text.toString()
                phone = binding.etPhone.text.toString()
                if(clientName.isEmpty()){
                    binding.etClientName.error = "Please enter client name"
                    return@setOnClickListener
                }
                if (email.isEmpty()) {
                    binding.etEmail.error = "Please enter email"
                    return@setOnClickListener
                }
                if (phone.isEmpty()) {
                    binding.etPhone.error = "Please enter phone number"
                    return@setOnClickListener
                }
                activityAddClientHelper.hitApi(ADD_NEW_CLIENT)
            }
        }

        override fun onSupportNavigateUp(): Boolean {
            finish()
            return true
        }
    }


