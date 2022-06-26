package dev.kgbier.util.networklogger.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.repeatOnLifecycle
import dev.kgbier.util.networklogger.R
import dev.kgbier.util.networklogger.repository.RealHttpEventLogRepository
import dev.kgbier.util.networklogger.view.list.RequestLogListAdapter
import kotlinx.coroutines.launch

internal class NetworkLogActivity : AppCompatActivity() {

    companion object {
        fun makeIntent(context: Context) = Intent(context, NetworkLogActivity::class.java)
    }

    private val view by lazy { LoadableListRootView(this) }
    private val viewModel = NetworkLogViewModel(
        repository = RealHttpEventLogRepository(this),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(view.root)

        lifecycle.coroutineScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.loadLogs()
                viewModel.state.collect(::renderState)
            }
        }
    }

    private fun renderState(state: NetworkLogViewModel.State) = when (state) {
        NetworkLogViewModel.State.Idle -> Unit
        is NetworkLogViewModel.State.EventList -> {
            view.progressBar.isGone = true
            view.recyclerView.isVisible = true
            view.recyclerView.adapter = RequestLogListAdapter(state.events, ::onCLickEventItem)
        }
        NetworkLogViewModel.State.Loading -> {
            view.progressBar.isVisible = true
            view.recyclerView.isGone = true
        }
    }

    private fun onCLickEventItem(id: String) {
        startActivity(NetworkLogEventActivity.makeIntent(this, id))
    }

    private fun clearLogs() {
        lifecycle.coroutineScope.launch {
            viewModel.clearLogs()
        }
    }

    private lateinit var menuItemClearAll: MenuItem
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuItemClearAll = menu.add("Clear all").apply {
            setIcon(R.drawable.ic_sweep_24)
            setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        menuItemClearAll.itemId -> {
            clearLogs()
            true
        }
        else -> false
    }
}
