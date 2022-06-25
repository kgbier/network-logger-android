package dev.kgbier.util.networklogger.view.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import com.squareup.contour.ContourLayout
import dev.kgbier.util.networklogger.R
import dev.kgbier.util.networklogger.view.util.resolveAttribute

class EventDetailsToolbarItemView(
    context: Context, attrs: AttributeSet?,
) : ContourLayout(context, attrs) {

    data class ViewModel(
        val onShare: () -> Unit,
    )

    constructor(context: Context) : this(context, null)

    private val imageViewShare = AppCompatImageView(context).apply {
        setImageResource(R.drawable.ic_share_24)
        scaleType = ImageView.ScaleType.CENTER_INSIDE
        resolveAttribute(android.R.attr.actionBarItemBackground)?.let(::setBackgroundResource)
    }

    init {
        imageViewShare.layoutBy(
            x = rightTo { parent.right() }.widthOf { 48.xdip },
            y = topTo { parent.top() }.heightOf { 48.ydip },
        )

        contourHeightWrapContent()
        contourWidthMatchParent()
    }

    fun bind(model: ViewModel) = with(model) {
        imageViewShare.setOnClickListener { onShare() }
    }
}