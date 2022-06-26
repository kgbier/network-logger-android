package dev.kgbier.util.networklogger.view

import dev.kgbier.util.networklogger.model.SparseHttpLogEvent
import dev.kgbier.util.networklogger.repository.HttpEventLogRepository
import dev.kgbier.util.networklogger.util.statusCodeToStatus
import dev.kgbier.util.networklogger.util.timestampDistanceFromNow
import dev.kgbier.util.networklogger.view.widget.RequestLogItemView
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import java.net.URI

internal class NetworkLogViewModel(
    private val repository: HttpEventLogRepository,
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
}