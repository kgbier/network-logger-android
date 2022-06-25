package dev.kgbier.util.networklogger.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.kgbier.util.networklogger.repository.RealHttpLoggingRepository
import dev.kgbier.util.networklogger.view.list.EventDetailsListAdapter
import kotlinx.coroutines.launch

class NetworkLogEventActivity : AppCompatActivity() {

    companion object {
        private const val ARG_EVENT_ID = "arg_event_id"

        fun makeIntent(
            context: Context,
            eventId: String,
        ) = Intent(context, NetworkLogEventActivity::class.java).apply {
            putExtra(ARG_EVENT_ID, eventId)
        }
    }

    class View(context: Context) {
        val root: FrameLayout
        val progressBar: ProgressBar
        val recyclerView: RecyclerView

        init {
            root = FrameLayout(context)

            progressBar = ProgressBar(context).apply {
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    Gravity.CENTER,
                )
            }.also { root.addView(it) }

            recyclerView = RecyclerView(context).apply {
                layoutManager = LinearLayoutManager(context)
            }.also { root.addView(it) }
        }
    }

    private val view by lazy { View(this) }
    val viewModel by lazy {
        NetworkLogEventViewModel(
            eventId = intent.getStringExtra(ARG_EVENT_ID) ?: "",
            repository = RealHttpLoggingRepository(this),
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(view.root)

        lifecycle.coroutineScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.loadEvent()
                viewModel.state.collect(::renderState)
            }
        }
    }

    private fun renderState(state: NetworkLogEventViewModel.State) = when (state) {
        NetworkLogEventViewModel.State.Idle -> Unit
        is NetworkLogEventViewModel.State.Event -> {
            view.progressBar.isGone = true
            view.recyclerView.isVisible = true
            view.recyclerView.adapter = EventDetailsListAdapter(state.listItems)
        }
        NetworkLogEventViewModel.State.Loading -> {
            view.progressBar.isVisible = true
            view.recyclerView.isGone = true
        }
    }
}
