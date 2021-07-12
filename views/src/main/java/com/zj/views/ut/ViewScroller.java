package com.zj.views.ut;

import android.content.Context;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.Interpolator;
import android.widget.OverScroller;

import androidx.core.view.ViewCompat;

@SuppressWarnings("unused")
public abstract class ViewScroller {

    static final int SCROLL_STATE_IDLE = 0;
    static final int SCROLL_STATE_DRAGGING = 1;
    static final int SCROLL_STATE_SETTLING = 2;

    public ViewScroller(View targetView) {
        this.targetView = targetView;
        init();
    }

    abstract void constrainScrollBy(int dx, int dy);

    private final View targetView;

    private int mMinFlingVelocity = 0;
    private int mMaxFlingVelocity = 0;
    private int mTouchSlop = 0;
    private int mScrollState = SCROLL_STATE_IDLE;
    private final VelocityTracker mVelocityTracker = VelocityTracker.obtain();
    private ViewFling mViewFling;

    private void init() {
        mViewFling = new ViewFling(targetView.getContext());
        ViewConfiguration vc = ViewConfiguration.get(targetView.getContext());
        mTouchSlop = vc.getScaledTouchSlop();
        mMinFlingVelocity = vc.getScaledMinimumFlingVelocity();
        mMaxFlingVelocity = vc.getScaledMaximumFlingVelocity();
    }

    private void addMovement(MotionEvent event) {
        MotionEvent vte = MotionEvent.obtain(event);
        mVelocityTracker.addMovement(vte);
        vte.recycle();
    }

    public void onEventDown(MotionEvent event) {
        addMovement(event);
    }

    public void onEventMove(MotionEvent event, float lastY) {
        if (mScrollState != SCROLL_STATE_DRAGGING) {
            boolean startScroll = false;
            float dy = lastY - event.getRawY();
            if (Math.abs(dy) > mTouchSlop) {
                startScroll = true;
            }
            if (startScroll) {
                setScrollState(SCROLL_STATE_DRAGGING);
            }
        }
        addMovement(event);
    }

    public void onEventUp(MotionEvent event) {
        addMovement(event);
        mVelocityTracker.computeCurrentVelocity(1000, mMaxFlingVelocity);
        float yVelocity = -mVelocityTracker.getYVelocity();
        if (Math.abs(yVelocity) < mMinFlingVelocity) yVelocity = 0f;
        else yVelocity = Math.min(-mMaxFlingVelocity, Math.max(yVelocity, mMaxFlingVelocity));
        if (yVelocity != 0f) {
            mViewFling.fling((int) yVelocity);
        } else {
            setScrollState(SCROLL_STATE_IDLE);
        }
        resetTouch();
    }

    public void clear() {
        resetTouch();
        mViewFling.stop();
        mScrollState = SCROLL_STATE_IDLE;
    }

    private void setScrollState(int state) {
        if (state == mScrollState) {
            return;
        }
        mScrollState = state;
        if (state != SCROLL_STATE_SETTLING) {
            mViewFling.stop();
        }
    }

    private void resetTouch() {
        if (mVelocityTracker != null) mVelocityTracker.clear();
    }


    private class ViewFling implements Runnable {

        ViewFling(Context context) {
            this.mScroller = new OverScroller(context, new Interpolator() {
                @Override
                public float getInterpolation(float f) {
                    float t = f - 1.0f;
                    return t * t * t * t * t + 1.0f;
                }
            });
        }

        private int mLastFlingY, mLastFlingX = 0;
        private boolean mEatRunOnAnimationRequest = false;
        private boolean mReSchedulePostAnimationCallback = false;
        private final OverScroller mScroller;

        @Override
        public void run() {
            disableRunOnAnimationRequests();
            OverScroller scroller = mScroller;
            if (scroller.computeScrollOffset()) {
                int x = scroller.getCurrX();
                int y = scroller.getCurrY();
                int dy = y - mLastFlingY;
                int dx = x - mLastFlingX;
                mLastFlingY = y;
                mLastFlingX = x;
                constrainScrollBy(dx, dy);
                postOnAnimation();
            }
            enableRunOnAnimationRequests();
        }

        public void fling(int velocityY) {
            mLastFlingY = mLastFlingX = 0;
            setScrollState(SCROLL_STATE_SETTLING);
            mScroller.fling(0, 0, 0, velocityY, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE);
            postOnAnimation();
        }

        public void stop() {
            targetView.removeCallbacks(this);
            mScroller.abortAnimation();
        }

        private void disableRunOnAnimationRequests() {
            mReSchedulePostAnimationCallback = false;
            mEatRunOnAnimationRequest = true;
        }

        private void enableRunOnAnimationRequests() {
            mEatRunOnAnimationRequest = false;
            if (mReSchedulePostAnimationCallback) {
                postOnAnimation();
            }
        }

        private void postOnAnimation() {
            if (mEatRunOnAnimationRequest) {
                mReSchedulePostAnimationCallback = true;
            } else {
                targetView.removeCallbacks(this);
                ViewCompat.postOnAnimation(targetView, this);
            }
        }
    }
}
