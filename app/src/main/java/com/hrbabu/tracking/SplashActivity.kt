package com.hrbabu.tracking


import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat.animate

open class SplashActivity : AppCompatActivity()  {

    private lateinit var binding: com.hrbabu.tracking.databinding.ActivitySplashBinding


    override fun onCreate(savedInstanceState: Bundle?) {

        //full screen
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )


        super.onCreate(savedInstanceState)
        binding = com.hrbabu.tracking.databinding.ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Time for 2 seconds

       animate(binding.root).alpha(1f).setDuration(2000).withEndAction {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

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


}