package com.zj.viewMob.views;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.animation.CycleInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.zj.viewMob.R;
import com.zj.views.list.refresh.layout.api.RefreshKernel;
import com.zj.views.list.refresh.layout.api.RefreshLayoutIn;
import com.zj.views.list.refresh.layout.simple.SimpleComponent;
import com.zj.views.ut.DPUtils;

import java.lang.ref.WeakReference;
import java.util.Random;

public class RefreshHeader extends SimpleComponent implements com.zj.views.list.refresh.layout.api.RefreshHeader {

    private ImageView iv;
    private AnimationDrawable adEnter, adStanding;
    private boolean onAnimStarted = false, isReleaseMoving = false;
    private int animRepeatCount = 1;
    private ValueAnimator standingLookupAnim;
    private LoadingPop loadingPop;
    private Paint paint;

    private Context getCtx() {
        return new WeakReference<>(super.getContext()).get();
    }

    public RefreshHeader(Context context) {
        this(context, null, 0);
    }

    public RefreshHeader(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint = new Paint();
        paint.setAntiAlias(true);
        int textColor = Color.BLACK;
        paint.setColor(textColor);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
    }

    @Override
    public void onMoving(boolean isDragging, float percent, int offset, int height, int maxDragHeight) {
        if (onAnimStarted) return;
        if (!isReleaseMoving) {
            if (percent < 0.45f) return;
        } else {
            percent += 0.45f;
        }
        float absPercent = Math.min(Math.max(percent - 0.45f, 0f), 1.0f);
        iv.setScaleX(absPercent);
        iv.setScaleY(absPercent);
        int animCount = adEnter.getNumberOfFrames();
        int index = Math.min(animCount, Math.max(0, (int) (animCount * absPercent)));
        Drawable d = adEnter.getFrame(index);
        if (d != null) iv.setImageDrawable(d);
    }

    @Override
    public int onFinish(@NonNull RefreshLayoutIn refreshLayout, boolean success) {
        adStanding.stop();
        standingLookupAnim.cancel();
        iv.setImageDrawable(adEnter);
        onAnimStarted = false;
        isReleaseMoving = false;
        return super.onFinish(refreshLayout, success);
    }


    @Override
    public void onReleased(@NonNull RefreshLayoutIn refreshLayout, int height, int maxDragHeight) {
        super.onReleased(refreshLayout, height, maxDragHeight);
        isReleaseMoving = true;
        if (!refreshLayout.isLoading()) iv.setImageDrawable(adStanding);
    }


    @Override
    public void onStartAnimator(@NonNull RefreshLayoutIn refreshLayout, int height, int maxDragHeight) {
        super.onStartAnimator(refreshLayout, height, maxDragHeight);
        adStanding.start();
        standingLookupAnim.start();
        onAnimStarted = true;
    }

    @Override
    public void onInitialized(@NonNull RefreshKernel kernel, int height, int maxDragHeight) {
        super.onInitialized(kernel, height, maxDragHeight);
        if (getChildCount() != 0) {
            removeAllViews();
        }
        adEnter = (AnimationDrawable) getCtx().getDrawable(R.drawable.r_act_loading_enter_anim);
        adStanding = (AnimationDrawable) getCtx().getDrawable(R.drawable.r_act_loading_anim);
        int picSize = DPUtils.dp2px(50);
        LayoutParams lp = new LayoutParams(picSize, picSize);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        iv = new ImageView(getCtx());
        addView(iv, lp);
        standingLookupAnim = ValueAnimator.ofFloat(0.0f, 1.0f);
        standingLookupAnim.setDuration(1000);
        standingLookupAnim.setRepeatMode(ValueAnimator.REVERSE);
        standingLookupAnim.setRepeatCount(ValueAnimator.INFINITE);
        standingLookupAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                upDateAnimate(animation.getAnimatedFraction());
            }
        });
        standingLookupAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                loadingPop = new LoadingPop(getCtx().getDrawable(R.drawable.r_act_loading_pop), animRepeatCount % 2 != 0);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                iv.setRotationY(0);
                iv.setRotationX(0);
                animRepeatCount = 1;
                loadingPop.curFraction = 0;
                postInvalidate();
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                animRepeatCount++;
                loadingPop.setIsJump(animRepeatCount % 2 != 0);
            }
        });
    }

    private void upDateAnimate(float f) {
        boolean isJump = animRepeatCount % 2 != 0;
        CycleInterpolator ci = new CycleInterpolator(-0.5f);
        if (isJump) {
            float maxScale = 0.25f;
            float of = 1 + ci.getInterpolation(f) * maxScale;
            if (iv != null) {
                iv.setScaleY(of);
            }
        }
        if (loadingPop != null) loadingPop.curFraction = f;
        postInvalidate();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (iv != null && loadingPop != null && getWidth() != 0 && getHeight() != 0) {
            canvas.save();
            loadingPop.draw(canvas, paint, new PointF(getWidth() / 2f, getHeight() / 2f));
            canvas.restore();
        }
    }

    private static class LoadingPop {

        private float curFraction = 1.0f;
        private int maxSize;
        private final Drawable drawable;
        private int tx;
        private boolean isJump, isLeft;
        private float maxFontSize;
        private int curX, curY;

        LoadingPop(Drawable drawable, boolean isJump) {
            this.drawable = drawable;
            this.isJump = isJump;
            randomAssets();
        }

        private void setIsJump(boolean isJump) {
            this.isJump = isJump;
            if (isJump) randomAssets();
        }

        private void randomAssets() {
            Random r = new Random();
            isLeft = r.nextBoolean();
            this.maxSize = DPUtils.dp2px(r.nextInt(10) + 35);
            this.maxFontSize = maxSize / 5f;
            this.tx = DPUtils.dp2px((r.nextInt(40)) + (isLeft ? 1 : 0) * (maxSize / 2f) + 0.5f) + maxSize;
        }

        void draw(Canvas canvas, Paint paint, PointF center) {
            int alpha = isJump ? 255 : (int) (curFraction * 255);
            paint.setAlpha(alpha);
            int size = (int) (isJump ? curFraction * maxSize : maxSize);
            float fontSize = isJump ? curFraction * maxFontSize : maxFontSize;
            paint.setTextSize(fontSize);
            if (isJump) {
                float lcf = (float) (Math.cos((curFraction + 1) * Math.PI) / 2.0f) + 0.5f;
                curX = (int) ((isLeft ? -1 : 1) * lcf * tx + center.x);
                float yf = curFraction;
                float acf = yf * yf * ((2.0f + 1) * yf - 2.0f);
                curY = (int) (-acf * maxSize * 0.85f + center.y);
            }
            if (curX != 0 && curY != 0) {
                Rect r = new Rect(curX, curY, curX + size, curY + size);
                drawable.setAlpha(alpha);
                drawable.setBounds(r);
                drawable.draw(canvas);
                Paint.FontMetrics m = paint.getFontMetrics();
                float textY = r.centerY() - (m.bottom - m.top) / 2f - m.top;
                canvas.drawText("LOADING", r.centerX(), textY, paint);
            }
        }
    }
}
