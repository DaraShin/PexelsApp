package com.shinkevich.pexelsapp.Model.Networking

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkService {

    private val cacheSize: Long = 20 * 1024 * 1024
    private val apiKey = "9WzlvrESoWHNPtKLSMys6Xh3mLzV6ybqOrKiR54uH7mWo3EodN3w8T2k"

    @Singleton
    @Provides
    fun provideOkHttp(@ApplicationContext appContext: Context): OkHttpClient {
        return OkHttpClient.Builder().apply {
            addInterceptor { chain ->
                val original: Request = chain.request()
                val request: Request = original.newBuilder()
                    .header("Authorization", apiKey)
                    .build()
                chain.proceed(request)
            }
        }
            .cache(Cache(appContext.cacheDir, cacheSize))
            .addInterceptor(NetworkCacheInterceptor(appContext))
            .addInterceptor(InternetConnectionInterceptor(appContext))
            .build()
    }

    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.pexels.com/v1/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    fun provideApiClient(retrofit: Retrofit): PexelsApi {
        return retrofit.create(PexelsApi::class.java)
    }
}