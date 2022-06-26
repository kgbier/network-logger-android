package dev.kgbier.util.networklogger.view

import androidx.lifecycle.LifecycleCoroutineScope
import dev.kgbier.util.networklogger.model.HttpLogEvent
import dev.kgbier.util.networklogger.repository.HttpEventLogRepository
import dev.kgbier.util.networklogger.util.prettyPrintJsonString
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class ShareLogEventViewModel(
    private val eventId: String,
    private val repository: HttpEventLogRepository,
    private val coroutineScope: LifecycleCoroutineScope,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {

    sealed interface Event {
        data class ShareText(val text: String) : Event
    }

    private val _event = MutableSharedFlow<Event>()
    val event: Flow<Event>
        get() = _event

    fun sharePlainText() = coroutineScope.launch {
        val event = withContext(ioDispatcher) {
            repository.getEventById(eventId)
        }

        event.toPrettyString()
            .let { Event.ShareText(it) }
            .let { _event.emit(it) }
    }

    fun shareCurl() = coroutineScope.launch {
        val event = withContext(ioDispatcher) {
            repository.getEventById(eventId)
        }

        event.toCurlString()
            .let { Event.ShareText(it) }
            .let { _event.emit(it) }
    }

    private fun HttpLogEvent.toCurlString(): String = buildString {
        append("curl -X \"${transaction.method}\" \"${transaction.url}\"")
        request.headers.takeIf { it.isNotEmpty() }?.split("\n")?.forEach {
            appendBashNewline()
            append("-H '$it'")
        }
        request.body.takeIf { it.isNotEmpty() }?.let {
            appendBashNewline()
            append("-d $'$it'")
        }
    }

    private fun StringBuilder.appendBashNewline() = appendLine(" \\")

    private fun HttpLogEvent.toPrettyString(): String = buildString {
        appendLine("============= URL ============")
        appendLine(transaction.url)
        appendLine("=========== Method ===========")
        appendLine(transaction.method)
        response?.let {
            appendLine("==== Response Status Code ====")
            appendLine(it.statusCode)
        }
        appendLine("=========== Sent At ==========")
        appendLine(request.timestamp)
        appendLine("======= Request Headers ======")
        appendLine(request.headers)
        appendLine("======== Request Body ========")
        appendLine(prettyPrintJsonString(request.body))
        response?.let {
            appendLine("========= Received At ========")
            appendLine(it.timestamp)
            appendLine("====== Response Headers ======")
            appendLine(it.headers)
            appendLine("======== Response Body =======")
            appendLine(prettyPrintJsonString(it.body))
        }
    }
}