package dev.kgbier.util.networklogger.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.repeatOnLifecycle
import dev.kgbier.util.networklogger.R
import dev.kgbier.util.networklogger.repository.RealHttpEventLogRepository
import dev.kgbier.util.networklogger.view.list.EventDetailsListAdapter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

internal class NetworkLogEventActivity : AppCompatActivity() {

    companion object {
        private const val ARG_EVENT_ID = "arg_event_id"

        fun makeIntent(
            context: Context,
            eventId: String,
        ) = Intent(context, NetworkLogEventActivity::class.java).apply {
            putExtra(ARG_EVENT_ID, eventId)
        }
    }

    private val view by lazy { LoadableListRootView(this) }
    private val viewModel by lazy {
        NetworkLogEventViewModel(
            eventId = intent.getStringExtra(ARG_EVENT_ID) ?: "",
            repository = RealHttpEventLogRepository(this),
            coroutineScope = lifecycle.coroutineScope,
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(view.root)

        lifecycle.coroutineScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.onEach(::renderState).launchIn(this)
                viewModel.event.onEach(::handleEvent).launchIn(this)
                viewModel.loadEvent()
            }
        }
    }

    private fun handleEvent(event: NetworkLogEventViewModel.Event) = when (event) {
        is NetworkLogEventViewModel.Event.ShowShare -> {
            Toast.makeText(this, "share", Toast.LENGTH_SHORT).show()
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

    private lateinit var menuItemShare: MenuItem
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuItemShare = menu.add("Share").apply {
            setIcon(R.drawable.ic_share_24)
            setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        menuItemShare.itemId -> {
            viewModel.showShare()
            true
        }
        else -> false
    }
}
