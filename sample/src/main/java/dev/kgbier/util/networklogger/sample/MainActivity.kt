package dev.kgbier.util.networklogger.sample

import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import dev.kgbier.util.networklogger.sample.di.MainDependencyGraph
import dev.kgbier.util.networklogger.view.NetworkLogActivity

class MainActivity : AppCompatActivity() {

    private val di = MainDependencyGraph(this)

    private val viewModel = MainViewModel(
        okHttpClient = di.okHttpClient,
    )

    private val textViewStatus by lazy {
        findViewById<TextView>(R.id.textViewStatus)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.buttonShowLogs).setOnClickListener {
            startActivity(NetworkLogActivity.makeIntent(this))
        }

        findViewById<Button>(R.id.buttonMakeRequest).setOnClickListener {
            viewModel.makeRequest(
                onStart = { runOnMain { textViewStatus.text = "Loading" } },
                onSuccess = { runOnMain { textViewStatus.text = "Success!" } },
                onError = { runOnMain { textViewStatus.text = "Failed: ${it.message}" } },
            )
        }
    }

    private val mainThread by lazy { Handler(mainLooper) }

    private inline fun runOnMain(crossinline block: () -> Unit) {
        mainThread.post { block() }
    }
}