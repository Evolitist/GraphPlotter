package com.evolitist.graphplotter.ui.utils

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import com.evolitist.graphplotter.dp
import com.evolitist.graphplotter.getColorAttr
import com.evolitist.graphplotter.model.Point
import com.xujiaao.android.bezier.spline.BezierSpline

sealed class GraphDrawable(private val context: Context, val points: Array<Point>) : Drawable() {
    companion object {
        const val size = 1024
        private const val half = size / 2f
        private const val one = size / 200f
    }

    private val axisPaint = Paint().apply {
        color = context.getColorAttr(android.R.attr.colorForeground)
        style = Paint.Style.STROKE
        strokeWidth = 0.5.dp
    }
    private val guidelinePaint = Paint().apply {
        color = context.getColorAttr(android.R.attr.colorControlHighlight)
        style = Paint.Style.STROKE
        strokeWidth = 0.5.dp
    }
    protected val graphPaint = Paint().apply {
        color = context.getColorAttr(android.R.attr.textColorLink)
        style = Paint.Style.STROKE
        strokeWidth = 0.5.dp
        isAntiAlias = true
    }
    protected val pointPaint = Paint().apply {
        color = context.getColorAttr(android.R.attr.textColorLinkInverse)
        style = Paint.Style.FILL
    }

    override fun getIntrinsicWidth() = size
    override fun getIntrinsicHeight() = size
    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT
    override fun setAlpha(alpha: Int) = Unit
    override fun setColorFilter(colorFilter: ColorFilter?) = Unit
    override fun draw(canvas: Canvas) {
        val bounds = RectF(bounds)
        canvas.takeIf { !bounds.isEmpty && alpha != 0 }?.run {
            save()
            translate(bounds.left, bounds.top)
            scale(bounds.width() / size, bounds.height() / size)
            prepare()
            onDraw()
            restore()
        }
    }

    private fun Canvas.prepare() {
        for (i in -9..9) {
            val paint = if (i == 0) axisPaint else guidelinePaint
            val mid = half + i * one * 10
            drawLine(mid, 0f, mid, size.toFloat(), paint)
            drawLine(0f, mid, size.toFloat(), mid, paint)
        }
    }

    abstract fun Canvas.onDraw()

    class Rough(context: Context, points: Array<Point>) : GraphDrawable(context, points) {
        override fun Canvas.onDraw() {
            if (points.isNotEmpty()) {
                points.reduce { acc, point ->
                    val startX = half + acc.x * one
                    val startY = half + acc.y * one
                    drawLine(startX, startY, half + point.x * one, half + point.y * one, graphPaint)
                    drawCircle(startX, startY, 1.dp, pointPaint)
                    point
                }.let { last ->
                    drawCircle(half + last.x * one, half + last.y * one, 1.dp, pointPaint)
                }
            }
        }
    }

    class Smooth(context: Context, points: Array<Point>) : GraphDrawable(context, points) {
        private val spline = BezierSpline(points.size)
        private val path = Path()

        override fun Canvas.onDraw() {
            points.forEachIndexed { index, point ->
                val px = half + point.x * one
                val py = half + point.y * one
                spline.set(index, px, py)
                drawCircle(px, py, 1.dp, pointPaint)
            }
            spline.applyToPath(path)
            drawPath(path, graphPaint)
        }
    }
}