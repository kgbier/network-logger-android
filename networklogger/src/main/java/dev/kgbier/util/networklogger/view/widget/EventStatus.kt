package dev.kgbier.util.networklogger.view.widget

import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import dev.kgbier.util.networklogger.view.colour.BLUEGREY_500
import dev.kgbier.util.networklogger.view.colour.GREEN_500
import dev.kgbier.util.networklogger.view.colour.RED_500
import dev.kgbier.util.networklogger.view.colour.YELLOW_500

internal enum class EventStatus {
    OK,
    INFO,
    INDETERMINATE,
    ERROR,
}

internal class EventStatusIndicatorDrawable : ShapeDrawable() {

    init {
        shape = OvalShape()
    }

    var status: EventStatus = EventStatus.INDETERMINATE
        set(value) {
            field = value
            when (value) {
                EventStatus.OK -> GREEN_500
                EventStatus.INFO -> BLUEGREY_500
                EventStatus.INDETERMINATE -> YELLOW_500
                EventStatus.ERROR -> RED_500
            }.also { setTint(it) }
        }
}