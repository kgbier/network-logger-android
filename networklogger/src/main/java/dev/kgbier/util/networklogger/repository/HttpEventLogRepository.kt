package dev.kgbier.util.networklogger.repository

import android.content.Context
import dev.kgbier.util.networklogger.data.HttpLoggingDatabase
import dev.kgbier.util.networklogger.model.HttpLogEvent
import dev.kgbier.util.networklogger.model.SparseHttpLogEvent

internal interface HttpEventLogRepository : HttpLoggingRepository {
    fun getEvents(): List<SparseHttpLogEvent>
    fun getEventById(id: String): HttpLogEvent
    fun clearAll()
}

internal class RealHttpEventLogRepository(private val context: Context) : HttpEventLogRepository {

    private val db by lazy { HttpLoggingDatabase(context) }

    override fun getEvents(): List<SparseHttpLogEvent> = db.reader.fetchTransactions()

    override fun getEventById(id: String): HttpLogEvent = db.reader.fetchEventById(id)

    override fun logRequest(
        transactionId: String,
        url: String,
        method: String,
        headers: List<Pair<String, String>>,
        body: String,
        timestamp: Long,
    ) = db.writer.logRequest(
        transactionId = transactionId,
        url = url,
        method = method,
        headers = headers.toBlock(),
        body = body,
        timestamp = timestamp,
    )

    override fun logResponse(
        transactionId: String,
        statusCode: Int,
        headers: List<Pair<String, String>>,
        body: String,
        timestamp: Long,
    ) = db.writer.logResponse(
        transactionId = transactionId,
        statusCode = statusCode,
        headers = headers.toBlock(),
        body = body,
        timestamp = timestamp,
    )

    override fun clearAll() = db.clearDatabase()

    private fun List<Pair<String, String>>.toBlock() =
        joinToString("\n") { (key, value) ->
            "$key: $value"
        }
}