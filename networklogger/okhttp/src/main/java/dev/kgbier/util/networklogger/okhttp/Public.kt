package dev.kgbier.util.networklogger.okhttp

import android.content.Context
import dev.kgbier.util.networklogger.NetworkLoggerRepository

fun NetworkLoggerOkHttpInterceptor(context: Context) =
    NetworkLoggerOkHttpInterceptor(NetworkLoggerRepository(context))