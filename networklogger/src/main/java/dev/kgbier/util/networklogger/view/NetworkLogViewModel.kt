package dev.kgbier.util.networklogger.view

import dev.kgbier.util.networklogger.model.SparseHttpLogEvent
import dev.kgbier.util.networklogger.repository.HttpLoggingRepository
import dev.kgbier.util.networklogger.view.widget.EventStatus
import dev.kgbier.util.networklogger.view.widget.RequestLogItemView
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import java.net.URI
import java.text.SimpleDateFormat
import java.util.Date
import java.util.concurrent.TimeUnit

class NetworkLogViewModel(
    private val repository: HttpLoggingRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default,
) {

    private val _state = MutableStateFlow<State>(State.Idle)

    val state: Flow<State> get() = _state

    sealed interface State {
        object Idle : State
        object Loading : State
        data class EventList(val events: List<RequestLogItemView.ViewModel>) : State
    }

    suspend fun loadLogs() = withContext(defaultDispatcher) {
        _state.value = State.Loading
        val events = withContext(ioDispatcher) {
            repository.getEvents()
        }
        val list = events.map { it.toViewModel() }
        _state.value = State.EventList(list)
    }

    suspend fun clearLogs() = withContext(defaultDispatcher) {
        withContext(ioDispatcher) {
            repository.clearAll()
        }
        loadLogs()
    }

    private fun SparseHttpLogEvent.toViewModel(): RequestLogItemView.ViewModel {
        val uri = URI.create(url)
        return RequestLogItemView.ViewModel(
            id = uuid,
            path = uri.path,
            host = uri.host,
            status = statusCodeToStatus(statusCode),
            sentAt = timestampDistanceFromNow(requestTimestamp),
        )
    }

    private fun statusCodeToStatus(statusCode: Int?): EventStatus =
        when (statusCode) {
            null,
            in 100..199,
            -> EventStatus.INFO
            in 200..299 -> EventStatus.OK
            in 300..399 -> EventStatus.INDETERMINATE
            else -> EventStatus.ERROR
        }

    private fun timestampDistanceFromNow(timestamp: Long): String =
        getTimeBetween(Date(), Date(timestamp))

    private fun timestampDistanceBetween(timestampFrom: Long, timestampTo: Long): String =
        getTimeBetween(Date(timestampFrom), Date(timestampTo))

    private fun getTimeBetween(from: Date, to: Date): String {
        val diff = from.time - to.time

        val secsAgo = TimeUnit.MILLISECONDS.toSeconds(diff)
        val minsAgo = TimeUnit.MILLISECONDS.toMinutes(diff)

        return if (minsAgo < 60) {
            val outputMins: Long = minsAgo % 60
            val outputSecs: Long = secsAgo % 60

            val str = mutableListOf<String>()

            val millsAgo = TimeUnit.MILLISECONDS.toMillis(diff)
            if (millsAgo < 1000) {
                str.add("${millsAgo % 1000}ms")
            } else {
                if (outputMins > 0) {
                    str.add("${outputMins}m")
                }
                if (outputSecs > 0) {
                    str.add("${outputSecs}s")
                }
            }

            str.joinToString(" ")
        } else {
            SimpleDateFormat.getInstance().format(to)
        }
    }
}