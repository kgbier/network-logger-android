package dev.kgbier.util.networklogger.model

data class HttpLogRequest(
    val transactionId: String,
    val headers: String,
    val body: String,
    val timestamp: Long,
)