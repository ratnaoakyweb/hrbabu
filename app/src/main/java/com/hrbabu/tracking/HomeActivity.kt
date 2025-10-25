package com.hrbabu.tracking

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.MediaStore
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.hrbabu.tracking.databinding.ActivityHomeBinding
import com.hrbabu.tracking.service.LocationService
import java.io.File
import java.io.FileOutputStream
import java.util.*
import androidx.core.graphics.toColorInt
import androidx.core.view.GravityCompat
import com.hrbabu.tracking.activity.ActivityClientList
import com.hrbabu.tracking.activity.ActivityLeaveList
import com.hrbabu.tracking.activity.ActivityProfile
import com.hrbabu.tracking.activity.ActivityVisitList
import com.hrbabu.tracking.activity.AddVisitActivity
import com.hrbabu.tracking.adapter.TaskAdapter
import com.hrbabu.tracking.camera.CustomCamera
import com.hrbabu.tracking.helpers.HomeActivityHelper
import com.hrbabu.tracking.request_response.history.HistoryResponse
import com.hrbabu.tracking.request_response.history.RcItem
import com.hrbabu.tracking.service.LocationLiveData
import com.hrbabu.tracking.utils.ButtonState
import com.hrbabu.tracking.utils.CameraState
import com.hrbabu.tracking.utils.PrefUtil
import com.hrbabu.tracking.utils.getFormattedTime

class HomeActivity : BaseActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var homeActivityHelper: HomeActivityHelper
    companion object {
        private const val REQUEST_CHECK_SETTINGS = 3001
        private const val CAMERA_PERMISSION_CODE = 100
    }

    var pendingLocation: android.location.Location? = null
    var filePath = ""

    private lateinit var cameraCurrentState : CameraState

    private lateinit var currentState: ButtonState
    var selectedClientId = -1;
    var selectedVisitId = -1;
    var selectedVisitCheckInId = -1;
    var selectedVisitCheckInTime = "";

    private fun checkCameraPermission() : Boolean {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_CODE
            )
            return false
        } else {
            return true
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setButtonState(ButtonState.INACTIVE)
        homeActivityHelper = HomeActivityHelper(this)
        homeActivityHelper.init(thisActivity = this)

        val drawerLayout = binding.drawerLayout

        findViewById<LinearLayout>(R.id.llUser).setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            startActivity(Intent(this, ActivityProfile::class.java))
        }

//        findViewById<TextView>(R.id.tvSettings).setOnClickListener {
//            drawerLayout.closeDrawer(GravityCompat.START)
//            Toast.makeText(this, "Settings clicked", Toast.LENGTH_SHORT).show()
//        }


        findViewById<LinearLayout>(R.id.layoutLeave).setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)

            startActivity(Intent(this, ActivityLeaveList::class.java))
        }
        findViewById<LinearLayout>(R.id.layoutClient).setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)

            startActivity(Intent(this, ActivityClientList::class.java))
        }
        findViewById<LinearLayout>(R.id.layoutVisit).setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)

            startActivity(Intent(this, AddVisitActivity::class.java))
        }

        binding.navigationView.tvVersion.text = "V: ${getAppVersionName(this)}"
        findViewById<LinearLayout>(R.id.layoutLogout).setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)

            showLogoutDialog()
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
            if(currentState == ButtonState.CHECK_IN){
                startVisit.launch(Intent(this, ActivityVisitList::class.java))

            }else if (currentState == ButtonState.CHECK_OUT){
                cameraCurrentState = CameraState.CHECK_OUT
                if(pendingLocation!=null){
                    openCameraLauncher.launch(Intent(this@HomeActivity, CustomCamera::class.java))
                }else{
                    captureAccurateLocation { location ->
                        if (location != null) {
                            pendingLocation = location
                            openCameraLauncher.launch(Intent(this@HomeActivity, CustomCamera::class.java))
                        }
                        else {
                            Toast.makeText(this, "Unable to get GPS location", Toast.LENGTH_LONG).show()
                        }

                    }
                }
            }
//            if(binding.switchPunchIn.isChecked){
//                startVisit.launch(Intent(this, ActivityVisitList::class.java))
////                startActivity(Intent(this, ActivityVisitList::class.java))
////                if(currentState == ButtonState.CHECK_IN){
////                    val intent = (Intent(this, ActivityClientList::class.java))
////                    clientLauncher.launch(intent)
////
////                }else{
////                        cameraCurrentState = CameraState.CHECK_OUT
////                        captureAccurateLocation { location ->
////                            if (location != null) {
////                                pendingLocation = location
////                                openCameraLauncher.launch(Intent(this@HomeActivity, CustomCamera::class.java))
////                            }
////                            else {
////                                    Toast.makeText(this, "Unable to get GPS location", Toast.LENGTH_LONG).show()
////                            }
////
////                        }
////                }
//
//            }else{
//                Toast.makeText(this, "You are Inactive now", Toast.LENGTH_SHORT).show()
//
//            }
        }
        binding.llLocation.visibility= View.VISIBLE
        getCurrentLocation()


        //set current date and day

        binding.tvDate.text = android.text.format.DateFormat.format("dd MMM, EEEE", Date())


        setUpSideBar()

    }

    fun showUpdateDialog(message: String, isMandatory: Boolean) {
        val appPackageName = packageName
        val updateIntent = Intent(Intent.ACTION_VIEW).apply {
            data = try {
                Uri.parse("market://details?id=$appPackageName")
            } catch (e: ActivityNotFoundException) {
                Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
            }
        }

        val builder = AlertDialog.Builder(this).apply {
            setTitle("Update Required")
            setMessage(
                if (isMandatory) {
                    "A new version of this app is available and must be installed to continue.\n\n$message"
                } else {
                    "A new version of this app is available.\n\n$message"
                }
            )
            setCancelable(!isMandatory)

            setPositiveButton("Update Now") { dialog, _ ->
                dialog.dismiss()
                try {
                    startActivity(updateIntent)
                } catch (ex: Exception) {
                    Toast.makeText(
                        this@HomeActivity,
                        "Unable to open Play Store.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                if (isMandatory) {
                    finishAffinity() // Close the app completely
                }
            }

            if (!isMandatory) {
                setNegativeButton("Remind Me Later") { dialog, _ ->
                    dialog.dismiss()
                }
            }
        }

        val dialog = builder.create()
        dialog.show()

        // Optional: Customize button colors for a cleaner look
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(ContextCompat.getColor(this, R.color.gray))
    }


    fun setUpSideBar(){
        val profileResponse = getProfileResponse()
        findViewById<TextView>(R.id.tvUserName).text = profileResponse?.res?.fullName ?: "User"
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
                        openCameraLauncher.launch(Intent(this@HomeActivity, CustomCamera::class.java))
                    }
                    else {
//                        Toast.makeText(this, "Unable to get GPS location", Toast.LENGTH_LONG).show()
//                        binding.switchPunchIn.isChecked = false

//                        pendingLocation =  android.location.Location(LocationManager.GPS_PROVIDER).apply {
//                            latitude = 0.0
//                            longitude = 0.0
//                        }
//                        openCameraLauncher.launch(Intent(this@HomeActivity, CustomCamera::class.java))

                    }

                }
            }
        }
    }

    fun setupToggel( isPunchIn : Boolean ,  isVisitCheckIn: Boolean){
        // Punch-in toggle
        if(selectedVisitCheckInTime.isNotEmpty() && isVisitCheckIn){
            binding.tvClockIn.text = "Check-In Time : ${getFormattedTime(selectedVisitCheckInTime)}"
            binding.tvClockIn.visibility = View.VISIBLE
        }
        binding.switchPunchIn.isChecked = isPunchIn
        if(isVisitCheckIn){
        setButtonState(ButtonState.CHECK_OUT)
        }
        if(!isPunchIn){
            setButtonState(ButtonState.INACTIVE)
        }else{
            if(isVisitCheckIn){
                setButtonState(ButtonState.CHECK_OUT)
            }else{
                setButtonState(ButtonState.CHECK_IN)
            }
        }
//        binding.switchPunchIn.apply {
//            // Disable built-in toggle behavior
//            setOnTouchListener { _, _ -> true } // consume touch events (prevents toggle)
//
//            // Add manual click listener
//            setOnClickListener {
//
//                // handle click here (no change in state unless you do it by code)
//            }
//        }
        binding.switchPunchIn.isClickable = false
        binding.switchPunchIn.isFocusable = false
        binding.llSwitch1.setOnClickListener {
            binding.llSwitch.performClick()
        }
        binding.llSwitch.setOnClickListener {

            if(binding.llLocation.visibility == View.VISIBLE){
                Toast.makeText(this, "Please wait location is being captured", Toast.LENGTH_LONG).show()

                return@setOnClickListener
            }
            
          
            captureAccurateLocation { location ->

                if(location == null){
                    Toast.makeText(this, "Unable to get GPS location", Toast.LENGTH_LONG).show()

                    return@captureAccurateLocation
                }
            }
            if(!checkCameraPermission()){
              return@setOnClickListener
            }
            if(!binding.switchPunchIn.isChecked){

                captureAccurateLocation { location ->
                    if (location != null) {
                        pendingLocation = location
                        cameraCurrentState = CameraState.PUNCH_IN
                        openCameraLauncher.launch(Intent(this@HomeActivity, CustomCamera::class.java))
                    } else {

                        pendingLocation =  android.location.Location(LocationManager.GPS_PROVIDER).apply {
                            latitude = 0.0
                            longitude = 0.0
                        }
                        openCameraLauncher.launch(Intent(this@HomeActivity, CustomCamera::class.java))

                    }
                }
            }
            else{
                if(currentState == ButtonState.CHECK_OUT){
                    Toast.makeText(this, "Please check-out from visit first", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
                cameraCurrentState = CameraState.PUNCH_OUT
                captureAccurateLocation { location ->
                    if (location != null) {
                        pendingLocation = location
                        openCameraLauncher.launch(Intent(this@HomeActivity, CustomCamera::class.java))
                    } else {

                        pendingLocation =  android.location.Location(LocationManager.GPS_PROVIDER).apply {
                            latitude = 0.0
                            longitude = 0.0
                        }
                        openCameraLauncher.launch(Intent(this@HomeActivity, CustomCamera::class.java))

                    }

                }
            }


        }

    }

    fun setHistoryData(response: HistoryResponse?){
        if(response!=null){
            if((response.rc?.size ?: 0) > 0){
                binding.clNoData.visibility=View.GONE

                binding.recyclerTasks.adapter = TaskAdapter(response.rc as List<RcItem>)
            }else{
                binding.clNoData.visibility=View.VISIBLE

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
             Priority.PRIORITY_HIGH_ACCURACY, 10000L * 6 // 10 seconds, reasonable for most apps
         )
             .setWaitForAccurateLocation(false)      // accept approximate first
             .setMinUpdateIntervalMillis(10000L * 6 )     // minimum 10 seconds between updates
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
               // Toast.makeText(this, "Location permission not granted", Toast.LENGTH_LONG).show()
                askFormLocationPermission()
                return@addOnSuccessListener
            }

            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                object : LocationCallback() {
                    override fun onLocationResult(result: LocationResult) {
//                        fusedLocationClient.removeLocationUpdates(this)
                        binding.llLocation.visibility= View.GONE
//                        Toast.makeText(this@HomeActivity, "Location captured", Toast.LENGTH_SHORT).show()
                        LocationLiveData.updateLocation(result.lastLocation!!)
//                        callback(result.lastLocation)
                        pendingLocation=result.lastLocation

//                        binding.llLocation.visibility= View.VISIBLE
//
//                        binding.tvLocation.text= result.lastLocation?.latitude.toString() + ", " + result.lastLocation?.longitude.toString() + System.currentTimeMillis()
//                        binding.tvLocation.text= System.currentTimeMillis().toString()+ "  -- " + pendingLocation?.isFromMockProvider
                    }

                    //error
                    override fun onLocationAvailability(availability: LocationAvailability) {
                        super.onLocationAvailability(availability)
                        if (!availability.isLocationAvailable) {
                            binding.llLocation.visibility= View.VISIBLE
                            binding.tvLocation.text= "Waiting for location..."
                        }
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
//        LocationLiveData.getLastLocation()?.let {
//            callback(it)
//        }
        callback(pendingLocation)
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
                val url = result.data?.getStringExtra("url")
                if (url != null && pendingLocation != null) {
                    val imagePath = url
                    filePath = imagePath
                    // Reverse geocode
                    val geocoder = android.location.Geocoder(this, Locale.getDefault())
                    val addresses =
                        geocoder.getFromLocation(pendingLocation!!.latitude, pendingLocation!!.longitude, 1)
                    val address = addresses?.firstOrNull()?.getAddressLine(0) ?: ""

                    // Save to DB
//                    val punchEvent = PunchEvent(
//                        latitude = pendingLocation!!.latitude,
//                        longitude = pendingLocation!!.longitude,
//                        address = address,
//                        imagePath = imagePath,
//                        eventType = "PunchIn",
//                        createdAt = System.currentTimeMillis(),
//                        isSynced = false
//                    )

//                    lifecycleScope.launch {
//                        AppDatabase.getDatabase(this@HomeActivity).punchEventDao()
//                            .insert(punchEvent)
//                        Toast.makeText(this@HomeActivity, "Punch-In saved locally", Toast.LENGTH_SHORT).show()
//                    }
                    if(cameraCurrentState == CameraState.PUNCH_IN){
                        homeActivityHelper.hitApi(HomeActivityHelper.KEY_PunchIn)
                    }else if (cameraCurrentState == CameraState.PUNCH_OUT){
                        homeActivityHelper.hitApi(HomeActivityHelper.PunchOut)
                    }else if(cameraCurrentState == CameraState.CHECK_IN){
                        homeActivityHelper.hitApi(HomeActivityHelper.CheckIn)
                    }else if (cameraCurrentState == CameraState.CHECK_OUT){
                        homeActivityHelper.hitApi(HomeActivityHelper.CheckOut)
                    }


                }
            } else {
//                binding.switchPunchIn.isChecked =  !binding.switchPunchIn.isChecked
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
                        openCameraLauncher.launch(Intent(this@HomeActivity, CustomCamera::class.java))
                    }
                }
            } else {
                binding.switchPunchIn.isChecked = false
                Toast.makeText(this, "GPS is required for Punch-In", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun showLogoutDialog() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to log out?")
            .setPositiveButton("Yes") { dialog, _ ->
                dialog.dismiss()
                logout()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
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


    fun setToggelState(isChecked: Boolean){
        if(isChecked){
                        binding.tvStatus.text = "Working"
                binding.tvStatus.setTextColor("#4CAF50".toColorInt())
                binding.switchPunchIn.isChecked=true
        }else{
                binding.tvStatus.text = "Not Working"
                binding.tvStatus.setTextColor(Color.parseColor("#FF5252"))
                binding.switchPunchIn.isChecked=false
        }
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

    // Define at the top of your Activity (as a property)
    private val startVisit = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            val clientId = data?.getIntExtra("client_id",-1)
            val visitId = data?.getIntExtra("visit_id",-1)

            //Toast.makeText(this, "Visit Added: $visitName", Toast.LENGTH_SHORT).show()
            if(currentState == ButtonState.CHECK_IN){
//                    val intent = (Intent(this, ActivityClientList::class.java))
//                    clientLauncher.launch(intent)
                selectedClientId = clientId?.toInt() ?: -1
                selectedVisitId = visitId?.toInt() ?: -1
                if(selectedClientId != -1 && selectedVisitId!= -1){
                    cameraCurrentState = CameraState.CHECK_IN
                    if(pendingLocation!=null){
                        openCameraLauncher.launch(Intent(this@HomeActivity, CustomCamera::class.java))
                    }
//                    captureAccurateLocation { location ->
//                        if (location != null) {
//                            pendingLocation = location
//                            openCameraLauncher.launch(Intent(this@HomeActivity, CustomCamera::class.java))
//                        }
//                        else {
////                        Toast.makeText(this, "Unable to get GPS location", Toast.LENGTH_LONG).show()
////                        binding.switchPunchIn.isChecked = false
//
////                        pendingLocation =  android.location.Location(LocationManager.GPS_PROVIDER).apply {
////                            latitude = 0.0
////                            longitude = 0.0
////                        }
////                        openCameraLauncher.launch(Intent(this@HomeActivity, CustomCamera::class.java))
//
//                        }
//
//                    }
                }

                }else{
                        cameraCurrentState = CameraState.CHECK_OUT
                        captureAccurateLocation { location ->
                            if (location != null) {
                                pendingLocation = location
                                openCameraLauncher.launch(Intent(this@HomeActivity, CustomCamera::class.java))
                            }
                            else {
                                    Toast.makeText(this, "Unable to get GPS location", Toast.LENGTH_LONG).show()
                            }

                        }
                }
        }
    }

    fun askFormLocationPermission(){
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1001
            )
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
            1001 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted
                    getCurrentLocation()
                } else {
                    // Permission denied show dialog

                    AlertDialog.Builder(this)
                        .setTitle("Permission Required")
                        .setMessage("Location permission is required for this app to function. Please grant the permission in app settings.")
                        .setPositiveButton("OK") { dialog, _ ->
                            dialog.dismiss()
                            getCurrentLocation()
                        }
                        .show()

                }
            }

            CAMERA_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted
                    Toast.makeText(this, "Camera permission granted", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}





