package com.zj.test.views

import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.util.AttributeSet
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.widget.AppCompatImageView
import java.lang.IllegalArgumentException
import kotlin.math.min

class DynamicLivingImageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, def: Int = 0) : AppCompatImageView(context, attrs, def) {

    private var size = 0
        set(value) {
            if (field != value) {
                initHolders(value)
            }
            field = value
        }

    private var step = 1f
    private val density = 2
    private val oversize = 6.toPx()
    private val stayTime = 10f
    private val colorNods = floatArrayOf(.9f, 1f)
    private var startAnim = false
    private val holders = mutableListOf<HaloInfo>()
    private val colors = intArrayOf(Color.TRANSPARENT, Color.RED)
    private lateinit var mPaint: Paint
    private val interceptor = DecelerateInterpolator(0.5f)

    fun startLivingAnim() {
        startAnim = true
        postInvalidate()
        requestLayout()
    }

    fun stopLivingAnim() {
        startAnim = false
        requestLayout()
    }

    override fun onDraw(canvas: Canvas?) {
        if (startAnim) {
            canvas?.let {
                val count = it.save()
                drawHalo(it)
                super.onDraw(canvas)
                it.restoreToCount(count)
            }
            postInvalidate()
        } else {
            super.onDraw(canvas)
        }
    }

    private fun initHolders(size: Int) {
        repeat(density) {
            val cur = it * 1.0f / density * (size + oversize)
            holders.add(HaloInfo(cur))
        }
    }
    
    private fun drawHalo(canvas: Canvas) {
        holders.also { it.sortByDescending { i -> i.currentR } }.forEach {
            if (!::mPaint.isInitialized) {
                mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
            }
            mPaint.reset()
            it.currentR += step
            val center = size / 2f
            val maxSize = center + oversize
            if (it.currentR <= maxSize + stayTime) {
                val cur = it.currentR.coerceAtMost(maxSize)
                if (it.currentR > maxSize) {
                    mPaint.alpha = ((1f - (it.currentR - maxSize) / stayTime) * 255).toInt()
                }
                mPaint.style = Paint.Style.FILL
                val radius = (maxSize) * interceptor.getInterpolation((cur / maxSize).coerceAtMost(1f))
                mPaint.shader = RadialGradient(center, center, radius, colors, colorNods, Shader.TileMode.CLAMP)
                canvas.drawCircle(center, center, radius, mPaint)
            } else {
                it.reset()
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        if (w > 0 && h > 0) {
            size = min(w, h)
        }
        super.onSizeChanged(w, h, oldw, oldh)
    }

    private class HaloInfo(cur: Float) {
        var currentR = cur
        var holding = false

        fun reset() {
            currentR = 0f
            holding = true
        }
    }

    private fun Int.toPx(): Float {
        return 0.5f + this * Resources.getSystem().displayMetrics.density
    }


}