package com.shinkevich.pexelsapp.Model.Networking

import android.content.Context
import android.net.ConnectivityManager
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.util.concurrent.TimeUnit

class NetworkCacheInterceptor(val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request: Request = chain.request()
        request = if (hasConnection(context)) {
            request.newBuilder().header("Cache-Control", "public, max-age=" + 3600).build()
        } else {
            request.newBuilder()
                .header("Cache-Control", "public, only-if-cached, max-stale=" + 3600).build()
        }
        return chain.proceed(request)
    }

}