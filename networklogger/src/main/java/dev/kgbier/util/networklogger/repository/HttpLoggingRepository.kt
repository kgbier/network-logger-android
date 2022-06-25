package dev.kgbier.util.networklogger.repository

import android.content.Context
import dev.kgbier.util.networklogger.data.HttpLoggingDatabase
import dev.kgbier.util.networklogger.model.HttpLogEvent
import dev.kgbier.util.networklogger.model.SparseHttpLogEvent

interface HttpLoggingRepository {

    fun getEvents(): List<SparseHttpLogEvent>

    fun getEventById(id: String): HttpLogEvent

    fun logRequest(
        transactionId: String,
        url: String,
        method: String,
        headers: String,
        body: String,
        timestamp: Long,
    )

    fun logResponse(
        transactionId: String,
        statusCode: Int,
        headers: String,
        body: String,
        timestamp: Long,
    )

    fun clearAll()
}

class RealHttpLoggingRepository(private val context: Context) : HttpLoggingRepository {

    private val db by lazy { HttpLoggingDatabase(context) }

    override fun getEvents(): List<SparseHttpLogEvent> = db.reader.fetchTransactions()

    override fun getEventById(id: String): HttpLogEvent = db.reader.fetchEventById(id)

    override fun logRequest(
        transactionId: String,
        url: String,
        method: String,
        headers: String,
        body: String,
        timestamp: Long,
    ) = db.writer.logRequest(transactionId, url, method, headers, body, timestamp)

    override fun logResponse(
        transactionId: String,
        statusCode: Int,
        headers: String,
        body: String,
        timestamp: Long,
    ) = db.writer.logResponse(transactionId, statusCode, headers, body, timestamp)

    override fun clearAll() = db.clearDatabase()
}