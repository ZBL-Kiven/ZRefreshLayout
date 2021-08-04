package com.zj.views.nest

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.OverScroller
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.NestedScrollingParent
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import kotlin.math.roundToInt

/**
 * Created by Zjj on 21.7.7
 * */
@Suppress("unused")
open class NestRecyclerView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, def: Int = 0) : RecyclerView(context, attributeSet, def) {

    private val mParentScrollConsumed = IntArray(2)
    private var nestScrollerIn: NestScrollerIn? = null
    private var mCurrentFling = 0
    private var nestRootParent: ViewGroup? = null
    private var selfUnderTheAppBarRootParent: ViewGroup? = null
    private var consumedSelf: Boolean = false
    private var interceptNextEvent: Boolean? = null
    private var overScrollerDispatchToParent: Boolean = true
    private var headerOffsetChangedListener: HeaderOffsetChangedListener? = null
    private var nestHeader: View? = null
    private var lastY: Float = 0f
    private var lastDy: Float = 0f

    @SuppressLint("ClickableViewAccessibility")
    override fun onFinishInflate() {
        super.onFinishInflate()
        overScrollMode = OVER_SCROLL_NEVER
        descendantFocusability = FOCUS_BLOCK_DESCENDANTS
        post {
            checkNotCoordinatorParent()
            findDefaultOverScroller()
            nestHeader?.let {
                onPatchNestMeasureHeight()
                getScrollFlags()
            }
            repeat(childCount) {
                getChildAt(it).let { child ->
                    isFocusableInTouchMode = false
                    if (child is RecyclerView) {
                        child.descendantFocusability = FOCUS_BLOCK_DESCENDANTS
                    }
                }
            }
        }
    }

    private val overScroller = OverScroller(context) {
        val t = it - 1.0f
        t * t * t * t * t + 1.0f
    }

    /**
     * Open the internal multi-layer nested scrolling interface,
     * NestRecyclerView supports multi-level nested scrolling,
     * and the nesting protocol of its direct subclass should be specified at each level separately
     * */
    fun withNestIn(nestScrollerIn: NestScrollerIn?) {
        this.nestScrollerIn = nestScrollerIn
    }

    /**
     * NestRecyclerView supports linkage with other Views as Header, and AppBarLayout is supported by default.
     * You can to use [AppBarLayout] as the linkage head to make it more convenient to use the SystemUi compatible solution and the specified distance linkage solution specified by the system.
     * If you need to customize the linkage Header, please implement the [NestHeaderIn] protocol in any View and add it to any parents leaf node or root node.
     * */
    fun setOnHeaderOffsetChangedListener(l: HeaderOffsetChangedListener?) {
        this.headerOffsetChangedListener = l
    }

    /**
     * The processing here will satisfy whether there is a sliding processing of the linkage head at the top.
     * And handle the ScrollFlag.SNAP event in [MotionEvent.ACTION_UP].
     * */
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev == null) return super.dispatchTouchEvent(ev)
        if (ev.actionMasked == MotionEvent.ACTION_DOWN) {
            if (!overScroller.isFinished) {
                overScroller.abortAnimation()
            }
        }
        when (ev.action) {
            MotionEvent.ACTION_CANCEL -> abortScroller()
            MotionEvent.ACTION_DOWN -> {
                lastY = ev.rawY;interceptNextEvent = true
            }
            MotionEvent.ACTION_MOVE -> {
                try {
                    lastDy = lastY - ev.rawY
                    if (overScrollerDispatchToParent) {
                        val ie = blockIfOverScrollDispatch(lastDy.roundToInt(), ViewCompat.TYPE_TOUCH)
                        if (ie != interceptNextEvent) interceptNextEvent = ie
                        if (ie) invalidate()
                    }
                } finally {
                    lastY = ev.rawY
                }
            }
            MotionEvent.ACTION_UP -> {
                checkPendingSnapEvent(lastDy)
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    /**
     * if (actionMasked == MotionEvent.ACTION_DOWN
     * || mFirstTouchTarget != null) {
     * final boolean disallowIntercept = (mGroupFlags & FLAG_DISALLOW_INTERCEPT) != 0;
     * if (!disallowIntercept) {
     *      intercepted = onInterceptTouchEvent(ev);
     *      ev.setAction(action); // restore action in case it was changed
     *   } else {
     *      intercepted = false;
     *   }
     * }
     *
     * onInterceptTouchEvent Conditions of execution actionMasked == MotionEvent.ACTION_DOWN 或者 mFirstTouchTarget != null
     * mFirstTouchTarget != null means that there is a child that can handle the event
     * */
    override fun onInterceptTouchEvent(e: MotionEvent?): Boolean {
        return super.onInterceptTouchEvent(e)
    }

    /**
     * Different from [RecyclerView.dispatchNestedPreScroll], NestRecyclerView will give priority to the processing power of nested sliding,
     * until it can not complete the scroll in the same direction and pass it to the child View.
     * When processing scroll events after consumption via ParentView,
     * refer to the parameter comments [RecyclerView.dispatchNestedPreScroll] can be linked with other components
     * that implement [androidx.core.view.NestedScrollingChild3] [androidx.core.view.NestedScrollingChild3],
     * and its [getNestedChild] will receive scroll events after [canScrollVertically] false
     * */
    override fun dispatchNestedPreScroll(dx: Int, dy: Int, consumed: IntArray?, offsetInWindow: IntArray?, type: Int): Boolean {
        if ((!isNestHeaderFold() && !canScrollVertically(-1)) || interceptNextEvent == true) return false
        val consume = consumed ?: IntArray(2)
        if (dy > 0) { // up
            if (!canScrollVertically(1)) {
                val target = getNestedChild()
                target?.apply {
                    this.scrollBy(0, dy)
                    consume[1] = dy
                    val tpv = this.parent
                    val scrollVer = target.canScrollVertically(1)
                    consumedSelf = (tpv as? NestedScrollingParent)?.let {
                        if (!scrollVer) it.onNestedScroll(target, 0, 0, dx, dy)
                        true
                    } ?: scrollVer
                }
            }
        }
        if (dy < 0) { // down
            val target = getNestedChild()
            target?.apply {
                if (this.canScrollVertically(-1)) {
                    this.scrollBy(0, dy)
                    consume[1] = dy
                    consumedSelf = true
                }
            }
        }
        val parentScrollConsumed = mParentScrollConsumed
        val parentConsumed = super.dispatchNestedPreScroll(dx, dy - consume[1], parentScrollConsumed, offsetInWindow, type)
        consume[1] += parentScrollConsumed[1]
        return consumedSelf || parentConsumed
    }

    /**
     * Fling events will be considered to be consumed here,
     * and the events will be allocated one by one through [overScroller].
     * For details of allocation, see [computeScroll]
     * */
    override fun dispatchNestedPreFling(velocityX: Float, velocityY: Float): Boolean {
        if (!isNestHeaderFold() && !canScrollVertically(-1)) return true
        mCurrentFling = 0
        overScroller.fling(0, 0, 0, velocityY.roundToInt(), 0, 0, Int.MIN_VALUE, Int.MAX_VALUE)
        invalidate()
        return true
    }

    /**
     * @return It is at least a Child with unlimited length or a reasonable length.
     * When its length and the length of the parent control cannot meet the minimum height of this view, nested sliding will not work.
     * It might be a View class that does not implement the [androidx.core.view.NestedScrollingChild3] protocol,
     * but they should to scrollable functions such as ScrollTo and ScrollBy, and meet touch events that support scroller by their self.
     * Commonly used Views such as RecyclerView, ListView, ViewPager, NestScrollView, ScrollView, etc. are recommended.
     * These target Views need`nt require any additional special processing.
     * and the target View is need`nt as a direct children of NestRecyclerView.
     * */
    open fun getNestedChild(): View? {
        return nestScrollerIn?.getInnerView()
    }

    private fun checkNotCoordinatorParent(anchor: View? = this) {
        anchor?.parent?.let {
            return if (it is CoordinatorLayout) {
                throw IllegalArgumentException("NestRecyclerView cannot be a subclass of CoordinatorLayout.If you need to implement Martial-Design's Appbar function, you can add a AppBarLayout under the same levels leaf layout or root layout.")
            } else checkNotCoordinatorParent(it as? View)
        }
    }

    /**
     * Get the ScrollFlags of the internal View of the head [AppBarLayout].
     * If the parent layout does not contain any [AppBarLayout],
     * this method will not complete the call.
     * @return ScrollFlags If the execution is complete, false -1
     * */
    private fun findAppbarLayoutTopViewScrollFlags(): Int {
        val appBarLayout = (findDefaultOverScroller() as? AppBarLayout) ?: return -1
        var firstFlags: Int = -1
        repeat(appBarLayout.childCount) { i ->
            (appBarLayout.getChildAt(i).layoutParams as? AppBarLayout.LayoutParams)?.let {
                if (it.scrollFlags.and(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL) != 0) {
                    firstFlags = it.scrollFlags
                    return@repeat
                }
            }
        }
        return firstFlags
    }

    private fun onPatchNestMeasureHeight() {
        val nrp = nestRootParent
        val ahl = nestHeader
        val sup = selfUnderTheAppBarRootParent
        if (nrp != null && ahl != null && sup != null) {
            when (nrp) {
                is LinearLayout -> {
                    if (nrp.orientation == LinearLayout.HORIZONTAL) {
                        throw  IllegalArgumentException("NestRecyclerView does not support linkage with AppBarLayout in horizontal scrolling as a multi-layer nest")
                    }
                    if (nrp != sup && (sup.measuredHeight <= 0 || sup.measuredHeight >= nrp.measuredHeight)) {
                        val lp = sup.layoutParams ?: LinearLayout.LayoutParams(sup.measuredHeight, sup.measuredHeight)
                        lp.height = nrp.measuredHeight - (ahl.measuredHeight - getHeaderTotalHeight())
                        sup.layoutParams = lp
                    }
                }
                else -> Log.e("NestRecyclerViewError", "case: When the head and NestRecyclerView are not specified as a vertical dependency, their height will not be accurately calculated. It is recommended to use a vertical LinearLayout as the parent of the head view")
            }
        }
        requestLayout()
    }

    /**
     * Find any [AppBarLayout] or [NestHeaderIn] as its head to achieve the scrolling linkage,
     * @see [NestHeaderIn]
     * */
    private fun findDefaultOverScroller(anchor: View? = this.parent as? ViewGroup, parentIds: MutableList<Int> = mutableListOf()): View? {
        if (anchor == null) return null
        if (nestHeader == null) {
            (anchor as? ViewGroup)?.let { p ->
                repeat(p.childCount) {
                    val v = p.getChildAt(it)
                    if (v is AppBarLayout || v is NestHeaderIn) {
                        nestRootParent = p
                        nestHeader = v
                        if (parentIds.isNullOrEmpty()) selfUnderTheAppBarRootParent = nestRootParent
                    } else {
                        parentIds.add(p.id)
                    }
                }
                if (nestHeader == null) {
                    return findDefaultOverScroller(p.parent as? View, parentIds)
                } else {
                    repeat(p.childCount) {
                        val v = p.getChildAt(it)
                        if (v.id in parentIds) {
                            selfUnderTheAppBarRootParent = v as? ViewGroup
                        }
                    }
                }
            }
        }
        return nestHeader
    }

    /**
     * @return scrollFlags or -1 ，
     * @see [findDefaultOverScroller]
     * */
    private fun getScrollFlags(): Int {
        if (!hasNestHeaders()) return -1
        return if (nestHeader is AppBarLayout) {
            findAppbarLayoutTopViewScrollFlags()
        } else {
            (nestHeader as? NestHeaderIn)?.getScrollFlags() ?: -1
        }
    }

    private fun checkFlags(flag: Int, flags: Int? = getScrollFlags()): Boolean {
        return flags?.and(flag) == flag
    }

    private fun checkPendingSnapEvent(dy: Float = 0f) {
        if (!isNestHeaderFold()) {
            val offset = getScrolledOffset()
            nestHeader?.let {
                val x2 = getHeaderTotalHeight() - offset
                abortScroller()
                if (checkFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP)) {
                    val d = if (offset > x2) x2 else -x2
                    post {
                        overScroller.startScroll(0, 0, 0, d, 500)
                        invalidate()
                    }
                } else {
                    if (dy == 0f) return
                    post {
                        overScroller.startScroll(0, 0, 0, if (dy < 0) -offset else offset, 500)
                        invalidate()
                    }
                }
            }
        }
    }

    private fun abortScroller() {
        if (!overScroller.isFinished) {
            overScroller.abortAnimation()
        }
        mCurrentFling = 0
    }

    /**
     * [blockIfOverScrollDispatch] Can block the nesting mechanism,
     * used for head linkage, or overwrite it to achieve other behaviors
     * */
    override fun computeScroll() {
        if (overScroller.computeScrollOffset()) {
            val current = overScroller.currY
            val dy = current - mCurrentFling
            mCurrentFling = current
            var blockSelf = false
            if (overScrollerDispatchToParent) {
                blockSelf = blockIfOverScrollDispatch(dy, ViewCompat.TYPE_NON_TOUCH)
            }
            val target = getNestedChild()
            if (!blockSelf) {
                if (dy > 0) {
                    if (canScrollVertically(1)) {
                        scrollBy(0, dy)
                    } else {
                        if (target?.canScrollVertically(1) == true) {
                            target.scrollBy(0, dy)
                        } else {
                            if (!overScroller.isFinished) {
                                overScroller.abortAnimation()
                            }
                        }
                    }
                }
                if (dy < 0) {
                    if (target?.canScrollVertically(-1) == true) {
                        target.scrollBy(0, dy)
                    } else {
                        if (canScrollVertically(-1)) {
                            scrollBy(0, dy)
                        } else {
                            if (!overScroller.isFinished) {
                                overScroller.abortAnimation()
                            }
                        }
                    }
                }
            }
            invalidate()
        }
        super.computeScroll()
    }

    open fun blockIfOverScrollDispatch(dy: Int, @ViewCompat.NestedScrollType type: Int): Boolean {
        val flags = getScrollFlags()
        if (flags == -1) {
            return false
        }
        run moveTop@{
            if (canScrollNestHeader(findDefaultOverScroller() ?: return@moveTop, dy)) {
                onComputeScroll(dy, type)
                return@blockIfOverScrollDispatch true
            }
        }
        return false
    }

    open fun isNestHeaderFold(): Boolean {
        return getScrolledOffset() >= getHeaderTotalHeight()
    }

    open fun canScrollNestHeader(headView: View, dy: Int): Boolean {
        val x1 = getScrolledOffset()
        return if (dy >= 0) {
            x1 < getHeaderTotalHeight()
        } else {
            if (checkFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED)) {
                !canScrollVertically(-1) && x1 < headView.height
            } else {
                x1 < headView.height
            }
        }
    }

    open fun getScrolledOffset(): Int {
        return nestHeader?.let {
            val r = Rect()
            it.getGlobalVisibleRect(r)
            if (r.isEmpty) return@getScrolledOffset 0
            val stableHeight = it.bottom - getHeaderTotalHeight()
            getHeaderTotalHeight() - (r.height() - stableHeight)
        } ?: 0
    }

    open fun onComputeScroll(dy: Int, type: Int) {
        var offset = getScrolledOffset()
        val total = getHeaderTotalHeight()
        if (dy > 0 && offset + dy >= total) {
            nestRootParent?.scrollTo(0, total)
            offset = total
        } else if (dy < 0 && offset + dy <= 0) {
            nestRootParent?.scrollTo(0, 0)
            offset = 0
        } else {
            nestRootParent?.scrollBy(0, dy)
        }
        (nestHeader as? NestHeaderIn)?.onScrolling(offset, total, type)
        headerOffsetChangedListener?.onChanged(offset, total, type)
        invalidate()
    }

    open fun getHeaderTotalHeight(): Int {
        (nestHeader as? AppBarLayout)?.let {
            return it.totalScrollRange
        }
        (nestHeader as? NestHeaderIn)?.let {
            return it.getTotalScrollRange()
        }
        return 0
    }

    /**
     * Whether to detect the default head view, the default is to detect,
     * @see [NestHeaderIn]
     * */
    open fun hasNestHeaders(): Boolean {
        return true
    }

    /**
     * It can be implemented as an upper-level Header View that implements linked scrolling.
     * It can be implemented on any type of View, even a View that does not support nested sliding.
     * */
    interface NestHeaderIn {
        /**
         * the total scroll distance you need,
         *It must be established when [getScrollFlags] contains [AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL],
         * this property is used.
         * You can use the Top of some established View to hover some controls
         * */
        fun getTotalScrollRange(): Int

        /**
         * This callback is available when [getScrollFlags] points to a scrollable Flag
         * */
        fun onScrolling(cur: Int, total: Int, @ViewCompat.NestedScrollType type: Int)

        fun getScrollFlags(): Int = AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED
    }

    interface HeaderOffsetChangedListener {

        fun onChanged(cur: Int, total: Int, @ViewCompat.NestedScrollType type: Int)

    }

    /**
     * Used to specify and allow other ScrollingView to be added inside,
     * but there can only be one child control (mainly nested folding control) that continues the recycling mechanism inside.
     * */
    interface NestScrollerIn {

        fun getInnerView(): View?

    }
}