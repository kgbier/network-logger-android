package dev.kgbier.util.networklogger.repository

interface HttpLoggingRepository {

    fun logRequest(
        transactionId: String,
        url: String,
        method: String,
        headers: List<Pair<String, String>>,
        body: String,
        timestamp: Long,
    )

    fun logResponse(
        transactionId: String,
        statusCode: Int,
        headers: List<Pair<String, String>>,
        body: String,
        timestamp: Long,
    )
}