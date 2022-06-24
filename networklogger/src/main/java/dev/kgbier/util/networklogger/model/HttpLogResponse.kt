package dev.kgbier.util.networklogger.model

data class HttpLogResponse(
    val transactionId: String,
    val statusCode: Int,
    val headers: String,
    val body: String,
    val timestamp: Long,
)