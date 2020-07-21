package com.zj.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.zj.views.ut.DPUtils.dp2px;

/**
 * Created by ZJJ on 2018/5/4.
 */

@SuppressWarnings("unused")
public class BaseTitleView extends FrameLayout {

    private Drawable l_drawableRes;
    private String l_text = "";
    private int l_textColor = Color.BLACK;
    private int l_drawableColor = -1;
    private float l_width;
    private float l_height;
    private float l_padding;
    private float l_textSize = dp2px(15);
    private Drawable r_drawableRes;
    private String r_text = "";
    private int r_textColor = Color.BLACK;
    private int r_drawableColor = -1;
    private float r_width;
    private float r_height;
    private float r_padding;
    private float r_textSize = dp2px(15);
    private String titleText = "";
    private float titleTextSize = 19;
    private int titleTextColor = Color.BLACK;

    public BaseTitleView(@NonNull Context context) {
        this(context, null, 0);
    }

    public BaseTitleView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseTitleView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    @TargetApi(LOLLIPOP)
    public BaseTitleView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr);
    }

    private View rootView;

    private SimpleCusLayoutView vLeft, vRight;
    private TextView tvTitle;

    @SuppressWarnings("unchecked")
    private <T extends View> T findById(int id) {
        return (T) rootView.findViewById(id);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.BaseTitleView);
            try {
                l_text = ta.getString(R.styleable.BaseTitleView_l_vText);
                l_width = ta.getFloat(R.styleable.BaseTitleView_l_vWidth, WRAP_CONTENT);
                l_height = ta.getFloat(R.styleable.BaseTitleView_l_vHeight, WRAP_CONTENT);
                l_padding = ta.getFloat(R.styleable.BaseTitleView_l_vPadding, l_padding);
                l_textSize = ta.getFloat(R.styleable.BaseTitleView_l_vTextSize, l_textSize);
                l_textColor = ta.getColor(R.styleable.BaseTitleView_l_vTextColor, l_textColor);
                l_drawableRes = ta.getDrawable(R.styleable.SimpleCusLayoutView_vDrawable);
                l_drawableColor = ta.getColor(R.styleable.BaseTitleView_l_vDrawableColor, l_drawableColor);
                r_text = ta.getString(R.styleable.BaseTitleView_r_vText);
                r_width = ta.getFloat(R.styleable.BaseTitleView_r_vWidth, WRAP_CONTENT);
                r_height = ta.getFloat(R.styleable.BaseTitleView_r_vHeight, WRAP_CONTENT);
                r_padding = ta.getFloat(R.styleable.BaseTitleView_r_vPadding, r_padding);
                r_textSize = ta.getFloat(R.styleable.BaseTitleView_r_vTextSize, r_textSize);
                r_textColor = ta.getColor(R.styleable.BaseTitleView_r_vTextColor, r_textColor);
                r_drawableRes = ta.getDrawable(R.styleable.BaseTitleView_r_vDrawable);
                r_drawableColor = ta.getColor(R.styleable.BaseTitleView_r_vDrawableColor, r_drawableColor);
                titleText = ta.getString(R.styleable.BaseTitleView_vTitle);
                titleTextSize = ta.getFloat(R.styleable.BaseTitleView_vTitleTextSize, titleTextSize);
                titleTextColor = ta.getColor(R.styleable.BaseTitleView_vTitleTextColor, titleTextColor);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                ta.recycle();
            }
        }
        initView(context);
    }

    private void initView(Context context) {
        rootView = inflate(context, R.layout.base_title_view, this);
        vLeft = findById(R.id.base_title_vLeft);
        vRight = findById(R.id.base_title_vRight);
        tvTitle = findById(R.id.base_title_tvTitle);
        setTitleColor(titleTextColor);
        setTitleTextSize(titleTextSize);
        setTitle(titleText);
        vLeft.setText(l_text).setDrawableRes(l_drawableRes).setTextColor(l_textColor).setDrawableColor(l_drawableColor).setWidth(l_width).setHeight(l_height).setPadding(l_padding).setTextSize(l_textSize).draw();
        vRight.setText(r_text).setDrawableRes(r_drawableRes).setTextColor(r_textColor).setDrawableColor(r_drawableColor).setWidth(r_width).setHeight(r_height).setPadding(r_padding).setTextSize(r_textSize).draw();
    }

    public void setLeftTxt(String txt) {
        vLeft.setText(txt).draw();
    }

    public void setRightTxt(String txt) {
        vRight.setText(txt).draw();
    }

    public void setLeftIcon(@DrawableRes int drawableRes) {
        vLeft.setDrawableRes(drawableRes).draw();
    }

    public void setRightIcon(@DrawableRes int drawableRes) {
        vRight.setDrawableRes(drawableRes).draw();
    }

    public void setLeftTextColor(int textColor) {
        vLeft.setTextColor(getColor(textColor)).draw();
    }

    public void setLeftDrawableColor(int drawableColor) {
        vLeft.setDrawableColor(getColor(drawableColor)).draw();
    }

    public void setLeftWidth(float width) {
        vLeft.setWidth(width).draw();
    }

    public void setLeftHeight(float height) {
        vLeft.setHeight(height).draw();
    }

    public void setLeftPadding(float padding) {
        vLeft.setPadding(padding).draw();
    }

    public void setLeftTextSize(float textSize) {
        vLeft.setTextSize(textSize).draw();
    }

    public void setRightTextColor(int textColor) {
        vRight.setTextColor(getColor(textColor)).draw();
    }

    public void setRightDrawableColor(int drawableColor) {
        vRight.setDrawableColor(getColor(drawableColor)).draw();
    }

    public void setRightWidth(float width) {
        vRight.setWidth(width).draw();
    }

    public void setRightHeight(float height) {
        vRight.setHeight(height).draw();
    }

    public void setRightPadding(float padding) {
        vRight.setPadding(padding).draw();
    }

    public void setRightTextSize(float textSize) {
        vRight.setTextSize(textSize).draw();
    }

    public void setTitle(String title) {
        tvTitle.setText(title);
    }

    public void setTitle(int titleId) {
        tvTitle.setText(getContext().getString(titleId));
    }

    public void setTitleTextSize(float dpSize) {
        tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, dpSize);
    }

    public void setTitleTextSizeRef(int ref) {
        tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(ref));
    }

    public void setTitleColor(int color) {
        tvTitle.setTextColor(getColor(color));
    }

    public void setLeftClickListener(OnClickListener l) {
        vLeft.setOnClickListener(l);
    }

    public void setLeftLongClickListener(OnLongClickListener l) {
        vLeft.setOnLongClickListener(l);
    }

    public void setRightClickListener(OnClickListener l) {
        vRight.setOnClickListener(l);
    }

    public void setRightLongClickListener(OnLongClickListener l) {
        vRight.setOnLongClickListener(l);
    }

    public void setLeftVisibility(boolean visibility) {
        vLeft.setVisibility(visibility ? VISIBLE : GONE);
    }

    public void setRightVisibility(boolean visibility) {
        vRight.setVisibility(visibility ? VISIBLE : GONE);
    }

    private int getColor(int c) {
        try {
            return ContextCompat.getColor(getContext(), c);
        } catch (Exception e) {
            return c;
        }
    }
}
