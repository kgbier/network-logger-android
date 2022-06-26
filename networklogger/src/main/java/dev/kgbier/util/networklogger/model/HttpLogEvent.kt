package dev.kgbier.util.networklogger.model

internal data class HttpLogEvent(
    val transaction: HttpLogTransaction,
    val request: HttpLogRequest,
    val response: HttpLogResponse?,
)