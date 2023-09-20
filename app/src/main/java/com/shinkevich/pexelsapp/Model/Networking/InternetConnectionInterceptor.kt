package com.shinkevich.pexelsapp.Model.Networking

import android.content.Context
import android.net.ConnectivityManager
import okhttp3.Interceptor
import okhttp3.Response


class InternetConnectionInterceptor(val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.proceed(chain.request().newBuilder().build())
        if (!hasConnection(context) && request.cacheResponse() == null) {
            throw NoConnectionException()
        }

        return request
    }

}