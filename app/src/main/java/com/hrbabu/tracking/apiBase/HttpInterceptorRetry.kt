package com.social.pe.apibase

import android.content.Context
import com.hrbabu.tracking.utils.PrefKeys
import com.hrbabu.tracking.utils.PrefUtil
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class HttpInterceptorRetry internal constructor(var context: Context) : Interceptor {
    var isApiDone = false

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var request: Request = chain.request()


        val builder: Request.Builder = request.newBuilder()
        builder.header("Accept", "application/json")

        val token = PrefUtil.Init(context).getString(PrefKeys.token)

        request = builder.build()
        val response: Response = chain.proceed(request)

        return response
    }

    private fun setAuthHeader(builder: Request.Builder, token: String?) {
        if (token != null)
            builder.header("Authorization", String.format("Bearer %s", token))
    }

    private fun logout(): Int {
        //logout your user
        return 0
    }
}