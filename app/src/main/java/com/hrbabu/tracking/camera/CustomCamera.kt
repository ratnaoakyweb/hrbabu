package com.hrbabu.tracking.camera

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hrbabu.tracking.databinding.ActivityCustomCameraBinding
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.PictureResult
import com.otaliastudios.cameraview.controls.Facing
import com.otaliastudios.cameraview.size.AspectRatio
import com.otaliastudios.cameraview.size.SizeSelector
import com.otaliastudios.cameraview.size.SizeSelectors
import java.io.File
import java.io.FileOutputStream

class CustomCamera : AppCompatActivity() {
    private lateinit var binding: ActivityCustomCameraBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCustomCameraBinding.inflate(layoutInflater)

        setContentView(binding.root)


        binding.camera.setLifecycleOwner(this);
     val width = SizeSelectors.maxWidth(800);
    val height = SizeSelectors.minHeight(2000);
    val dimensions = SizeSelectors.and(width); // Matches sizes bigger than 1000x2000.
//    val ratio = SizeSelectors.aspectRatio(AspectRatio.of(1, 1), 0); // Matches 1:1 sizes.

    val result = SizeSelectors.or(
    SizeSelectors.and( dimensions), // Try to match both constraints
     // If none is found, at least try to match the aspect ratio
    SizeSelectors.biggest() // If none is found, take the biggest
    )
    binding.camera.setPictureSize(result);

        binding.camera.addCameraListener(object : CameraListener() {
            override fun onPictureTaken(result: PictureResult) {
                super.onPictureTaken(result)
                // Handle the picture taken event here
                // For example, save the image or display it in an ImageView

                result.toBitmap { bitmap ->
                    // Do something with the bitmap
                    // For example, display it in an ImageView
                    binding.capturedImage.setImageBitmap(bitmap)
                   val url = saveImageToCache(bitmap!!)
//                    f (result.resultCode == RESULT_OK) {
//                        val bitmap = result.data?.extras?.get("data") as? Bitmap
                    if (bitmap != null) {
                        val intent = Intent().apply {
                            putExtra("url", url)
                        }
                        setResult(RESULT_OK, intent)
                        finish()
                    }

                }

            }
        })

        binding.captureButton.setOnClickListener {
            binding.camera.takePicture()
        }
        binding.flipButton.setOnClickListener {

                    binding.camera.facing =
                        if (binding.camera.facing  == Facing.BACK) Facing.FRONT else Facing.BACK

        }

    }

    private fun saveImageToCache(bitmap: Bitmap): String {
        val file = File(cacheDir, "punch_${System.currentTimeMillis()}.jpg")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
        }
        return file.absolutePath
    }
}