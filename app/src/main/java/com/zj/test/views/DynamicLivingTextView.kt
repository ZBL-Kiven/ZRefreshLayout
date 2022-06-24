package com.zj.test.views

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import kotlin.random.Random

class DynamicLivingTextView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, def: Int = 0) : AppCompatTextView(context, attrs, def) {

    private var animWidth: Float = 4.toPx()
    private var animWidthPadding: Float = 2.toPx()
    private var widthMargin: Float = 7.toPx()

    private var minAnimHeight: Float = 3.toPx()
    private var randomHeight: Float = 4.toPx()
    private var interval = 1.5f

    private val data = arrayOf(LivingStaffDrawer(), LivingStaffDrawer(), LivingStaffDrawer())

    private var startAnim = false

    private val textWidth: Float
        get() {
            return paint.measureText(text.toString())
        }
    private val textHeight: Float
        get() {
            return paint.fontMetrics.run { bottom - top }
        }

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
        canvas?.let {
            val count = it.save()
            if (startAnim) {
                repeat(data.size) { i ->
                    val cur = data[i]
                    val next: Float
                    val xS = paddingStart + (animWidth + animWidthPadding) * i * 1f
                    if (cur.mode) {
                        next = (cur.height + interval).coerceAtMost(cur.maxHeight)
                        if (next >= cur.maxHeight) {
                            buildNextDrawer(cur)
                        } else {
                            cur.height = next
                        }
                    } else {
                        next = (cur.height - interval).coerceAtMost(cur.maxHeight)
                        if (next <= cur.minHeight) {
                            buildNextDrawer(cur)
                        } else {
                            cur.height = next
                        }
                    }
                    val baseLineHeight: Float = paint.fontMetrics.run { descent - ascent } + paddingTop
                    it.drawRect(xS, baseLineHeight - next, xS + animWidth, baseLineHeight, paint)
                }
                it.translate(widthMargin + (animWidth + animWidthPadding) * data.size, 0f)
            }
            super.onDraw(canvas)
            it.restoreToCount(count)
            if (startAnim) postInvalidate()
        }
    }

    private fun buildNextDrawer(drawer: LivingStaffDrawer) {
        if (drawer.mode) {
            drawer.minHeight = minAnimHeight + (Random.nextFloat() * randomHeight - randomHeight / 2)
            drawer.mode = false
        } else {
            val textHeight = paint.fontMetrics.run { descent - ascent }
            drawer.maxHeight = textHeight + (Random.nextFloat() * randomHeight - randomHeight / 2)
            drawer.mode = true
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (startAnim && !text.isNullOrEmpty()) {
            val measureWidth = 0.5f + textWidth + (animWidth + animWidthPadding) * data.size + widthMargin + paddingStart + paddingEnd
            val measureHeight = 0.5f + textHeight + paddingTop + paddingBottom
            setMeasuredDimension(measureWidth.toInt(), measureHeight.toInt())
        }
    }

    private class LivingStaffDrawer {
        var mode: Boolean = true
        var height: Float = 0f
        var minHeight: Float = 0f
            set(value) {
                if (height <= 0) height = value
                field = value
            }
        var maxHeight: Float = 0f
    }

    private fun Int.toPx(): Float {
        return 0.5f + this * Resources.getSystem().displayMetrics.density
    }
}