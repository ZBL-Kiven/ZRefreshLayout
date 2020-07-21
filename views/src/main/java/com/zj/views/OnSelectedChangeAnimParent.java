package com.zj.views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by ZJJ on 2018/7/17.
 */

@SuppressWarnings("unused")
public class OnSelectedChangeAnimParent extends FrameLayout {

    public OnSelectedChangeAnimParent(@NonNull Context context) {
        super(context);
    }

    public OnSelectedChangeAnimParent(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public OnSelectedChangeAnimParent(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * beginAnimationTranslator
     *
     * @param animationView the view which your wanna execute animations;
     * @param animDuration  how long  with animation duration;
     * @param listener      onAnimation fraction update listener;
     */
    public void initData(View animationView, long animDuration, OnAnimListener listener) {
        animator = new DrawableValueAnimator(animationView);
        animator.setDuration(animDuration);
        animator.setOnAnimListener(listener);
    }

    private DrawableValueAnimator animator;

    private boolean isSelected = false;

    @Override
    public boolean isSelected() {
        return isSelected;
    }

    @Override
    public void setSelected(boolean selected) {
        if (isSelected() == selected) return;
        isSelected = selected;
        onSelectedChange();
        invalidate();
    }

    private void onSelectedChange() {
        if (animator == null) {
            throw new NullPointerException("valueAnimator was null reference to start ,has you called initData() before change selected state?");
        }
        animator.start(isSelected());
    }

    private static class DrawableValueAnimator {

        DrawableValueAnimator(View animationView) {
            this.animationView = animationView;
        }

        private OnAnimListener onAnimListener;
        private ValueAnimator valueAnimator;

        private long curDuration;
        private float curFraction;
        private View animationView;

        private long maxDuration;
        private float maxFraction;
        private long animDuration;
        private boolean isSelected;

        void setDuration(long duration) {
            this.animDuration = duration;
        }

        void setOnAnimListener(OnAnimListener listener) {
            this.onAnimListener = listener;
        }

        void start(boolean isSelected) {
            this.isSelected = isSelected;
            boolean isRunning = valueAnimator != null && valueAnimator.isRunning();
            if (valueAnimator != null) valueAnimator.cancel();
            if (valueAnimator == null || !valueAnimator.isRunning()) {
                valueAnimator = getAnim(isRunning);
                valueAnimator.start();
                return;
            }

            valueAnimator = getAnim(true);
            valueAnimator.start();
        }

        private ValueAnimator getAnim(boolean isRunning) {
            maxFraction = isRunning ? curFraction : 1.0f;
            maxDuration = isRunning ? curDuration : animDuration;
            ValueAnimator animator = new ValueAnimator();
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            setValues(animator);
            setListener(animator);
            return animator;
        }

        private void setValues(ValueAnimator animator) {
            curFraction = 0;
            curDuration = 0;
            animator.setDuration(maxDuration);
            animator.setFloatValues(0.0f, maxFraction);
        }

        private void setListener(ValueAnimator animator) {
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    curDuration = Math.min(animDuration, animation.getCurrentPlayTime());
                    curFraction = isSelected ? animation.getAnimatedFraction() : Math.max(0, maxFraction - animation.getAnimatedFraction());
                    if (onAnimListener != null) onAnimListener.onAnimFraction(animationView, curFraction);
                }
            });
        }
    }

    public interface OnAnimListener {
        void onAnimFraction(View animationView, float fraction);
    }
}
