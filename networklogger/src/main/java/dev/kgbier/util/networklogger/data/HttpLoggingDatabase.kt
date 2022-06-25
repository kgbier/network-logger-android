package dev.kgbier.util.networklogger.data

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.core.database.getIntOrNull
import androidx.core.database.getLongOrNull
import androidx.core.database.getStringOrNull
import dev.kgbier.util.networklogger.util.fromBase64
import dev.kgbier.util.networklogger.util.toBase64
import dev.kgbier.util.networklogger.model.HttpLogEvent
import dev.kgbier.util.networklogger.model.HttpLogRequest
import dev.kgbier.util.networklogger.model.HttpLogResponse
import dev.kgbier.util.networklogger.model.HttpLogTransaction
import dev.kgbier.util.networklogger.model.SparseHttpLogEvent

class HttpLoggingDatabase(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        private const val DB_NAME = "dev_http_log.db"
        private const val DB_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE transactions (
                uuid TEXT PRIMARY KEY NOT NULL,
                url TEXT NOT NULL,
                method TEXT NOT NULL
            )
            """
        )

        db.execSQL(
            """
            CREATE TABLE requests (
                transactionId TEXT PRIMARY KEY NOT NULL,
                headers TEXT NOT NULL,
                body TEXT NOT NULL,
                timestamp INTEGER NOT NULL
            )
            """
        )

        db.execSQL(
            """
            CREATE TABLE responses (
                transactionId TEXT PRIMARY KEY NOT NULL,
                status_code INTEGER NOT NULL,
                headers TEXT NOT NULL,
                body TEXT NOT NULL,
                timestamp INTEGER NOT NULL
            )
            """
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // These migration (read: deletion) scripts should be additive only, there's no need to remove any DROP TABLE commands
        db.execSQL("DROP TABLE IF EXISTS transactions")
        db.execSQL("DROP TABLE IF EXISTS requests")
        db.execSQL("DROP TABLE IF EXISTS responses")

        onCreate(db)
    }

    fun clearDatabase() {
        writableDatabase.execSQL("DELETE FROM transactions")
        writableDatabase.execSQL("DELETE FROM requests")
        writableDatabase.execSQL("DELETE FROM responses")
    }

    private class EventProjection(private val cursor: Cursor) {
        fun project() = HttpLogEvent(
            projectTransaction(),
            projectRequest(),
            projectResponse(),
        )

        fun projectTransaction() = HttpLogTransaction(uuid, url, method)

        fun projectRequest() = HttpLogRequest(
            uuid,
            requestHeaders,
            requestBody,
            requestTimestamp,
        )

        fun projectResponse() = statusCode?.let {
            HttpLogResponse(
                uuid,
                requireNotNull(statusCode),
                requireNotNull(responseHeaders),
                requireNotNull(responseBody),
                requireNotNull(responseTimestamp),
            )
        }

        val uuid: String
            get() = cursor["uuid"].getString()
        val url: String
            get() = cursor["url"].getString()
        val method: String
            get() = cursor["method"].getString()
        val requestHeaders: String
            get() = cursor["requestHeaders"].getString().fromBase64()
        val requestBody: String
            get() = cursor["requestBody"].getString().fromBase64()
        val requestTimestamp: Long
            get() = cursor["requestTimestamp"].getLong()
        val statusCode: Int?
            get() = cursor["status_code"].getIntOrNull()
        val responseHeaders: String?
            get() = cursor["responseHeaders"].getStringOrNull()?.fromBase64()
        val responseBody: String?
            get() = cursor["responseBody"].getStringOrNull()?.fromBase64()
        val responseTimestamp: Long?
            get() = cursor["responseTimestamp"].getLongOrNull()

        operator fun Cursor.get(column: String) = ColumnCursor(getColumnIndex(column))

        inner class ColumnCursor(private val id: Int) {
            fun getInt(): Int = cursor.getInt(id)
            fun getIntOrNull(): Int? = cursor.getIntOrNull(id)
            fun getLong(): Long = cursor.getLong(id)
            fun getLongOrNull(): Long? = cursor.getLongOrNull(id)
            fun getString(): String = cursor.getString(id)
            fun getStringOrNull(): String? = cursor.getStringOrNull(id)
        }
    }

    val reader get() = Reader()

    inner class Reader {
        fun fetchTransactions(): List<SparseHttpLogEvent> =
            readableDatabase.rawQuery(
                """
                SELECT 
                    uuid, url, method,
                    requests.timestamp as requestTimestamp,
                    status_code
                FROM transactions
                INNER JOIN requests ON transactions.uuid == requests.transactionId
                LEFT JOIN responses ON transactions.uuid == responses.transactionId
                ORDER BY requestTimestamp DESC
                """, null
            ).use {
                val eventProjection = EventProjection(it)
                val events = mutableListOf<SparseHttpLogEvent>()
                while (it.moveToNext()) {
                    events.add(
                        SparseHttpLogEvent(
                            eventProjection.uuid,
                            eventProjection.url,
                            eventProjection.method,
                            eventProjection.statusCode,
                            eventProjection.requestTimestamp,
                        )
                    )
                }
                events
            }

        fun fetchEventById(id: String): HttpLogEvent =
            readableDatabase.rawQuery(
                """
                SELECT 
                    uuid, url, method,
                    requests.headers as requestHeaders,
                    requests.body as requestBody,
                    requests.timestamp as requestTimestamp,
                    status_code,
                    responses.headers as responseHeaders,
                    responses.body as responseBody,
                    responses.timestamp as responseTimestamp
                FROM transactions
                INNER JOIN requests ON transactions.uuid == requests.transactionId
                LEFT JOIN responses ON transactions.uuid == responses.transactionId
                WHERE uuid == '$id'
                """, null
            ).use {
                it.moveToFirst()
                val eventProjection = EventProjection(it)
                eventProjection.project()
            }
    }

    val writer get() = Writer()

    inner class Writer {
        fun logRequest(
            transactionId: String,
            url: String,
            method: String,
            headers: String,
            body: String,
            timestamp: Long,
        ) {
            writableDatabase.execSQL(
                """
                INSERT INTO transactions ( uuid, url, method ) 
                VALUES ( '$transactionId', '$url', '$method' )
                """
            )
            writableDatabase.execSQL(
                """
                INSERT INTO requests ( transactionId, headers, body, timestamp ) 
                VALUES ( '$transactionId', '${headers.toBase64()}', '${body.toBase64()}', $timestamp )
                """
            )
        }

        fun logResponse(
            transactionId: String,
            statusCode: Int,
            headers: String,
            body: String,
            timestamp: Long,
        ) = writableDatabase.execSQL(
            """
            INSERT INTO responses ( transactionId, status_code, headers, body, timestamp ) 
            VALUES ( '$transactionId', $statusCode, '${headers.toBase64()}', '${body.toBase64()}', $timestamp )
            """
        )
    }
}
