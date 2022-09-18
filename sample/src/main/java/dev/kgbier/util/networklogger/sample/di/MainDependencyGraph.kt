package dev.kgbier.util.networklogger.sample.di

import android.content.Context
import android.util.Log
import dev.kgbier.util.networklogger.ktor.NetworkLoggerKtorPluginInstaller
import dev.kgbier.util.networklogger.okhttp.NetworkLoggerOkHttpInterceptor
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.logging.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

class MainDependencyGraph(private val context: Context) {

    // region Ktor

    private object KtorAndroidDebugLogger : Logger {
        override fun log(message: String) {
            Log.d("KTOR", message)
        }
    }

    private val networkLoggerKtorPluginInstaller by lazy {
        NetworkLoggerKtorPluginInstaller(context)
    }

    val ktorHttpClient by lazy {
        HttpClient(CIO) {
            install(Logging) {
                logger = KtorAndroidDebugLogger
                level = LogLevel.ALL
            }
            install(networkLoggerKtorPluginInstaller)
        }
    }

    // endregion

    // region OkHttp

    private val okHttpLoggingInterceptor: HttpLoggingInterceptor
        get() = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    private val networkLoggerOkHttpInterceptor by lazy {
        NetworkLoggerOkHttpInterceptor(context)
    }

    val okHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(okHttpLoggingInterceptor)
            .addInterceptor(networkLoggerOkHttpInterceptor)
            .build()
    }

    // endregion
}