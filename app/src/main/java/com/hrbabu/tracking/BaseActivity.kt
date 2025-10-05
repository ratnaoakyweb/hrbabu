package com.hrbabu.tracking

import android.graphics.Bitmap
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.hrbabu.tracking.database.PunchEvent
import com.hrbabu.tracking.database.PunchViewModel
import java.io.File
import java.io.FileOutputStream

open class BaseActivity : AppCompatActivity()  {
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


}