package com.zj.views.nest

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent

@Suppress("unused")
class NestScrollView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, def: Int = 0) : androidx.core.widget.NestedScrollView(context, attributeSet, def) {

    interface NestIn {
        fun dispatchChildEvent(ev: MotionEvent?): Boolean?
    }

    private var curChildScrollMod = false
    private var nestScrollIn: NestIn? = null

    fun setNestScrollIn(si: NestIn) {
        this.nestScrollIn = si
    }

    private var childScrollAble = false

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        if (childScrollAble == curChildScrollMod && !childScrollAble) return true
        return super.onInterceptTouchEvent(ev)
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        childScrollAble = nestScrollIn?.dispatchChildEvent(ev) ?: false
        when (ev?.action) {
            MotionEvent.ACTION_MOVE -> {
                if (childScrollAble != curChildScrollMod) {
                    curChildScrollMod = childScrollAble
                    val b = reDispatchTouchEvent(ev)
                    return if (!childScrollAble) b else false
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun reDispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev == null) return dispatchTouchEvent(ev)
        ev.action = MotionEvent.ACTION_CANCEL
        val ev2 = MotionEvent.obtain(ev)
        this.dispatchTouchEvent(ev)
        ev2.action = MotionEvent.ACTION_DOWN
        return this.dispatchTouchEvent(ev2)
    }
}