package dev.kgbier.util.networklogger.util

import dev.kgbier.util.networklogger.view.widget.EventStatus

fun statusCodeToStatus(statusCode: Int?): EventStatus = when (statusCode) {
    null,
    in 100..199,
    -> EventStatus.INFO
    in 200..299 -> EventStatus.OK
    in 300..399 -> EventStatus.INDETERMINATE
    else -> EventStatus.ERROR
}