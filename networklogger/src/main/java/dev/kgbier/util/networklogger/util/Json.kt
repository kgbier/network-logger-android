package dev.kgbier.util.networklogger.util

import org.json.JSONObject

internal fun prettyPrintJsonString(input: String): String = runCatching {
    JSONObject(input).toString(2)
}.getOrDefault(input)