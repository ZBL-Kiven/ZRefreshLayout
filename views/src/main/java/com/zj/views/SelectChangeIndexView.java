package com.zj.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


/**
 * Created by ZJJ on 2018/7/17.
 */

@SuppressWarnings("unused")
public class SelectChangeIndexView extends OnSelectedChangeAnimParent {
    public SelectChangeIndexView(@NonNull Context context) {
        this(context, null, 0);
    }

    public SelectChangeIndexView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SelectChangeIndexView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    public void init() {
        if (getChildCount() == 1 && getChildAt(0) instanceof DrawableTextView) initData(getChildAt(0), 300, new OnAnimListener() {
            @Override
            public void onAnimFraction(View animationView, float fraction) {
                ((DrawableTextView) SelectChangeIndexView.this.getChildAt(0)).initData(fraction);
            }
        });
    }
}
