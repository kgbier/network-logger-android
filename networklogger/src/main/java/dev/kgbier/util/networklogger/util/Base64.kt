package dev.kgbier.util.networklogger.util

import android.util.Base64

fun String.toBase64() = Base64.encodeToString(encodeToByteArray(), Base64.DEFAULT)
fun String.fromBase64() = Base64.decode(this, Base64.DEFAULT).decodeToString()