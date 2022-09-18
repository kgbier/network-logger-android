package dev.kgbier.util.networklogger.ktor

import android.content.Context
import dev.kgbier.util.networklogger.NetworkLoggerRepository
import io.ktor.client.plugins.*

typealias NetworkLoggerKtorPluginInstaller = HttpClientPlugin<Nothing, NetworkLoggerKtorPlugin>

fun NetworkLoggerKtorPluginInstaller(context: Context): NetworkLoggerKtorPluginInstaller =
    NetworkLoggerKtorPlugin.NetworkLoggerKtorPluginInstaller(NetworkLoggerRepository(context))
