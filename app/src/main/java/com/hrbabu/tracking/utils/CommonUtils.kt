package com.hrbabu.tracking.utils

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.hrbabu.tracking.BuildConfig

class CommonUtils {
    companion object {
        fun showLog(tag: String?, message: String) {
            try {
                if (BuildConfig.DEBUG) {
                    Log.e(tag, "" + message)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun showToast(context: Context, message: String) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
}