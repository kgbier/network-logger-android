package dev.kgbier.util.networklogger

import android.content.Context
import android.content.Intent
import dev.kgbier.util.networklogger.repository.HttpLoggingRepository
import dev.kgbier.util.networklogger.repository.RealHttpEventLogRepository
import dev.kgbier.util.networklogger.view.NetworkLogActivity

fun HttpLoggingRepository(context: Context): HttpLoggingRepository =
    RealHttpEventLogRepository(context)

fun makeNetworkLogActivityIntent(context: Context): Intent =
    NetworkLogActivity.makeIntent(context)