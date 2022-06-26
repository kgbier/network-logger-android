package dev.kgbier.util.networklogger.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.concurrent.TimeUnit

internal fun timestampDistanceFromNow(timestamp: Long): String =
    getTimeBetween(Date(), Date(timestamp))

internal fun timestampDistanceBetween(timestampFrom: Long, timestampTo: Long): String =
    getTimeBetween(Date(timestampFrom), Date(timestampTo))

internal fun getTimeBetween(from: Date, to: Date): String {
    val diff = from.time - to.time

    val secsAgo = TimeUnit.MILLISECONDS.toSeconds(diff)
    val minsAgo = TimeUnit.MILLISECONDS.toMinutes(diff)

    return if (minsAgo < 60) {
        val outputMins: Long = minsAgo % 60
        val outputSecs: Long = secsAgo % 60

        val str = mutableListOf<String>()

        val millsAgo = TimeUnit.MILLISECONDS.toMillis(diff)
        if (millsAgo < 1000) {
            str.add("${millsAgo % 1000}ms")
        } else {
            if (outputMins > 0) {
                str.add("${outputMins}m")
            }
            if (outputSecs > 0) {
                str.add("${outputSecs}s")
            }
        }

        str.joinToString(" ")
    } else {
        SimpleDateFormat.getInstance().format(to)
    }
}