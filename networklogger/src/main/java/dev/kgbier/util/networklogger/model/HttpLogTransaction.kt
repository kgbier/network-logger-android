package dev.kgbier.util.networklogger.model

internal data class HttpLogTransaction(
    val uuid: String,
    val url: String,
    val method: String,
)