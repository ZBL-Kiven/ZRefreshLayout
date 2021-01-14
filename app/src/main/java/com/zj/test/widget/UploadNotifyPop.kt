package com.zj.test.widget

import android.animation.Animator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.graphics.PixelFormat
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import com.zj.test.R
import java.lang.ref.WeakReference

@Suppress("unused")
object UploadNotifyPop : Application.ActivityLifecycleCallbacks, Animator.AnimatorListener, ValueAnimator.AnimatorUpdateListener {
    private var ivPic: ImageView? = null
    private var sbProgress: SeekBar? = null
    private var tvDesc: TextView? = null
    private var close: View? = null
    private var retry: View? = null
    private var state: UploadingState? = null
    private var wlp: WindowManager.LayoutParams? = null
    private var rootView: View? = null
    private var curY = 0f
    private var lastY = 0f
    private var offsetY = 0f
    private var isInit = false
    private var isNeedReloadWhenResumed = false
    private var path = ""
    private var desc = ""
    private var isDismissing = false
    private var valueAnim: ValueAnimator? = null
    private val rootViewId: Int; get() = rootView?.id ?: 0x7f00a

    private const val DISMISS_INTERVAL = 3000L
    private val handler = Handler(Looper.getMainLooper()) {
        if (it.what == rootViewId) {
            dismiss()
            false
        } else true
    }
    private var curCtx: WeakReference<Context>? = null

    private fun init(context: Application) {
        if (isInit) return
        isInit = true
        context.registerActivityLifecycleCallbacks(this)
        valueAnim = ValueAnimator.ofFloat(0.0f, 1.0f).setDuration(200)
        valueAnim?.addListener(this)
        valueAnim?.addUpdateListener(this)
    }

    fun show(context: Context, path: String, desc: String) {
        init(context.applicationContext as Application)
        this.path = path
        this.desc = desc
        isNeedReloadWhenResumed = true
        curCtx = WeakReference(context)
        startWithAnim(context)
        state = UploadingState.UP_LOADING.withProgress(0)
        initWithState()
    }

    private fun parseWithActivity(context: Context) {
        if (isNeedReloadWhenResumed) initView(context)
        removeFormWindow(rootView?.context ?: return)
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        wlp = buildWlp()
        try {
            wm.addView(rootView, wlp)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun startWithAnim(context: Context) {
        parseWithActivity(context)
        rootView?.let { v ->
            v.alpha = 0f
            rootView?.post {
                v.translationY = v.height * -1f
                startAnim()
            }
        }
    }

    private fun buildWlp(): WindowManager.LayoutParams {
        val wlp = WindowManager.LayoutParams()
        wlp.gravity = Gravity.TOP.or(Gravity.CENTER_HORIZONTAL)
        wlp.flags = wlp.flags or WindowManager.LayoutParams.FLAG_IGNORE_CHEEK_PRESSES or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT
        wlp.height = WindowManager.LayoutParams.WRAP_CONTENT
        wlp.format = PixelFormat.TRANSLUCENT
        wlp.type = WindowManager.LayoutParams.TYPE_APPLICATION_PANEL
        wlp.token = null
        this.wlp = wlp
        return wlp
    }

    @JvmStatic
    fun dismiss() {
        isNeedReloadWhenResumed = false
        if (isDismissing) return
        isDismissing = true
        startAnim()
    }

    fun setState(state: UploadingState) {
        isNeedReloadWhenResumed = true
        this@UploadNotifyPop.state = state
        if (state == UploadingState.UP_LOADING) {
            handler.removeMessages(rootViewId)
        } else {
            handler.sendEmptyMessageDelayed(rootViewId, DISMISS_INTERVAL)
        }
        initWithState()
        if (state != UploadingState.UP_LOADING) curCtx?.get()?.let {
            startWithAnim(it)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initView(context: Context) {
        if (rootView == null) {
            rootView = View.inflate(context, R.layout.pop_view, null)
            ivPic = rootView?.findViewById(R.id.upload_pop_uploading_iv_pic)
            sbProgress = rootView?.findViewById(R.id.upload_pop_uploading_sb_progress)
            tvDesc = rootView?.findViewById(R.id.upload_pop_uploading_tv_desc)
            close = rootView?.findViewById(R.id.upload_pop_uploading_v_close)
            retry = rootView?.findViewById(R.id.upload_pop_uploading_v_retry)
            sbProgress?.setOnTouchListener { _, _ -> true }
            rootView?.setOnTouchListener { _, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        curY = event.rawY;lastY = event.rawY
                    }
                    MotionEvent.ACTION_MOVE -> {
                        val step = curY - event.rawY
                        if (step > 0) {
                            offsetY += step
                        } else {
                            offsetY = 0f
                        }
                        curY = event.rawY
                    }
                    MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                        lastY = 0f;curY = 0f
                        if (offsetY > 50) dismiss()
                    }
                }
                false
            }
        }
        tvDesc?.text = desc
        //        Glide.with(ivPic).load(path).thumbnail(0.1f).into(ivPic)
        initWithState()
        close?.setOnClickListener {
            removeFormWindow(it.context)
        }
    }

    private fun initWithState() {
        if (!isAddedToWindow() || state == null) return
        sbProgress?.visibility = if (state == UploadingState.UP_LOADING) View.VISIBLE else View.GONE
        retry?.visibility = if (state == UploadingState.FAILED) View.VISIBLE else View.GONE
        close?.visibility = if (state != UploadingState.SUCCESS) View.VISIBLE else View.GONE
        sbProgress?.progress = state?.progress ?: 0
    }

    private fun removeFormWindow(context: Context) {
        if (rootView?.parent == null) return
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        wm.removeView(rootView)
    }

    private fun isAddedToWindow(): Boolean {
        return rootView?.parent != null
    }

    enum class UploadingState {
        UP_LOADING, FAILED, SUCCESS;

        var progress: Int = 0
            get() {
                return if (this == UP_LOADING) field else 0
            }
            private set

        fun withProgress(progress: Int): UploadingState {
            require(this == UP_LOADING) { "only uploading state can taken progress params" }
            this.progress = progress
            return this
        }
    }

    private fun onAnimDuration(animatedFraction: Float) {
        val af = animatedFraction.coerceAtLeast(0.0f).coerceAtMost(1.0f)
        val fra = if (isDismissing) 1.0f - af else af
        rootView?.let { v ->
            v.alpha = fra
            v.translationY = if (isDismissing) -af * v.measuredHeight else -(1.0f - af) * v.measuredHeight
        }
        if (af >= 0.9f) rootView?.visibility = if (isDismissing) View.GONE else View.VISIBLE
        if (isDismissing && af >= 1) {
            curCtx?.get()?.let { removeFormWindow(it) }
            isDismissing = false
        }
    }

    private fun startAnim() {
        valueAnim?.let {
            if (it.isStarted) it.end()
            it.start()
        }
    }

    override fun onAnimationRepeat(animation: Animator?) {}

    override fun onAnimationEnd(animation: Animator?) {
        onAnimDuration(1.0f)
    }

    override fun onAnimationCancel(animation: Animator?) {
        onAnimDuration(1.0f)
    }

    override fun onAnimationStart(animation: Animator?) {
        onAnimDuration(0f)
    }

    override fun onAnimationUpdate(animation: ValueAnimator?) {
        onAnimDuration(animation?.animatedFraction ?: 0f)
    }

    override fun onActivityPaused(activity: Activity) {
        removeFormWindow(activity)
    }

    override fun onActivityStarted(activity: Activity) {
    }

    override fun onActivityDestroyed(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
    }

    override fun onActivityResumed(activity: Activity) {
        curCtx = WeakReference(activity)
        if (isNeedReloadWhenResumed) activity.window.decorView.post {
            parseWithActivity(activity)
        }
    }
}