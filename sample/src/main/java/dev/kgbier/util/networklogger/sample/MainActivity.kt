package dev.kgbier.util.networklogger.sample

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import dev.kgbier.util.networklogger.Constants
import dev.kgbier.util.networklogger.sample.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.buttonMain).apply {
            text = Constants.libstring
        }
    }
}