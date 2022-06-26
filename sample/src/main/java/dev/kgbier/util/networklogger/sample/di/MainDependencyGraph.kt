package dev.kgbier.util.networklogger.sample.di

import android.content.Context
import dev.kgbier.util.networklogger.HttpLoggingRepository
import dev.kgbier.util.networklogger.sample.NetworkLoggingOkHttpInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor


class MainDependencyGraph(val context: Context) {

    val httpLoggingRepository by lazy {
        HttpLoggingRepository(context)
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