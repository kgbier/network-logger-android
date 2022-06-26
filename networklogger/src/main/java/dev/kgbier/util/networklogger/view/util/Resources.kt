package dev.kgbier.util.networklogger.view.util

import android.content.Context
import android.util.TypedValue
import android.view.View
import androidx.annotation.AttrRes
import androidx.core.content.ContextCompat

/**
 * Resource attribute (attr) resolution
 */

internal fun Context.resolveColorAttribute(@AttrRes resource: Int): Int? =
    resolveAttribute(resource)?.let {
        ContextCompat.getColor(this, it)
    }

internal fun View.resolveColorAttribute(@AttrRes resource: Int): Int? =
    context.resolveColorAttribute(resource)

internal fun Context.resolveDimensionAttribute(@AttrRes resource: Int): Int? =
    resolveAttribute(resource)?.let {
        resources.getDimensionPixelSize(it)
    }

internal fun View.resolveDimensionAttribute(@AttrRes resource: Int): Int? =
    context.resolveDimensionAttribute(resource)

internal fun Context.resolveAttribute(@AttrRes resource: Int): Int? = with(TypedValue()) {
    if (theme.resolveAttribute(resource, this, true)) {
        return resourceId
    } else null
}

internal fun View.resolveAttribute(@AttrRes resource: Int): Int? =
    context.resolveAttribute(resource)
