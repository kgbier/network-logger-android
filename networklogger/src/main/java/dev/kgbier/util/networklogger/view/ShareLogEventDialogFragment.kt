package dev.kgbier.util.networklogger.view

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.os.bundleOf
import androidx.core.view.setPadding
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import dev.kgbier.util.networklogger.view.util.resolveAttribute


@SuppressLint("SetTextI18n")
internal class ShareLogEventDialogFragment : DialogFragment() {

    companion object {
        const val TAG = "ShareLogEventDialogFragment:sharedetails"
        private const val ARG_EVENT_ID = "arg_event_id"

        fun newInstance(eventId: String): ShareLogEventDialogFragment =
            ShareLogEventDialogFragment().apply {
                arguments = bundleOf(ARG_EVENT_ID to eventId)
            }
    }

    class RootView(context: Context) {

        val root: RelativeLayout
        val layoutView: LinearLayout
        val textViewPlainText: ItemTextView
        val textViewCurl: ItemTextView

        init {
            root = RelativeLayout(context)

            layoutView = LinearLayout(context).apply {
                layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
                orientation = LinearLayout.VERTICAL
            }.also { root.addView(it) }

            textViewPlainText = ItemTextView(context).apply {
                layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
                text = "Text"
            }.also { layoutView.addView(it) }

            textViewCurl = ItemTextView(context).apply {
                layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
                text = "cURL"
            }.also { layoutView.addView(it) }
        }

        class ItemTextView(context: Context) : AppCompatTextView(
            context,
            null,
            androidx.appcompat.R.style.TextAppearance_AppCompat_Large,
        ) {
            init {
                val padding = (resources.displayMetrics.density * 24).toInt()
                setPadding(padding)
                context.resolveAttribute(android.R.attr.selectableItemBackground)
                    ?.let { setBackgroundResource(it) }
            }
        }
    }

    private var view: RootView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = ContextThemeWrapper(
        layoutInflater.context,
        androidx.appcompat.R.style.ThemeOverlay_AppCompat_Dialog,
    ).let { RootView(it) }
        .also {
            view = it
            viewLifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
                override fun onDestroy(owner: LifecycleOwner) {
                    super.onDestroy(owner)
                    view = null
                }
            })
        }
        .also { bindView(it) }
        .root

    private fun bindView(view: RootView) {
        view.textViewPlainText.setOnClickListener { }
        view.textViewCurl.setOnClickListener { }
    }
}