package com.hrbabu.tracking.utils

import android.content.Context
import androidx.fragment.app.Fragment
import com.hrbabu.tracking.apiBase.ApiClient
import com.hrbabu.tracking.apiBase.apiList.ApiList

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
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
