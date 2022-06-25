package dev.kgbier.util.networklogger.view.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.graphics.Typeface
import android.text.style.TextAppearanceSpan
import android.util.AttributeSet
import androidx.appcompat.R
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.text.buildSpannedString
import androidx.core.text.inSpans
import androidx.core.widget.TextViewCompat
import com.squareup.contour.ContourLayout
import dev.kgbier.util.networklogger.view.util.resolveAttribute

class RequestLogItemView(
    context: Context, attrs: AttributeSet?,
) : ContourLayout(context, attrs) {

    data class ViewModel(
        val id: String,
        val path: String,
        val host: String,
        val status: EventStatus,
        val sentAt: String,
    )

    constructor(context: Context) : this(context, null)

    private val indicator = EventStatusIndicatorDrawable().apply {
        bounds = Rect(0, 0, 16.dip, 16.dip)
    }

    private val textViewInfo = AppCompatTextView(context).apply {
        TextViewCompat.setTextAppearance(this,
                                         R.style.TextAppearance_AppCompat_Body1)
        typeface = Typeface.MONOSPACE
        compoundDrawablePadding = 16.dip
        setCompoundDrawablesRelative(indicator, null, null, null)
    }

    private val textViewSentAt = AppCompatTextView(context).apply {
        TextViewCompat.setTextAppearance(this, R.style.TextAppearance_AppCompat_Caption)
    }

    init {
        resolveAttribute(R.attr.selectableItemBackground)?.let(::setBackgroundResource)

        textViewInfo.layoutBy(
            x = leftTo { parent.left() }.rightTo { textViewSentAt.left() - 8.dip },
            y = topTo { parent.top() },
        )

        textViewSentAt.layoutBy(
            x = rightTo { parent.right() },
            y = topTo { parent.top() },
        )

        contourHeightWrapContent()
        contourWidthMatchParent()

        @SuppressLint("SetTextI18n")
        if (isInEditMode) {
            bindEventModel(
                ViewModel(
                    id = "id",
                    path = "/api/v2/search",
                    host = "clientapi.staging.com",
                    status = EventStatus.OK,
                    sentAt = "Sent 30s ago",
                )
            )
        }
    }

    private val captionStyle =
        TextAppearanceSpan(context, R.style.TextAppearance_AppCompat_Caption)

    fun bindEventModel(event: ViewModel) = with(event) {
        indicator.status = status

        textViewInfo.text = buildSpannedString {
            append(path)
            append("\n")
            inSpans(captionStyle) { append(host) }
        }
        textViewSentAt.text = sentAt
    }
}