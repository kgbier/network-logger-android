package dev.kgbier.util.networklogger.model

internal data class HttpLogRequest(
    val transactionId: String,
    val headers: String,
    val body: String,
    val timestamp: Long,
)