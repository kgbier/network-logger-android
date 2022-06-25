package dev.kgbier.util.networklogger.sample

import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val textViewStatus by lazy {
        findViewById<TextView>(R.id.textViewStatus)
    }

    private val viewModel = MainViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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