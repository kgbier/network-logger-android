package dev.kgbier.util.networklogger.view

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity

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
}
