package com.zj.views.list;

import android.view.View;

public final class ViewHelper {

    public static void clear(View v) {
        v.setAlpha(1);
        v.setScaleY(1);
        v.setScaleX(1);
        v.setTranslationY(0);
        v.setTranslationX(0);
        v.setRotation(0);
        v.setRotationY(0);
        v.setRotationX(0);
        v.setPivotY(v.getMeasuredHeight() / 2f);
        v.setPivotX(v.getMeasuredWidth() / 2f);
        v.animate().setInterpolator(null).setStartDelay(0);
    }
}
