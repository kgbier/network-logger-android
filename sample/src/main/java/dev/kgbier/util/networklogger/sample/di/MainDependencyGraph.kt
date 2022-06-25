package dev.kgbier.util.networklogger.sample.di

import android.content.Context
import dev.kgbier.util.networklogger.repository.RealHttpLoggingRepository
import dev.kgbier.util.networklogger.sample.NetworkLoggingOkHttpInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor


class MainDependencyGraph(val context: Context) {

    val httpLoggingRepository by lazy {
        RealHttpLoggingRepository(context)
    }

    val networkLoggingOkHttpInterceptor by lazy {
        NetworkLoggingOkHttpInterceptor(httpLoggingRepository)
    }

    val okHttpLoggingInterceptor: HttpLoggingInterceptor
        get() = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    val okHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(okHttpLoggingInterceptor)
            .addInterceptor(networkLoggingOkHttpInterceptor)
            .build()
    }
}