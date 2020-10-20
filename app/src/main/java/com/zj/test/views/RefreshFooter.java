package com.zj.test.views;

import android.animation.TimeInterpolator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.ColorUtils;

import com.zj.views.list.refresh.layout.api.RefreshLayoutIn;
import com.zj.views.list.refresh.layout.constant.SpinnerStyle;
import com.zj.views.list.refresh.layout.simple.SimpleComponent;
import com.zj.views.ut.DPUtils;

public class RefreshFooter extends SimpleComponent implements com.zj.views.list.refresh.layout.api.RefreshFooter {

    protected boolean mManualNormalColor;
    protected boolean mManualAnimationColor;
    protected Paint mPaint;
    protected int mNormalColor = 0xffeeeeee;
    protected int mAnimatingColor = 0xffeea30f;
    protected float mCircleSpacing, curAlpha;
    protected long mStartTime = 0;
    protected boolean mIsStarted = false;
    protected TimeInterpolator mInterpolator = new AccelerateDecelerateInterpolator();

    public RefreshFooter(Context context) {
        this(context, null);
    }

    public RefreshFooter(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, 0);
        mPaint = new Paint();
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
        mSpinnerStyle = SpinnerStyle.Translate;
        mCircleSpacing = DPUtils.dp2px(12);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        final int width = getWidth();
        final int height = getHeight();
        float radius = (Math.min(width, height) - mCircleSpacing) / 6f;
        float x = width / 2f - (radius * 2 + mCircleSpacing);
        float y = height / 2f;
        mPaint.setAlpha((int) (curAlpha * 255f));
        final long now = System.currentTimeMillis();

        for (int i = 0; i < 3; i++) {

            long time = now - mStartTime - 120 * (i + 1);
            float percent = time > 0 ? ((time % 750) / 750f) : 0;
            percent = mInterpolator.getInterpolation(percent);

            canvas.save();
            float translateX = x + (radius * 2) * i + mCircleSpacing * i;
            canvas.translate(translateX, y);
            if (percent < 0.5) {
                float scale = 1 - percent * 2 * 0.7f;
                canvas.scale(scale, scale);
            } else {
                float scale = percent * 2 * 0.7f - 0.4f;
                canvas.scale(scale, scale);
            }
            canvas.drawCircle(0, 0, radius, mPaint);
            canvas.restore();
        }
        super.dispatchDraw(canvas);
        if (mIsStarted) {
            invalidate();
        }
    }

    @Override
    public void onMoving(boolean isDragging, float percent, int offset, int height, int maxDragHeight) {
        super.onMoving(isDragging, percent, offset, height, maxDragHeight);
        if (percent < 0.6f) curAlpha = 0;
        else curAlpha = Math.min(1, (percent - 0.6f) / 0.6f);
    }

    @Override
    public void onStartAnimator(@NonNull RefreshLayoutIn refreshLayout, int height, int maxDragHeight) {
        super.onStartAnimator(refreshLayout, height, maxDragHeight);
        if (mIsStarted) return;
        invalidate();
        mIsStarted = true;
        mStartTime = System.currentTimeMillis();
        mPaint.setColor(mAnimatingColor);
    }

    @Override
    public int onFinish(@NonNull RefreshLayoutIn layout, boolean success) {
        mIsStarted = false;
        mStartTime = 0;
        mPaint.setColor(mNormalColor);
        return 0;
    }

    @Override
    @Deprecated
    public void setPrimaryColors(@ColorInt int... colors) {
        if (!mManualAnimationColor && colors.length > 1) {
            setAnimatingColor(colors[0]);
            mManualAnimationColor = false;
        }
        if (!mManualNormalColor) {
            if (colors.length > 1) {
                setNormalColor(colors[1]);
            } else if (colors.length > 0) {
                setNormalColor(ColorUtils.compositeColors(0x99ffffff, colors[0]));
            }
            mManualNormalColor = false;
        }
    }

    public void setNormalColor(@ColorInt int color) {
        mNormalColor = color;
        mManualNormalColor = true;
        if (!mIsStarted) {
            mPaint.setColor(color);
        }
    }

    public void setAnimatingColor(@ColorInt int color) {
        mAnimatingColor = color;
        mManualAnimationColor = true;
        if (mIsStarted) {
            mPaint.setColor(color);
        }
    }
}
