package com.evolitist.graphplotter.ui.view

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.preference.PreferenceManager
import com.evolitist.graphplotter.R
import com.evolitist.graphplotter.model.Point
import com.evolitist.graphplotter.ui.utils.GraphDrawable
import com.jsibbold.zoomage.ZoomageView
import kotlin.math.min

class GraphView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    ViewGroup(context, attrs, defStyleAttr), SharedPreferences.OnSharedPreferenceChangeListener {

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context) : this(context, null)

    private val tiv = ZoomageView(context, attrs, defStyleAttr).apply {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        setScaleRange(0.6f, 5f)
        doubleTapToZoomScaleFactor = 5f
    }.also {
        addView(it)
    }
    var points: Array<Point> = emptyArray()
        set(value) {
            field = value
            generateImage()
        }

    val bitmap: Bitmap
        get() {
            val bmp = Bitmap.createBitmap(GraphDrawable.size, GraphDrawable.size, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bmp)
            tiv.drawable?.draw(canvas)
            return bmp
        }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        PreferenceManager.getDefaultSharedPreferences(context)
            .registerOnSharedPreferenceChangeListener(this)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        PreferenceManager.getDefaultSharedPreferences(context)
            .unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        children.forEach { child ->
            if (child.visibility == View.GONE) return@forEach
            child.measure(
                MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
            )
            child.layout(0, 0, child.measuredWidth, child.measuredHeight)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val modeW = MeasureSpec.getMode(widthMeasureSpec)
        val modeH = MeasureSpec.getMode(heightMeasureSpec)
        if (modeW == MeasureSpec.UNSPECIFIED && modeH == MeasureSpec.UNSPECIFIED) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        } else if (modeW == MeasureSpec.UNSPECIFIED) {
            MeasureSpec.getSize(heightMeasureSpec).let {
                setMeasuredDimension(it, it)
            }
        } else if (modeH == MeasureSpec.UNSPECIFIED) {
            MeasureSpec.getSize(widthMeasureSpec).let {
                setMeasuredDimension(it, it)
            }
        } else {
            min(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec)).let {
                setMeasuredDimension(it, it)
            }
        }
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                parent.requestDisallowInterceptTouchEvent(true)
            }
            MotionEvent.ACTION_UP -> {
                parent.requestDisallowInterceptTouchEvent(false)
            }
        }
        return super.onInterceptTouchEvent(event)
    }

    override fun onSharedPreferenceChanged(prefs: SharedPreferences, key: String) {
        if (key == context.getString(R.string.key_graph_smooth)) {
            generateImage(prefs.getBoolean(key, true))
        }
    }

    private fun generateImage(smooth: Boolean? = null) {
        val b = smooth ?: PreferenceManager.getDefaultSharedPreferences(context).getBoolean(
            context.getString(R.string.key_graph_smooth), true
        )
        tiv.setImageDrawable(
            if (b) {
                GraphDrawable.Smooth(context, points)
            } else {
                GraphDrawable.Rough(context, points)
            }
        )
    }
}