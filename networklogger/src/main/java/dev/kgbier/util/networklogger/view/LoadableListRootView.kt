package dev.kgbier.util.networklogger.view

import android.content.Context
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class LoadableListRootView(context: Context) {
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