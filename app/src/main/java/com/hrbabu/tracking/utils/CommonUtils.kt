package com.hrbabu.tracking.utils

import android.util.Log
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
    }
}