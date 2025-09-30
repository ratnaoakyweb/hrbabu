package com.hrbabu.tracking.utils

import android.content.Context
import androidx.fragment.app.Fragment
import com.hrbabu.tracking.apiBase.ApiClient
import com.hrbabu.tracking.apiBase.apiList.ApiList

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

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


