package com.evolitist.graphplotter

import android.content.Context
import android.content.res.Resources
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt

val Int.dp: Float get() = this * Resources.getSystem().displayMetrics.density
val Double.dp: Float get() = this.toFloat() * Resources.getSystem().displayMetrics.density

@ColorInt
fun Context.getColorAttr(@AttrRes resId: Int): Int {
    val tv = TypedValue()
    theme.resolveAttribute(resId, tv, true)
    return tv.data
}
