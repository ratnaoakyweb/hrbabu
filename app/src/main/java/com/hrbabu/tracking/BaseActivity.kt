package com.hrbabu.tracking

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.color.MaterialColors
import com.hrbabu.tracking.database.PunchEvent
import com.hrbabu.tracking.database.PunchViewModel
import com.hrbabu.tracking.request_response.login.LoginResponse
import com.hrbabu.tracking.request_response.profile.ProfileResponse
import com.hrbabu.tracking.service.LocationService
import com.hrbabu.tracking.utils.PrefKeys
import com.hrbabu.tracking.utils.PrefUtil
import java.io.File
import java.io.FileOutputStream

open class BaseActivity : AppCompatActivity()  {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    fun startService()
    {
//        val intent = Intent(this, LocationService::class.java)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            startForegroundService(intent)
//        } else {
//            startService(intent)
//        }
    }
//    private val vm: PunchViewModel by viewModels()
//    fun onCapturedPunch(bitmap: Bitmap, lat: Double?, lng: Double?, address: String?) {
//        // save bitmap to cache first (no storage permission)
//        val path = saveImageToCache(bitmap) // implement as you already have
//        val event = PunchEvent(
//            latitude = lat,
//            longitude = lng,
//            address = address,
//            imagePath = path,
//            eventType = "PunchIn",
//            createdAt = System.currentTimeMillis(),
//            isSynced = false
//        )
//
//        vm.saveEvent(event) { id ->
//            runOnUiThread {
//                Toast.makeText(this, "Punch saved locally (id=$id)", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//    private fun saveImageToCache(bitmap: Bitmap): String {
//        val file = File(cacheDir, "punch_${System.currentTimeMillis()}.jpg")
//        FileOutputStream(file).use { out ->
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
//        }
//        return file.absolutePath
//    }
    fun getLoginResponse() : LoginResponse? {
        val gson = com.google.gson.Gson()
        val loginJson = PrefUtil.Init(this).getString(PrefKeys.loginResponse)
        return gson.fromJson(loginJson, LoginResponse::class.java)
    }

    fun getProfileResponse(): ProfileResponse?{
        val gson = com.google.gson.Gson()
        val profileJson = PrefUtil.Init(this).getString(PrefKeys.profileResponse)
        return gson.fromJson(profileJson, ProfileResponse::class.java)
    }
    fun getAppVersionName(context: Context): String? {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            "0.0.0"
        }
    }



}