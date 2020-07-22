package com.zj.views;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

import static com.zj.views.ut.DPUtils.dp2px;

/**
 * Created by ZJJ on 2018/7/13.
 * <p>
 * Enhanced textDrawableView, text supports one line for the time being, you can customize the picture, customize the color / picture conversion in two selection modes
 * <p>
 * {@link #initData} (float curAnimationFraction);
 * Supports custom size of pictures, supports transformation, supports relative positions such as up, down, left, right, etc.
 * According to the setting of DrawableTextView.initData (0.0f / 1.0f), instant switching between two states can be realized;
 * Can also be used for gradient switching with attribute animation;
 *
 * @version v1.0.1 Badge supported , now you can set a badge in DrawableTextView, and set a String text with {@link #setBadgeText}
 * also you should declared the badge attrs in your xml file , the property 'badgeEnable' is required . you can set the badges text color/size ,
 * background ,padding ,margin ,minWidth ,minHeight . as always , it supported to set a Gravity for badges.
 */

@SuppressWarnings("unused")
public class DrawableTextView extends View {

    private float drawableWidth = dp2px(20);
    private float drawableHeight = dp2px(20);
    private int translationTime = 300;
    @Nullable
    private Drawable replaceDrawable, selectedDrawable, badgeBackground;
    private int orientation;
    private int gravity, badgeGravity;
    private float paddingLeft = 0.0f, paddingTop = 0.0f, paddingRight = 0.0f, paddingBottom = 0.0f;
    private float drawablePadding = 0.0f;
    @Nullable
    private String text, badgeText;
    private float textSize = dp2px(12);
    private int textColor = Color.GRAY;
    private int textColorSelect = Color.BLACK;
    private float viewWidth, viewHeight, layoutWidth, layoutHeight, badgeMinWidth, badgeMinHeight;

    private boolean badgeEnable = false;
    private int badgeTextColor = Color.BLACK;
    private float badgeTextSize = 0;
    private float badgePadding = 0.0f;
    private float badgeMarginStart = 0.0f;
    private float badgeMarginEnd = 0.0f;
    private float badgeMarginTop = 0.0f;
    private float badgeMarginBottom = 0.0f;

    private Paint textPaint;
    private Paint badgeTextPaint;

    @Target(ElementType.PARAMETER)
    public @interface Orientation {
        int left = 0;
        int top = 1;
        int right = 2;
        int bottom = 3;
    }

    @Target(ElementType.PARAMETER)
    public @interface Gravity {
        int left = 0x0002;
        int top = 0x0004;
        int right = 0x0008;
        int bottom = 0x0010;
        int center = 0x0001;
    }

    public DrawableTextView(Context context) {
        this(context, null, 0);
    }

    public DrawableTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DrawableTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.DrawableTextView);
            try {
                layoutWidth = ta.getDimension(R.styleable.DrawableTextView_viewWidth, 0f);
                layoutHeight = ta.getDimension(R.styleable.DrawableTextView_viewHeight, 0f);
                drawableWidth = ta.getDimension(R.styleable.DrawableTextView_drawableWidth, drawableWidth);
                drawableHeight = ta.getDimension(R.styleable.DrawableTextView_drawableHeight, drawableHeight);
                float padding = ta.getDimension(R.styleable.DrawableTextView_padding, 0f);
                paddingLeft = ta.getDimension(R.styleable.DrawableTextView_paddingLeft, padding);
                paddingRight = ta.getDimension(R.styleable.DrawableTextView_paddingRight, padding);
                paddingBottom = ta.getDimension(R.styleable.DrawableTextView_paddingBottom, padding);
                paddingTop = ta.getDimension(R.styleable.DrawableTextView_paddingTop, padding);
                drawablePadding = ta.getDimension(R.styleable.DrawableTextView_drawablePadding, drawablePadding);
                replaceDrawable = ta.getDrawable(R.styleable.DrawableTextView_replaceDrawable);
                selectedDrawable = ta.getDrawable(R.styleable.DrawableTextView_selectedDrawable);
                text = ta.getString(R.styleable.DrawableTextView_text);
                textSize = ta.getDimension(R.styleable.DrawableTextView_textSize, textSize);
                textColor = ta.getColor(R.styleable.DrawableTextView_textColor, textColor);
                textColorSelect = ta.getColor(R.styleable.DrawableTextView_textColorSelect, textColorSelect);
                translationTime = ta.getInteger(R.styleable.DrawableTextView_translationTime, translationTime);
                orientation = ta.getInt(R.styleable.DrawableTextView_orientation, Orientation.left);
                gravity = ta.getInt(R.styleable.DrawableTextView_gravity, Gravity.center);
                badgeEnable = ta.getBoolean(R.styleable.DrawableTextView_badgeEnable, badgeEnable);
                if (badgeEnable) {
                    badgeText = ta.getString(R.styleable.DrawableTextView_badgeText);
                    badgeBackground = ta.getDrawable(R.styleable.DrawableTextView_badgeBackground);
                    badgeTextColor = ta.getColor(R.styleable.DrawableTextView_badgeTextColor, badgeTextColor);
                    badgeTextSize = ta.getDimension(R.styleable.DrawableTextView_badgeTextSize, badgeTextSize);
                    badgePadding = ta.getDimension(R.styleable.DrawableTextView_badgePadding, badgePadding);
                    badgeGravity = ta.getInt(R.styleable.DrawableTextView_badgeInGravity, Gravity.center);
                    float badgeMargin = ta.getDimension(R.styleable.DrawableTextView_badgeMargin, 0f);
                    badgeMinWidth = ta.getDimension(R.styleable.DrawableTextView_badgeMinWidth, 0f);
                    badgeMinHeight = ta.getDimension(R.styleable.DrawableTextView_badgeMinHeight, 0f);
                    badgeMarginStart = ta.getDimension(R.styleable.DrawableTextView_badgeMarginStart, badgeMargin);
                    badgeMarginEnd = ta.getDimension(R.styleable.DrawableTextView_badgeMarginEnd, badgeMargin);
                    badgeMarginTop = ta.getDimension(R.styleable.DrawableTextView_badgeMarginTop, badgeMargin);
                    badgeMarginBottom = ta.getDimension(R.styleable.DrawableTextView_badgeMarginBottom, badgeMargin);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                ta.recycle();
            }
        }
        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(textSize);
        textPaint.setTextAlign(Paint.Align.CENTER);
        if (badgeEnable) {
            badgeTextPaint = new Paint();
            badgeTextPaint.setAntiAlias(true);
            badgeTextPaint.setTextSize(badgeTextSize);
            badgeTextPaint.setColor(badgeTextColor);
            badgeTextPaint.setTextAlign(Paint.Align.CENTER);
        }
        calculationAll();
    }

    private PointF textStart, badgeTextStart;
    private Rect drawableRect, badgeRect;

    private void calculationAll() {
        calculateViewDimension();
        calculateGravityBounds();
        calculateBadgeBounds();
    }

    private void calculateViewDimension() {
        float textWidth;
        float textHeight;
        if (TextUtils.isEmpty(text)) {
            textWidth = 0;
            textHeight = 0;
        } else {
            textWidth = textPaint.measureText(text);
            Paint.FontMetrics metrics = textPaint.getFontMetrics();
            textHeight = metrics.descent - metrics.ascent;
        }
        float vw, vh;
        if (orientation == Orientation.top || orientation == Orientation.bottom) {
            vw = Math.max(textWidth, drawableWidth) + paddingLeft + paddingRight;
            vh = textHeight + drawableHeight + paddingTop + paddingBottom + drawablePadding;
        } else {
            vh = Math.max(textHeight, drawableHeight) + paddingTop + paddingBottom;
            vw = textWidth + drawableWidth + paddingLeft + paddingRight + drawablePadding;
        }
        Paint.FontMetrics metrics = textPaint.getFontMetrics();
        final boolean badgeDisabled = !badgeEnable || TextUtils.isEmpty(badgeText);
        final float badgeTextHalfHeight = badgeDisabled ? 0 : Math.max(badgeMinHeight, metrics.descent - metrics.ascent) / 2f;
        final float badgeTextHalfWidth = badgeDisabled ? 0 : Math.max(badgeMinWidth, badgeTextPaint.measureText(badgeText)) / 2f;
        final boolean isAlignBottom = !badgeDisabled && (badgeGravity & Gravity.bottom) != 0;
        final boolean isAlignRight = !badgeDisabled && (badgeGravity & Gravity.right) != 0;
        final float bml = badgeDisabled ? 0 : isAlignRight ? (badgeTextHalfWidth - badgeMarginStart > vh ? badgeTextHalfWidth - badgeMarginStart - vh : 0f) : (badgeMarginStart > 0 ? 0 : Math.abs(badgeMarginStart));
        final float bmr = badgeDisabled ? 0 : isAlignRight ? (badgeMarginEnd < 0 ? 0 : badgeMarginEnd) : (badgeMarginEnd + badgeTextHalfWidth > vh ? badgeMarginEnd + badgeTextHalfWidth - vh : 0f);
        final float bmt = badgeDisabled ? 0 : isAlignBottom ? (badgeTextHalfHeight - badgeMarginTop > vh ? badgeTextHalfHeight - badgeMarginTop - vh : 0f) : (badgeMarginTop > 0 ? 0 : Math.abs(badgeMarginTop));
        final float bmb = badgeDisabled ? 0 : isAlignBottom ? (badgeMarginBottom < 0 ? 0 : badgeMarginBottom) : (badgeMarginBottom + badgeTextHalfHeight > vh ? badgeMarginBottom + badgeTextHalfHeight - vh : 0f);
        viewWidth = vw + bml + bmr;
        viewHeight = vh + bmt + bmb;
        int drawableLeft = 0, drawableRight = 0, drawableTop = 0, drawableBottom = 0;
        float textX = 0, textY = 0;
        switch (orientation) {
            case Orientation.left:
                drawableLeft = (int) (paddingLeft + bml);
                drawableTop = (int) (viewHeight / 2.0f - drawableHeight / 2.0f + 0.5f);
                drawableRight = (int) (drawableLeft + drawableWidth);
                drawableBottom = (int) (drawableTop + drawableHeight);
                textX = drawableRight + drawablePadding + textWidth / 2f;
                textY = viewHeight / 2.0f + (textHeight / 2.0f - textPaint.getFontMetrics().descent);
                break;
            case Orientation.right:
                drawableLeft = (int) (paddingLeft + textWidth + drawablePadding + 0.5f);
                drawableTop = (int) (viewHeight / 2.0f - drawableHeight / 2.0f + 0.5f);
                drawableRight = (int) (drawableLeft + drawableWidth);
                drawableBottom = (int) (drawableTop + drawableHeight);
                textX = paddingLeft + textWidth / 2f;
                textY = viewHeight / 2.0f + (textHeight / 2.0f - textPaint.getFontMetrics().descent);
                break;
            case Orientation.top:
                drawableLeft = (int) (viewWidth / 2.0f - drawableWidth / 2.0f + 0.5f);
                drawableTop = (int) (paddingTop + bmt);
                drawableRight = (int) (drawableLeft + drawableWidth);
                drawableBottom = (int) (drawableTop + drawableHeight);
                textX = viewWidth / 2f;
                textY = drawableBottom + drawablePadding + (textHeight - textPaint.getFontMetrics().descent);
                break;
            case Orientation.bottom:
                drawableLeft = (int) (viewWidth / 2.0f - drawableWidth / 2.0f + 0.5f);
                drawableTop = (int) (paddingTop + textHeight + drawablePadding);
                drawableRight = (int) (drawableLeft + drawableWidth);
                drawableBottom = (int) (drawableTop + drawableHeight);
                textX = viewWidth / 2f;
                textY = paddingTop + (textHeight - textPaint.getFontMetrics().descent);
                break;
        }
        drawableRect = new Rect(drawableLeft, drawableTop, drawableRight, drawableBottom);
        textStart = new PointF(textX, textY);
    }

    private void calculateGravityBounds() {
        float widthOffset = layoutWidth - viewWidth;
        float heightOffset = layoutHeight - viewHeight;
        if (widthOffset <= 0 && heightOffset <= 0) return;

        if ((gravity & Gravity.left) != 0) {
            widthOffset = 0;
            if ((gravity & Gravity.center) != 0) {
                textStart.y += heightOffset / 2f;
                drawableRect.top += heightOffset / 2f;
                drawableRect.bottom += heightOffset / 2f;
                return;
            }
        }
        if ((gravity & Gravity.top) != 0) {
            heightOffset = 0;
        }
        boolean isCenterXSet = false;
        boolean isCenterYSet = false;
        if ((gravity & Gravity.center) != 0) {
            if (widthOffset > 0) {
                isCenterXSet = true;
                textStart.x += widthOffset / 2f;
                drawableRect.left += widthOffset / 2f;
                drawableRect.right += widthOffset / 2f;
            }
            if (heightOffset > 0) {
                isCenterYSet = true;
                textStart.y += heightOffset / 2f;
                drawableRect.top += heightOffset / 2f;
                drawableRect.bottom += heightOffset / 2f;
            }
        }
        if ((gravity & Gravity.right) != 0) {
            float xo = isCenterXSet ? widthOffset / 2f : widthOffset;
            textStart.x += xo;
            drawableRect.left += xo;
            drawableRect.right += xo;
        }
        if ((gravity & Gravity.bottom) != 0) {
            float yo = isCenterYSet ? heightOffset / 2f : heightOffset;
            textStart.y += yo;
            drawableRect.top += yo;
            drawableRect.bottom += yo;
        }
    }

    private void calculateBadgeBounds() {
        if (!badgeEnable || TextUtils.isEmpty(badgeText)) return;
        Paint.FontMetrics metrics = badgeTextPaint.getFontMetrics();
        final float textHeight = metrics.descent - metrics.ascent;
        final float textWidth = badgeTextPaint.measureText(badgeText);
        float badgeWidth = Math.max(badgeMinWidth, textWidth) + badgePadding * 2f;
        float badgeHeight = Math.max(badgeMinHeight, textHeight) + badgePadding * 2f;
        float left = paddingLeft, top = paddingTop;
        float widthOffset = Math.max(viewWidth, layoutWidth) - paddingLeft - paddingRight;
        float heightOffset = Math.max(viewHeight, layoutHeight) - paddingTop - paddingBottom;
        try {
            if ((badgeGravity & Gravity.left) != 0) {
                widthOffset = 0;
                if ((badgeGravity & Gravity.center) != 0) {
                    top += heightOffset / 2f - badgeHeight / 2f;
                    return;
                }
            }
            if ((badgeGravity & Gravity.top) != 0) {
                heightOffset = 0;
                if ((badgeGravity & Gravity.center) != 0) {
                    left += widthOffset / 2f - badgeWidth / 2f;
                    return;
                }
            }
            boolean isCenterXSet = false;
            boolean isCenterYSet = false;
            if ((badgeGravity & Gravity.center) != 0) {
                isCenterXSet = true;
                isCenterYSet = true;
                left += Math.max(0, widthOffset / 2f - badgeWidth / 2f);
                top += Math.max(0, heightOffset / 2f - badgeHeight / 2f);
            }
            if ((badgeGravity & Gravity.right) != 0) {
                float xo = isCenterXSet ? widthOffset / 2f - badgeWidth / 2f : widthOffset - badgeWidth;
                left += xo;
            }
            if ((badgeGravity & Gravity.bottom) != 0) {
                float yo = isCenterYSet ? heightOffset / 2f - badgeHeight / 2f : heightOffset - badgeHeight;
                top += yo;
            }
        } finally {
            int l = (int) (left + 0.5f);
            int r = (int) (left + badgeWidth + 0.5f);
            int t = (int) (top + 0.5f);
            int b = (int) (top + badgeHeight + 0.5f);
            badgeRect = new Rect(l, t, r, b);
            float textX = badgeRect.centerX();
            Paint.FontMetrics m = badgeTextPaint.getFontMetrics();
            float textY = badgeRect.centerY() - (m.bottom - m.top) / 2f - m.top;
            badgeTextStart = new PointF(textX, textY);
        }
    }

    private float curAnimFraction;

    //Use the initData method to generate a gradient animation based on the value of a property state. It is recommended to use it with OnSelectedAnimParent;
    public void initData(float curAnimationFraction) {
        this.curAnimFraction = curAnimationFraction;
        postInvalidate();
    }

    //Check that is sure you`re set the attrs property [badgeEnable = true] ,else it`ll never working;
    public void setBadgeText(String text) {
        if (!badgeEnable) throw new IllegalStateException("please check the attrs property [badgeEnable = true]");
        badgeText = text;
        postInvalidate();
    }

    public void clearBadgeText() {
        badgeText = "";
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        calculationAll();
        drawText(canvas);
        drawDrawable(canvas);
        drawBadge(canvas);
        canvas.restore();
    }

    private void drawText(Canvas canvas) {
        if (TextUtils.isEmpty(text)) return;
        ArgbEvaluator evaluator = new ArgbEvaluator();
        int evaTextColor = (int) evaluator.evaluate(curAnimFraction, textColor, textColorSelect);
        textPaint.setColor(evaTextColor);
        canvas.drawText(text, textStart.x, textStart.y, textPaint);
    }

    private void drawDrawable(Canvas canvas) {
        if (selectedDrawable == null || replaceDrawable == null) {
            if (selectedDrawable != null) {
                selectedDrawable.setBounds(drawableRect);
                selectedDrawable.setAlpha(255);
                selectedDrawable.draw(canvas);
            }
            if (replaceDrawable != null) {
                replaceDrawable.setAlpha(255);
                replaceDrawable.setBounds(drawableRect);
                replaceDrawable.draw(canvas);
            }
            return;
        }
        replaceDrawable.setBounds(drawableRect);
        selectedDrawable.setBounds(drawableRect);
        int curAlpha = (int) (curAnimFraction * 255f + 0.5f);
        replaceDrawable.setAlpha(255 - curAlpha);
        selectedDrawable.setAlpha(curAlpha);
        if (curAlpha > 0) {
            selectedDrawable.draw(canvas);
        }
        if (curAlpha < 255) {
            replaceDrawable.draw(canvas);
        }
    }

    private void drawBadge(Canvas canvas) {
        if (!badgeEnable || TextUtils.isEmpty(badgeText)) return;
        if (badgeBackground != null) {
            badgeBackground.setBounds(badgeRect);
            badgeBackground.draw(canvas);
        }
        canvas.drawText(badgeText, badgeTextStart.x, badgeTextStart.y, badgeTextPaint);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        float w = Math.max(layoutWidth, viewWidth);
        float h = Math.max(layoutHeight, viewHeight);
        setMeasuredDimension((int) w, (int) h);
    }

    public void setOrientation(@Orientation int orientation) {
        this.orientation = orientation;
        postInvalidate();
    }
}
