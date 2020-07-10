package com.evolitist.graphplotter.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.children
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.evolitist.graphplotter.BR
import com.evolitist.graphplotter.R
import com.evolitist.graphplotter.model.Point
import com.evolitist.graphplotter.ui.view.GraphView

@BindingAdapter("entries", "layout")
fun <T> ViewGroup.setEntries(entries: Array<T>?, layoutId: Int) {
    children.filterNot { it.id == R.id.table_header }.forEach { removeView(it) }
    LayoutInflater.from(context).run {
        if (entries != null) {
            for (entry in entries) {
                DataBindingUtil.inflate<ViewDataBinding>(
                    this,
                    layoutId,
                    this@setEntries,
                    true
                ).run {
                    setVariable(BR.data, entry)
                    if (root.parent == null) {
                        addView(root)
                    }
                }
            }
        }
    }
}

@BindingAdapter("points")
fun GraphView.setPoints(entries: Array<Point>?) {
    val p = entries ?: emptyArray()
    if (!points.contentEquals(p)) {
        points = p
    }
}
