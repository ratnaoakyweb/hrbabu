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
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.core.view.GravityCompat
import com.hrbabu.tracking.activity.ActivityClientList
import com.hrbabu.tracking.activity.ActivityVisitList
import com.hrbabu.tracking.adapter.TaskAdapter
import com.hrbabu.tracking.databinding.ItemTaskBinding
import com.hrbabu.tracking.helpers.HomeActivityHelper
import com.hrbabu.tracking.request_response.history.HistoryResponse
import com.hrbabu.tracking.request_response.history.RcItem
import com.hrbabu.tracking.service.LocationLiveData
import com.hrbabu.tracking.utils.ButtonState
import com.hrbabu.tracking.utils.CameraState
import com.hrbabu.tracking.utils.PrefUtil

class HomeActivity : BaseActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var homeActivityHelper: HomeActivityHelper
    companion object {
        private const val REQUEST_CHECK_SETTINGS = 3001
    }

    var pendingLocation: android.location.Location? = null
    var filePath = ""

    private lateinit var cameraCurrentState : CameraState

    private lateinit var currentState: ButtonState
    var selectedClientId = -1;
    var selectedVisitId = -1;
    var selectedVisitCheckInId = -1;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setButtonState(ButtonState.INACTIVE)
        homeActivityHelper = HomeActivityHelper(this)
        homeActivityHelper.init(thisActivity = this)
        binding.navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
//                R.id.nav_home -> { /* Navigate */ }
                R.id.nav_profile -> { /* Show profile */ }
                R.id.nav_logout -> { logout()}
            }
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        binding.ivMenu.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }






        binding.recyclerTasks.layoutManager = LinearLayoutManager(this)
//         binding.recyclerTasks.adapter = TaskAdapter(tasks)



        homeActivityHelper.hitApi(HomeActivityHelper.GetToggel)


        binding.btnClockOut.setOnClickListener {
            if(binding.llLocation.visibility == View.VISIBLE){
                Toast.makeText(this, "Please wait location is being captured", Toast.LENGTH_LONG).show()

               return@setOnClickListener
            }
            if(binding.switchPunchIn.isChecked){
                startActivity(Intent(this, ActivityVisitList::class.java))
//                if(currentState == ButtonState.CHECK_IN){
//                    val intent = (Intent(this, ActivityClientList::class.java))
//                    clientLauncher.launch(intent)
//
//                }else{
//                        cameraCurrentState = CameraState.CHECK_OUT
//                        captureAccurateLocation { location ->
//                            if (location != null) {
//                                pendingLocation = location
//                                openCameraLauncher.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
//                            }
//                            else {
//                                    Toast.makeText(this, "Unable to get GPS location", Toast.LENGTH_LONG).show()
//                            }
//
//                        }
//                }

            }else{
                Toast.makeText(this, "You are Inactive now", Toast.LENGTH_SHORT).show()

            }
        }
        binding.llLocation.visibility= View.VISIBLE
        getCurrentLocation()


        //set current date and day

        binding.tvDate.text = android.text.format.DateFormat.format("dd MMM, EEEE", Date())
    }

    private val clientLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            val selectedClient = data?.getIntExtra("clientId",-1)
            selectedClientId = selectedClient ?: -1
            if(selectedClientId != -1){
                cameraCurrentState = CameraState.CHECK_IN
                captureAccurateLocation { location ->
                    if (location != null) {
                        pendingLocation = location
                        openCameraLauncher.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
                    }
                    else {
//                        Toast.makeText(this, "Unable to get GPS location", Toast.LENGTH_LONG).show()
//                        binding.switchPunchIn.isChecked = false

//                        pendingLocation =  android.location.Location(LocationManager.GPS_PROVIDER).apply {
//                            latitude = 0.0
//                            longitude = 0.0
//                        }
//                        openCameraLauncher.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))

                    }

                }
            }
        }
    }

    fun setupToggel( isPunchIn : Boolean ,  isVisitCheckIn: Boolean){
        // Punch-in toggle
        binding.switchPunchIn.isChecked = isPunchIn
        if(isVisitCheckIn){
        setButtonState(ButtonState.CHECK_OUT)
        }
        if(!isPunchIn){
            setButtonState(ButtonState.INACTIVE)
        }
        binding.switchPunchIn.setOnCheckedChangeListener { _, isChecked ->


            if(binding.llLocation.visibility == View.VISIBLE){
                Toast.makeText(this, "Please wait location is being captured", Toast.LENGTH_LONG).show()
                binding.switchPunchIn.isChecked = !isChecked
                return@setOnCheckedChangeListener
            }

            captureAccurateLocation { location ->

                if(location == null){
                    Toast.makeText(this, "Unable to get GPS location", Toast.LENGTH_LONG).show()
                    binding.switchPunchIn.isChecked = !isChecked
                    return@captureAccurateLocation
                }
            }
            if (isChecked) {
                binding.tvStatus.text = "Working"
                binding.tvStatus.setTextColor("#4CAF50".toColorInt())

                captureAccurateLocation { location ->
                    if (location != null) {
                        pendingLocation = location
                        cameraCurrentState = CameraState.PUNCH_IN
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
                cameraCurrentState = CameraState.PUNCH_OUT
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
                    stopLocationService()
                }
            }
        }
    }

    fun setHistoryData(response: HistoryResponse?){
        if(response!=null){
            if((response.rc?.size ?: 0) > 0){
                if((response.rc?.get(0)?.activityType ?: "") == "Punch In"){
                    binding.switchPunchIn.isChecked=true
                    startLocationService()

                }else if((response.rc?.get(0)?.activityType ?: "") == "Punch Out"){
                    binding.switchPunchIn.isChecked=false
                }

                binding.recyclerTasks.adapter = TaskAdapter(response.rc as List<RcItem>)
            }
        }
    }

    /**
     * Foreground location service
     */
    private fun startLocationService() {
//        val intent = Intent(this, LocationService::class.java)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            startForegroundService(intent)
//        } else {
//            startService(intent)
//        }
    }

    private fun stopLocationService() {
        val intent = Intent(this, LocationService::class.java)
        stopService(intent)
    }


     fun getCurrentLocation(){

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
//
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

                return@addOnSuccessListener
            }

            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                object : LocationCallback() {
                    override fun onLocationResult(result: LocationResult) {
//                        fusedLocationClient.removeLocationUpdates(this)
                        binding.llLocation.visibility= View.GONE
                        Toast.makeText(this@HomeActivity, "Location captured", Toast.LENGTH_SHORT).show()
//                        callback(result.lastLocation)
                        pendingLocation=result.lastLocation
                    }
                },
                Looper.getMainLooper()
            )
        }

        task.addOnFailureListener { e ->
            homeActivityHelper.hideProgressDialog()
            binding.llLocation.visibility= View.VISIBLE
            binding.tvLocation.text= "Location Not captured"
//            Toast.makeText(this@HomeActivity, "Location Not captured", Toast.LENGTH_SHORT).show()
            if (e is ResolvableApiException) {
                try {
                    e.startResolutionForResult(this, REQUEST_CHECK_SETTINGS)
                } catch (_: Exception) {
//                    callback(null)
                }
            } else {
//                callback(null)
            }
        }
    }

    /**
     * GPS capture with system dialog if disabled
     */
    private fun captureAccurateLocation(callback: (android.location.Location?) -> Unit) {
        LocationLiveData.getLastLocation()?.let {
            callback(it)
        }
//            val time = SimpleDateFormat("hh:mm:ss a", Locale.getDefault()).format(Date())
//            binding.tvLocation.text = "Lat: $lat, Lng: $lng at $time"
//        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
//
//        val locationRequest = LocationRequest.Builder(
//            Priority.PRIORITY_HIGH_ACCURACY, 2000L
//        )
//            .setWaitForAccurateLocation(true)
//            .setMaxUpdates(1)
//            .build()
//
//        val builder = LocationSettingsRequest.Builder()
//            .addLocationRequest(locationRequest)
//            .setAlwaysShow(true)
//
//        val settingsClient = LocationServices.getSettingsClient(this)
//        val task = settingsClient.checkLocationSettings(builder.build())
//
//        task.addOnSuccessListener {
//            if (ActivityCompat.checkSelfPermission(
//                    this,
//                    Manifest.permission.ACCESS_FINE_LOCATION
//                ) != PackageManager.PERMISSION_GRANTED
//            ) {
//                callback(null)
//                return@addOnSuccessListener
//            }
//
//            fusedLocationClient.requestLocationUpdates(
//                locationRequest,
//                object : LocationCallback() {
//                    override fun onLocationResult(result: LocationResult) {
//                        fusedLocationClient.removeLocationUpdates(this)
//                        callback(result.lastLocation)
//                    }
//                },
//                Looper.getMainLooper()
//            )
//        }
//
//        task.addOnFailureListener { e ->
//            if (e is ResolvableApiException) {
//                try {
//                    e.startResolutionForResult(this, REQUEST_CHECK_SETTINGS)
//                } catch (_: Exception) {
//                    callback(null)
//                }
//            } else {
//                callback(null)
//            }
//        }
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
                    filePath = imagePath
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
                    if(cameraCurrentState == CameraState.PUNCH_IN){
                        homeActivityHelper.hitApi(HomeActivityHelper.PunchIn)
                    }else if (cameraCurrentState == CameraState.PUNCH_OUT){
                        homeActivityHelper.hitApi(HomeActivityHelper.PunchOut)
                    }else if(cameraCurrentState == CameraState.CHECK_IN){
                        homeActivityHelper.hitApi(HomeActivityHelper.CheckIn)
                    }else if (cameraCurrentState == CameraState.CHECK_OUT){
                        homeActivityHelper.hitApi(HomeActivityHelper.CheckOut)
                    }


                }
            } else {
                binding.switchPunchIn.isChecked =  !binding.switchPunchIn.isChecked
                //Toast.makeText(this, "Camera cancelled", Toast.LENGTH_SHORT).show()
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

    fun logout() {
        // Clear user session data (e.g., SharedPreferences)
        PrefUtil.Init(this).clearALl()

        // Navigate to LoginActivity
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    fun setButtonState(state: ButtonState) {
        currentState = state
        when (state) {
            ButtonState.CHECK_IN -> {
                binding.btnClockOut.text = "Check In"
                binding.btnClockOut.setBackgroundColor(Color.parseColor("#4CAF50")) // Green
                binding.btnClockOut.setTextColor(Color.WHITE)
            }
            ButtonState.INACTIVE -> {
                binding.btnClockOut.text = "Inactive"
                binding.btnClockOut.setBackgroundColor(Color.parseColor("#BDBDBD")) // Gray
                binding.btnClockOut.setTextColor(Color.WHITE)
            }
            ButtonState.CHECK_OUT -> {
                binding.btnClockOut.text = "Check Out"
                binding.btnClockOut.setBackgroundColor(Color.parseColor("#FF5252")) // Red
                binding.btnClockOut.setTextColor(Color.WHITE)
            }
        }
    }


}





