package dev.kgbier.util.networklogger.sample

import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import dev.kgbier.util.networklogger.makeNetworkLoggerActivityIntent
import dev.kgbier.util.networklogger.sample.di.MainDependencyGraph

class MainActivity : AppCompatActivity() {

    private val di = MainDependencyGraph(this)

    private val viewModel = MainViewModel(
        okHttpClient = di.okHttpClient,
        ktorHttpClient = di.ktorHttpClient,
    )

    private val textViewStatus by lazy {
        findViewById<TextView>(R.id.textViewStatus)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.buttonShowLogs).setOnClickListener {
            startActivity(makeNetworkLoggerActivityIntent(this))
        }

        findViewById<Button>(R.id.buttonMakeRequestKtor).setOnClickListener {
            viewModel.makeRequestKtor(
                onStart = ::handleOnStart,
                onSuccess = ::handleOnSuccess,
                onError = ::handleOnError,
            )
        }

        findViewById<Button>(R.id.buttonMakeRequestOkHttp).setOnClickListener {
            viewModel.makeRequestOkHttp(
                onStart = ::handleOnStart,
                onSuccess = ::handleOnSuccess,
                onError = ::handleOnError,
            )
        }
    }

    private fun handleOnStart() = runOnMain { textViewStatus.text = "Loading" }
    private fun handleOnSuccess() = runOnMain { textViewStatus.text = "Success!" }
    private fun handleOnError(error: Throwable) =
        runOnMain { textViewStatus.text = "Failed: ${error.message}" }

    private val mainThread by lazy { Handler(mainLooper) }

    private inline fun runOnMain(crossinline block: () -> Unit) {
        mainThread.post { block() }
    }
}