package com.zj.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.zj.views.DPUtils.sp2px;

/**
 * Created by ZJJ on 2018/5/3.
 */

@SuppressWarnings("unused")
public class SimpleCusLayoutView extends View {

    private Drawable drawableRes;
    private String text = "";
    private int textColor;
    private int drawableColor;
    private float width;
    private float height;
    private float padding = 0;
    private float textSize;

    private Paint mPaint;

    public SimpleCusLayoutView(Context context) {
        this(context, null, 0);
    }

    public SimpleCusLayoutView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SimpleCusLayoutView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    @TargetApi(LOLLIPOP)
    public SimpleCusLayoutView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SimpleCusLayoutView);
            try {
                drawableRes = ta.getDrawable(R.styleable.SimpleCusLayoutView_vDrawable);
                text = ta.getString(R.styleable.SimpleCusLayoutView_vText);
                width = ta.getDimension(R.styleable.SimpleCusLayoutView_vWidth, WRAP_CONTENT);
                height = ta.getDimension(R.styleable.SimpleCusLayoutView_vHeight, WRAP_CONTENT);
                padding = ta.getDimension(R.styleable.SimpleCusLayoutView_vPadding, 0f);
                textSize = ta.getDimension(R.styleable.SimpleCusLayoutView_vTextSize, 0f);
                textColor = ta.getColor(R.styleable.SimpleCusLayoutView_vTextColor, Color.BLACK);
                drawableColor = ta.getColor(R.styleable.SimpleCusLayoutView_vDrawableColor, -1);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                ta.recycle();
            }
        }
        mPaint = new Paint();
        initPaint();
        measureRect();
        invalidate();
    }

    private void initPaint() {
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(textColor);
        mPaint.setTextSize(textSize);
        mPaint.setTextAlign(Paint.Align.CENTER);
    }

    public SimpleCusLayoutView setDrawableRes(int drawableRes) {
        if (drawableRes > 0) {
            this.drawableRes = ContextCompat.getDrawable(getContext(), drawableRes);
            this.text = "";
        } else {
            this.drawableRes = null;
            this.text = "";
        }
        return this;
    }

    public SimpleCusLayoutView setDrawableRes(Drawable drawableRes) {
        if (drawableRes != null) {
            this.drawableRes = drawableRes;
            this.text = "";
        }
        return this;
    }

    public SimpleCusLayoutView setText(String txt) {
        if (!TextUtils.isEmpty(txt)) {
            this.text = txt;
            this.drawableRes = null;
        }
        return this;
    }

    public SimpleCusLayoutView setTextColor(int textColor) {
        this.textColor = textColor;
        return this;
    }

    public SimpleCusLayoutView setDrawableColor(int color) {
        this.drawableColor = color;
        return this;
    }

    public SimpleCusLayoutView setWidth(float width) {
        if (width > 0)
            this.width = dp2px(width);
        return this;
    }

    public SimpleCusLayoutView setHeight(float height) {
        if (height > 0)
            this.height = dp2px(height);
        return this;
    }

    public SimpleCusLayoutView setPadding(float padding) {
        if (padding > 0)
            this.padding = dp2px(padding);
        return this;
    }

    public SimpleCusLayoutView setTextSize(float textSize) {
        if (textSize > 0)
            this.textSize = sp2px(textSize);
        return this;
    }

    /**
     * refresh
     */
    public void draw() {
        initPaint();
        measureRect();
        requestLayout();
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        if (!TextUtils.isEmpty(text)) {
            drawTxt(canvas);
        } else if (drawableRes != null) {
            drawImg(canvas);
        }
        canvas.restore();
    }

    private void drawImg(Canvas canvas) {
        Drawable drawable = tintDrawable(drawableRes, drawableColor);
        drawable.setBounds((int) padding, (int) padding, (int) (width + padding), (int) (height + padding));
        drawable.draw(canvas);
    }

    private void drawTxt(Canvas canvas) {
        Paint.FontMetrics metrics = mPaint.getFontMetrics();
        float textHeight = Math.abs(metrics.top);
        canvas.drawText(text, padding + width / 2f, padding + textHeight, mPaint);
    }

    private void measureRect() {
        if (!TextUtils.isEmpty(text)) {
            width = mPaint.measureText(text);
        } else if (width <= 0 && drawableRes != null) {
            width = drawableRes.getIntrinsicWidth();
        }
        if (!TextUtils.isEmpty(text)) {
            height = Math.abs(mPaint.getFontMetrics().top) + mPaint.getFontMetrics().bottom;
        } else if (width <= 0 && drawableRes != null) {
            height = drawableRes.getIntrinsicHeight();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (width > 0 && height > 0)
            setMeasuredDimension((int) (width + padding * 2f + 0.5f), (int) (height + padding * 2f + 0.5f));
    }

    public Drawable tintDrawable(Drawable d, int tintColor) {
        if (d == null) {
            return null;
        }
        if (tintColor < 0) return d;
        if (d.getConstantState() != null) {
            Drawable drawable = d.getConstantState().newDrawable();
            ColorFilter filter = new PorterDuffColorFilter(tintColor, PorterDuff.Mode.SRC_IN);
            drawable.setColorFilter(filter);
            return drawable;
        }
        return d;
    }

    private int dp2px(float dpValue) {
        return (int) (0.5f + dpValue * Resources.getSystem().getDisplayMetrics().density);
    }
}
