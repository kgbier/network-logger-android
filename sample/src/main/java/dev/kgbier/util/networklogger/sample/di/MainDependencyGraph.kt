package dev.kgbier.util.networklogger.sample.di

import android.content.Context
import dev.kgbier.util.networklogger.okhttp.NetworkLoggerOkHttpInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

class MainDependencyGraph(val context: Context) {

    val okHttpLoggingInterceptor: HttpLoggingInterceptor
        get() = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    val networkLoggerOkHttpInterceptor by lazy {
        NetworkLoggerOkHttpInterceptor(context)
    }

    val okHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(okHttpLoggingInterceptor)
            .addInterceptor(networkLoggerOkHttpInterceptor)
            .build()
    }
}