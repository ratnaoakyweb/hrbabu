package com.hrbabu.tracking.utils

import android.content.Context
import androidx.fragment.app.Fragment
import com.hrbabu.tracking.apiBase.ApiClient
import com.hrbabu.tracking.apiBase.apiList.ApiList

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
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

