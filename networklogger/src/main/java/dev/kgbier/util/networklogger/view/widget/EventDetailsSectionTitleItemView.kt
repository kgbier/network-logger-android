package dev.kgbier.util.networklogger.view.widget

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.R
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.widget.TextViewCompat
import com.squareup.contour.ContourLayout

internal class EventDetailsSectionTitleItemView(
    context: Context, attrs: AttributeSet?,
) : ContourLayout(context, attrs) {

    data class ViewModel(val title: String)

    constructor(context: Context) : this(context, null)

    private val textViewTitle = AppCompatTextView(context).apply {
        TextViewCompat.setTextAppearance(this, R.style.TextAppearance_AppCompat_Caption)
    }

    init {
        textViewTitle.layoutBy(
            x = leftTo { parent.left() }.rightTo { parent.right() },
            y = topTo { parent.top() },
        )

        contourHeightWrapContent()
        contourWidthMatchParent()

        if (isInEditMode) {
            bind(ViewModel("Title"))
        }
    }

    fun bind(model: ViewModel) = with(model) {
        textViewTitle.text = title
    }
}
