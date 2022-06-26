package dev.kgbier.util.networklogger.sample.di

import android.content.Context
import dev.kgbier.util.networklogger.NetworkLoggerRepository
import dev.kgbier.util.networklogger.okhttp.NetworkLoggerOkHttpInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor


class MainDependencyGraph(val context: Context) {

    val httpLoggingRepository by lazy {
        NetworkLoggerRepository(context)
    }

    val networkLoggerOkHttpInterceptor by lazy {
        NetworkLoggerOkHttpInterceptor(httpLoggingRepository)
    }

    val okHttpLoggingInterceptor: HttpLoggingInterceptor
        get() = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    val okHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(okHttpLoggingInterceptor)
            .addInterceptor(networkLoggerOkHttpInterceptor)
            .build()
    }
}