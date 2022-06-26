package dev.kgbier.util.networklogger.util

import android.util.Base64

internal fun String.toBase64(): String = Base64.encodeToString(encodeToByteArray(), Base64.DEFAULT)
internal fun String.fromBase64(): String = Base64.decode(this, Base64.DEFAULT).decodeToString()