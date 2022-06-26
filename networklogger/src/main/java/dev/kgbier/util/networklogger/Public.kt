package dev.kgbier.util.networklogger

import android.content.Context
import android.content.Intent
import dev.kgbier.util.networklogger.repository.NetworkLoggerRepository
import dev.kgbier.util.networklogger.repository.RealHttpEventLogRepository
import dev.kgbier.util.networklogger.view.NetworkLogActivity

fun NetworkLoggerRepository(context: Context): NetworkLoggerRepository =
    RealHttpEventLogRepository(context)

fun makeNetworkLoggerActivityIntent(context: Context): Intent =
    NetworkLogActivity.makeIntent(context)