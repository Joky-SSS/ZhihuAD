package com.joky.zhihuad

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.widget.FrameLayout
import kotlin.math.max
import kotlin.math.min


class PathHoverView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {
    private val STATE_OPEN = 0
    private val STATE_CLOSE = 1
    private var currentState = 0
    private val mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val circlePath = Path()
    private val rectPath = Path()
    private val mPath = Path()
    private var maxRadius = 0


    var progress = 100
        set(value) {
            val tempVal = min(max(value, 0), 100)
            field = tempVal
            if (progress == 100) currentState = STATE_OPEN
            if (progress == 0) currentState = STATE_CLOSE
            postInvalidate()
        }
    init {
        mPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        maxRadius = Math.sqrt(w * w + h * h + 0.0).toInt()
        rectPath.reset()
        rectPath.addRect(0F, 0F, w.toFloat(), h.toFloat(), Path.Direction.CW)
    }

    override fun dispatchDraw(canvas: Canvas) {
        circlePath.reset()
        mPath.reset()
        if (currentState == STATE_OPEN) {
            circlePath.addCircle(width.toFloat(), height.toFloat(), (maxRadius * (100 - progress) / 100).toFloat(), Path.Direction.CW)
            mPath.op(rectPath, circlePath, Path.Op.INTERSECT)
        } else {
            circlePath.addCircle(0F, 0F, (maxRadius * progress / 100).toFloat(), Path.Direction.CW)
            mPath.op(rectPath, circlePath, Path.Op.DIFFERENCE)
        }
        val save = canvas.saveLayer(0F, 0F, width.toFloat(), height.toFloat(), null)
        super.dispatchDraw(canvas)
        canvas.drawPath(mPath, mPaint)
        canvas.restoreToCount(save)
    }
}