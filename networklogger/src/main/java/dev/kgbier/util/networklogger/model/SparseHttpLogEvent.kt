package dev.kgbier.util.networklogger.model

internal data class SparseHttpLogEvent(
    val uuid: String,
    val url: String,
    val method: String,
    val statusCode: Int?,
    val requestTimestamp: Long,
)