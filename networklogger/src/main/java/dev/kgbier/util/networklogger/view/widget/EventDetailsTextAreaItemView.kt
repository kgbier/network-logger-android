package dev.kgbier.util.networklogger.view.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.widget.HorizontalScrollView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.setPadding
import androidx.core.widget.TextViewCompat
import com.squareup.contour.ContourLayout
import dev.kgbier.util.networklogger.R

@SuppressLint("SetTextI18n")
internal class EventDetailsTextAreaItemView(
    context: Context, attrs: AttributeSet?,
) : ContourLayout(context, attrs) {

    data class ViewModel(val content: String)

    constructor(context: Context) : this(context, null)

    private val scrollView = HorizontalScrollView(context).apply {
        setBackgroundResource(R.color.background)
    }

    private val textViewContent = AppCompatTextView(context).apply {
        layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        setPadding(16.dip)
    }.also { scrollView.addView(it) }

    init {
        scrollView.layoutBy(
            x = leftTo { parent.left() }.rightTo { parent.right() },
            y = topTo { parent.top() },
        )

        contourHeightWrapContent()
        contourWidthMatchParent()

        if (isInEditMode) {
            bind(
                ViewModel(
                    """
                    Large amount of multiline content
                    Large amount of multiline content which should totally be able to scroll
                    Large amount of multiline content
                    Large amount of multiline content
                    Large amount of multiline content
                    Large amount of multiline content
                    """.trimIndent()
                )
            )
        }
    }

    fun bind(model: ViewModel) = with(model) {
        if (content.isEmpty()) {
            textViewContent.text = "Empty"
            TextViewCompat.setTextAppearance(
                textViewContent,
                androidx.appcompat.R.style.TextAppearance_AppCompat_Caption,
            )
        } else {
            textViewContent.text = content
            TextViewCompat.setTextAppearance(
                textViewContent,
                androidx.appcompat.R.style.TextAppearance_AppCompat_Body1,
            )
        }
        textViewContent.typeface = Typeface.MONOSPACE
    }
}