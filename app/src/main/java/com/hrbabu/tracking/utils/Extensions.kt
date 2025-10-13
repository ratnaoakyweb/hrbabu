package com.hrbabu.tracking.utils

import android.content.Context
import android.util.Log
import androidx.fragment.app.Fragment
import com.hrbabu.tracking.apiBase.ApiClient
import com.hrbabu.tracking.apiBase.apiList.ApiList

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.Console
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

fun <T> sendApiRequest(observable: Observable<T>): Observable<T>? {
    return addToQueue(observable = observable)
}

fun <T> addToQueue(observable: Observable<T>): Observable<T>? {
    return observable.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
}

fun getApiClientAuth(context: Context): ApiList {
    return ApiClient().create(context)
}

fun convertUtcToLocal(utcTime: String, outputFormat: String = "hh:mm a"): String {
    return try {
        // Input format from API (UTC time)
        val utcFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS", Locale.getDefault())
        utcFormat.timeZone = TimeZone.getTimeZone("UTC")

        val date = utcFormat.parse(utcTime) ?: return ""

        // Output format in Local Timezone
        val localFormat = SimpleDateFormat(outputFormat, Locale.getDefault())
        localFormat.timeZone = TimeZone.getDefault()

        localFormat.format(date)
    } catch (e: Exception) {
        e.printStackTrace()
        ""
    }
}



 fun getCurrentUtcTime(): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    sdf.timeZone = TimeZone.getTimeZone("UTC")
    return sdf.format(Date())
}

// 1️⃣ Get today's UTC start time (00:00:00.000)
fun getTodayUtcStartTime(): String {
    val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)

    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    sdf.timeZone = TimeZone.getTimeZone("UTC")
    return sdf.format(calendar.time)
}

// 2️⃣ Get today's UTC end time (23:59:59.999)
fun getTodayUtcEndTime(): String {
    val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    calendar.set(Calendar.HOUR_OF_DAY, 23)
    calendar.set(Calendar.MINUTE, 59)
    calendar.set(Calendar.SECOND, 59)
    calendar.set(Calendar.MILLISECOND, 999)

    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    sdf.timeZone = TimeZone.getTimeZone("UTC")
    return sdf.format(calendar.time)
}


fun getFormattedTime(input : String): String{
    var formattedTime = ""
    try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS", Locale.getDefault())
        inputFormat.timeZone = TimeZone.getTimeZone("UTC") // or your desired time zone

        val date = inputFormat.parse(input)

// Step 2: Format the Date object into 12-hour time with AM/PM
        val outputFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        formattedTime = outputFormat.format(date)
    }catch (e: Exception){

        try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("UTC") // or your desired time zone

            val date = inputFormat.parse(input)

// Step 2: Format the Date object into 12-hour time with AM/PM
            val outputFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
            formattedTime = outputFormat.format(date)
        }catch (e: Exception){



        }

    }
    return formattedTime
}

// Get Fromated Date

fun getFormattedDate(input : String): String {
    var formattedDate = ""
    try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        inputFormat.timeZone = TimeZone.getTimeZone("UTC") // or your desired time zone

        val date = inputFormat.parse(input)
        val outputFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
        formattedDate = outputFormat.format(date)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return formattedDate
}

fun checkAppUpdate(serverVersion: String , appVersion: String): AppUpdateState{
    //log
    Log.e("Version Check--->","Server Version: $serverVersion, App Version: $appVersion")
    val serverParts = serverVersion.split(".").map { it.toInt() }
    val appParts = appVersion.split(".").map { it.toInt() }

    val (serverMajor, serverMinor, serverPatch) = serverParts + listOf(0, 0, 0).take(3 - serverParts.size)
    val (appMajor, appMinor, appPatch) = appParts + listOf(0, 0, 0).take(3 - appParts.size)

    if (serverMajor > appMajor ||
        (serverMajor == appMajor && serverMinor > appMinor) ||
        (serverMajor == appMajor && serverMinor == appMinor && serverPatch > appPatch)
    ) {
        // Update available
        println("Update available")
    } else {
        // No update needed
        println("No update needed")
    }

    if(serverMajor > appMajor){
        Log.e("Version Check--->","Force Update Needed")
        return AppUpdateState.FORCE_UPDATE
    }

    if(serverMajor == appMajor && serverMinor > appMinor){
        Log.e("Version Check--->","Optional Update Needed")
        return AppUpdateState.OPTIONAL_UPDATE
    }

    if(serverMajor == appMajor && serverMinor == appMinor && serverPatch > appPatch){
        Log.e("Version Check--->","Optional Update Needed")
        return AppUpdateState.OPTIONAL_UPDATE
    }

    Log.e("Version Check--->","No Update Needed")

    return AppUpdateState.NO_UPDATE

}



