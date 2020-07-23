package com.zj.views.list.refresh.layout;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.zj.views.R;
import com.zj.views.list.refresh.layout.constant.SpinnerStyle;

public class RLLayoutParams extends ViewGroup.MarginLayoutParams {

    public RLLayoutParams(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RefreshLayout_Layout);
        backgroundColor = ta.getColor(R.styleable.RefreshLayout_Layout_layout_rlBackgroundColor, backgroundColor);
        if (ta.hasValue(R.styleable.RefreshLayout_Layout_layout_rlSpinnerStyle)) {
            spinnerStyle = SpinnerStyle.values[ta.getInt(R.styleable.RefreshLayout_Layout_layout_rlSpinnerStyle, SpinnerStyle.Translate.ordinal)];
        }
        ta.recycle();
    }
    public RLLayoutParams(int width, int height) {
        super(width, height);
    }
    public int backgroundColor = 0;
    public SpinnerStyle spinnerStyle = null;
}
