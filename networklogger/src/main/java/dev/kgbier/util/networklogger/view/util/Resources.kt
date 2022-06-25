package dev.kgbier.util.networklogger.view.util

import android.content.Context
import android.util.TypedValue
import android.view.View
import androidx.annotation.AttrRes
import androidx.core.content.ContextCompat

/**
 * Resource attribute (attr) resolution
 */

fun Context.resolveColorAttribute(@AttrRes resource: Int): Int? =
    resolveAttribute(resource)?.let {
        ContextCompat.getColor(this, it)
    }

fun View.resolveColorAttribute(@AttrRes resource: Int): Int? =
    context.resolveColorAttribute(resource)

fun Context.resolveDimensionAttribute(@AttrRes resource: Int): Int? =
    resolveAttribute(resource)?.let {
        resources.getDimensionPixelSize(it)
    }

fun View.resolveDimensionAttribute(@AttrRes resource: Int): Int? =
    context.resolveDimensionAttribute(resource)

fun Context.resolveAttribute(@AttrRes resource: Int): Int? = with(TypedValue()) {
    if (theme.resolveAttribute(resource, this, true)) {
        return resourceId
    } else null
}

fun View.resolveAttribute(@AttrRes resource: Int): Int? =
    context.resolveAttribute(resource)
