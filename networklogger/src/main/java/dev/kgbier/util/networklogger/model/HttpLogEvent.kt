package dev.kgbier.util.networklogger.model

data class HttpLogEvent(
    val transaction: HttpLogTransaction,
    val request: HttpLogRequest,
    val response: HttpLogResponse?,
)