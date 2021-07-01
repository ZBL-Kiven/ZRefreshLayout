package com.zj.views.nest

import android.graphics.Rect
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.ListView
import android.widget.ScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.absoluteValue
import kotlin.math.sign

class NestChildHelper(private val parent: ViewGroup, private val rvi: ScrollingViewIn) : NestScrollView.NestIn {

    private var touchSlop = 0
    private var lastX = 0f
    private var lastY = 0f
    private var isMarkOrientation: Boolean? = null

    interface ScrollingViewIn {
        fun getScrollingView(): ViewGroup?
    }

    override fun dispatchChildEvent(ev: MotionEvent?): Boolean? {
        if (ev == null) return null
        val rect = Rect()
        parent.getLocalVisibleRect(rect)
        val scrolling = rvi.getScrollingView() ?: return false
        if (!scrolling.isNestedScrollingEnabled) return false
        when (scrolling) {
            is RecyclerView -> {
                val lm = scrolling.layoutManager as? LinearLayoutManager ?: return false
                val orientation = if (lm.orientation == RecyclerView.HORIZONTAL) Orientation.HOR else Orientation.VER
                if (!canChildScroll(orientation, -1f) && !canChildScroll(orientation, 1f)) {
                    return true // Early return if child can't scroll in same direction as parent
                }
                val mko = handleInterceptTouchEvent(ev, lm.orientation)
                val isRelease = when (lm.orientation) {
                    RecyclerView.HORIZONTAL -> {
                        mko.second == SCROLL_RIGHT && lm.findFirstCompletelyVisibleItemPosition() != 0
                    }
                    RecyclerView.VERTICAL -> {
                        mko.second == SCROLL_BOTTOM && lm.findFirstCompletelyVisibleItemPosition() != 0
                    }
                    else -> false
                }
                if (mko.first) {
                    return isRelease || (rect.top < parent.bottom && rect.bottom >= parent.bottom)
                }
            }
            is ListView -> {
                val c = scrolling.getChildAt(scrolling.firstVisiblePosition)
                return c != null && c.top != 0
            }
            is NestScrollView -> {
                return canChildScroll(Orientation.ANY, -1f) && !canChildScroll(Orientation.ANY, 1f)
            }
            is ScrollView -> {
                return canChildScroll(Orientation.ANY, -1f) && !canChildScroll(Orientation.ANY, 1f)
            }
            else -> throw java.lang.IllegalArgumentException("the ${scrolling::class.java.name} is no longer support in nest stick scrolling")
        }
        return true
    }

    private fun handleInterceptTouchEvent(e: MotionEvent, orientation: Int): Pair<Boolean, Int> {
        when (e.action) {
            MotionEvent.ACTION_DOWN -> {
                lastX = e.x;lastY = e.y;isMarkOrientation = null
                return Pair(true, 0)
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = e.x - lastX
                val dy = e.y - lastY
                val isHorizontal = orientation == RecyclerView.HORIZONTAL
                val scaledDx = dx.absoluteValue * if (isHorizontal) .5f else 1f
                val scaledDy = dy.absoluteValue * if (isHorizontal) 1f else .5f
                if (scaledDx > touchSlop || scaledDy > touchSlop) {
                    val markOrientation = isHorizontal != (scaledDy > scaledDx)
                    if (isMarkOrientation == null) isMarkOrientation = markOrientation
                    return isMarkOrientation!!.let {
                        Pair(it, if (it && scaledDy > scaledDx) {
                            if (dy > 0) SCROLL_BOTTOM else SCROLL_TOP
                        } else {
                            if (dx > 0) SCROLL_RIGHT else SCROLL_LEFT
                        })
                    }
                }
                lastX = e.x;lastY = e.y
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                lastX = 0f;lastY = 0f;isMarkOrientation = null
            }
        }
        return Pair(false, 0)
    }

    private fun canChildScroll(orientation: Orientation, delta: Float): Boolean {
        val direction = -delta.sign.toInt()
        return when (orientation) {
            Orientation.HOR -> rvi.getScrollingView()?.canScrollHorizontally(direction) ?: false
            Orientation.VER -> rvi.getScrollingView()?.canScrollVertically(direction) ?: false
            Orientation.ANY -> canChildScroll(Orientation.VER, delta) || canChildScroll(Orientation.HOR, delta)
        }
    }

    companion object {
        private const val SCROLL_BOTTOM = 0
        private const val SCROLL_TOP = 1
        private const val SCROLL_RIGHT = 2
        private const val SCROLL_LEFT = 3

        private enum class Orientation {
            HOR, VER, ANY
        }
    }
}