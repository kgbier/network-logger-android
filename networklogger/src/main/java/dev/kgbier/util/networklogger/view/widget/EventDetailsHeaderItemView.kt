package dev.kgbier.util.networklogger.view.widget

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import androidx.appcompat.R
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.widget.TextViewCompat
import com.squareup.contour.ContourLayout

class EventDetailsHeaderItemView(
    context: Context, attrs: AttributeSet?
) : ContourLayout(context, attrs) {

    data class ViewModel(
        val host: String,
        val path: String,
        val method: String,
        val sentAt: String,
        val status: Status?,
    ) {
        data class Status(
            val eventStatus: EventStatus,
            val statusCode: String,
        )
    }

    constructor(context: Context) : this(context, null)

    private val indicator = EventStatusIndicatorDrawable().apply {
        bounds = Rect(0, 0, 12.dip, 12.dip)
    }

    private val textViewHost = AppCompatTextView(context).apply {
        TextViewCompat.setTextAppearance(this, R.style.TextAppearance_AppCompat_Caption)
    }
    private val textViewPath = AppCompatTextView(context).apply {
        TextViewCompat.setTextAppearance(this, R.style.TextAppearance_AppCompat_Body1)
    }
    private val textViewMethod = AppCompatTextView(context).apply {
        TextViewCompat.setTextAppearance(this, R.style.TextAppearance_AppCompat_Body1)
        compoundDrawablePadding = 4.dip
        setCompoundDrawablesRelative(indicator, null, null, null)
    }
    private val textViewTimeSent = AppCompatTextView(context).apply {
        TextViewCompat.setTextAppearance(this, R.style.TextAppearance_AppCompat_Caption)
    }

    init {
        val endContentBarrier = { minOf(textViewMethod.left(), textViewTimeSent.left()) }

        textViewHost.layoutBy(
            x = leftTo { parent.left() }.rightTo { endContentBarrier() - 8.dip },
            y = topTo { parent.top() },
        )
        textViewPath.layoutBy(
            x = leftTo { parent.left() }.rightTo { endContentBarrier() - 8.dip },
            y = topTo { textViewHost.bottom() },
        )

        textViewMethod.layoutBy(
            x = rightTo { parent.right() },
            y = topTo { parent.top() },
        )
        textViewTimeSent.layoutBy(
            x = rightTo { parent.right() },
            y = topTo { textViewMethod.bottom() },
        )

        contourHeightWrapContent()
        contourWidthMatchParent()

        if (isInEditMode) {
            bind(
                ViewModel(
                    "google.com",
                    "/search",
                    "POST",
                    "3m 32s",
                    ViewModel.Status(
                        EventStatus.OK,
                        "200",
                    )
                )
            )
        }
    }

    fun bind(model: ViewModel) = with(model) {
        indicator.status = status?.eventStatus ?: EventStatus.INFO

        textViewHost.text = host
        textViewPath.text = path
        textViewMethod.text = if (status == null) {
            method
        } else {
            "(${status.statusCode}) $method"
        }
        textViewTimeSent.text = sentAt
    }
}