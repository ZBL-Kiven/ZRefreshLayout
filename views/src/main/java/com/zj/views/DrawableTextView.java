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
import static com.zj.views.DPUtils.dp2px;

/**
 * Created by ZJJ on 2018/7/13.
 * <p>
 * Enhanced textDrawableView, text supports one line for the time being, you can customize the picture, customize the color / picture conversion in two selection modes
 *
 * @see DrawableTextView .initData (float curAnimationFraction);
 * Supports custom size of pictures, supports transformation, supports relative positions such as up, down, left, right, etc.
 * According to the setting of DrawableTextView.initData (0.0f / 1.0f), instant switching between two states can be realized;
 * Can also be used for gradient switching with attribute animation;
 */

@SuppressWarnings("unused")
public class DrawableTextView extends View {

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
        int bottom = 0x0016;
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

    private float drawableWidth = dp2px(20);
    private float drawableHeight = dp2px(20);
    private int translationTime = 300;
    private Drawable replaceDrawable;
    private Drawable selectedDrawable;
    private int orientation;
    private int gravity;
    private float padding;
    private float paddingLeft = 0.0f, paddingTop = 0.0f, paddingRight = 0.0f, paddingBottom = 0.0f;
    private float drawablePadding = 0.0f;
    private String text;
    private float textSize = dp2px(12);
    private int textColor = Color.GRAY;
    private int textColor_select = Color.BLACK;
    private float viewWidth, viewHeight, layoutWidth, layoutHeight;

    private Paint textPaint;

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.DrawableTextView);
            try {
                layoutWidth = ta.getDimension(R.styleable.DrawableTextView_viewWidth, 0f);
                layoutHeight = ta.getDimension(R.styleable.DrawableTextView_viewHeight, 0f);
                drawableWidth = ta.getDimension(R.styleable.DrawableTextView_drawableWidth, drawableWidth);
                drawableHeight = ta.getDimension(R.styleable.DrawableTextView_drawableHeight, drawableHeight);
                padding = ta.getDimension(R.styleable.DrawableTextView_padding, padding);
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
                textColor_select = ta.getColor(R.styleable.DrawableTextView_textColor_select, textColor_select);
                translationTime = ta.getInteger(R.styleable.DrawableTextView_translationTime, translationTime);
                orientation = ta.getInt(R.styleable.DrawableTextView_orientation, Orientation.left);
                gravity = ta.getInt(R.styleable.DrawableTextView_gravity, Gravity.center);
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
        calculateViewDimension();
        calculateGravityBounds();
    }

    private PointF textStart;
    private Rect drawableRect;

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
        if (orientation == Orientation.top || orientation == Orientation.bottom) {
            //vertical
            viewWidth = Math.max(textWidth, drawableWidth) + paddingLeft + paddingRight;
            viewHeight = textHeight + drawableHeight + paddingTop + paddingBottom + drawablePadding;
        } else {
            //horizontal
            viewHeight = Math.max(textHeight, drawableHeight) + paddingTop + paddingBottom;
            viewWidth = textWidth + drawableWidth + paddingLeft + paddingRight + drawablePadding;
        }
        int drawableLeft = 0, drawableRight = 0, drawableTop = 0, drawableBottom = 0;
        float textX = 0, textY = 0;
        switch (orientation) {
            case Orientation.left:
                drawableLeft = (int) paddingLeft;
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
                drawableTop = (int) paddingTop;
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

    private float curAnimFraction;

    //Use the initData method to generate a gradient animation based on the value of a property state. It is recommended to use it with OnSelectedAnimParent;
    public void initData(float curAnimationFraction) {
        this.curAnimFraction = curAnimationFraction;
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        calculateViewDimension();
        calculateGravityBounds();
        drawText(canvas);
        drawDrawable(canvas);
        canvas.restore();
    }

    private void drawText(Canvas canvas) {
        ArgbEvaluator evaluator = new ArgbEvaluator();
        int evaTextColor = (int) evaluator.evaluate(curAnimFraction, textColor, textColor_select);
        textPaint.setColor(evaTextColor);
        canvas.drawText(text, textStart.x, textStart.y, textPaint);
    }

    private void drawDrawable(Canvas canvas) {
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
