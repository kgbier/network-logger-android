package dev.kgbier.util.networklogger.view

import dev.kgbier.util.networklogger.model.HttpLogEvent
import dev.kgbier.util.networklogger.repository.HttpLoggingRepository
import dev.kgbier.util.networklogger.util.statusCodeToStatus
import dev.kgbier.util.networklogger.util.timestampDistanceFromNow
import dev.kgbier.util.networklogger.view.widget.EventDetailsHeaderItemView
import dev.kgbier.util.networklogger.view.widget.EventDetailsSectionTitleItemView
import dev.kgbier.util.networklogger.view.widget.EventDetailsTextAreaItemView
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import java.net.URI

class NetworkLogEventViewModel(
    private val eventId: String,
    private val repository: HttpLoggingRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default,
) {

    private val _state = MutableStateFlow<State>(State.Idle)

    val state: Flow<State> get() = _state

    sealed interface State {
        object Idle : State
        object Loading : State
        data class Event(val listItems: List<Any>) : State
    }

    suspend fun loadEvent() = withContext(defaultDispatcher) {
        _state.value = State.Loading
        val event = withContext(ioDispatcher) {
            repository.getEventById(eventId)
        }
        _state.value = State.Event(event.toItemList())
    }

    private fun HttpLogEvent.toItemList() = mutableListOf<Any>().apply {
        val uri = URI.create(transaction.url)

        add(EventDetailsHeaderItemView.ViewModel(
            host = uri.host,
            path = uri.path,
            method = transaction.method,
            sentAt = timestampDistanceFromNow(request.timestamp),
            status = response?.statusCode?.let {
                EventDetailsHeaderItemView.ViewModel.Status(
                    statusCodeToStatus(it),
                    it.toString()
                )
            },
        ))
        add(EventDetailsSectionTitleItemView.ViewModel("Request headers"))
        add(EventDetailsTextAreaItemView.ViewModel(request.headers))
        uri.query?.let {
            add(EventDetailsSectionTitleItemView.ViewModel("Request Query"))
            add(EventDetailsTextAreaItemView.ViewModel(it))
        }
        add(EventDetailsSectionTitleItemView.ViewModel("Request Body"))
        add(EventDetailsTextAreaItemView.ViewModel(request.body))

        response?.let {
            add(EventDetailsSectionTitleItemView.ViewModel("Response headers"))
            add(EventDetailsTextAreaItemView.ViewModel(it.headers))
            add(EventDetailsSectionTitleItemView.ViewModel("Response Body"))
            add(EventDetailsTextAreaItemView.ViewModel(it.body))
        }
    }
}