package com.shinkevich.pexelsapp.Model.Networking

import android.content.Context
import android.net.ConnectivityManager
import okhttp3.Interceptor
import okhttp3.Response


class InternetConnectionInterceptor(val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        if (!hasConnection()) {
            throw NoConnectionException()
        }
        return chain.proceed(chain.request().newBuilder().build())
    }

    private fun hasConnection() : Boolean{
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo  = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

}