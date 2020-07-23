package com.zj.views.list.adapters.anim;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.view.View;

import com.zj.views.list.adapters.AnimationAdapter;

import java.util.List;


@SuppressWarnings("unused")
public abstract class ScaleInAnimationAdapter<T> extends AnimationAdapter<T> {

    private final float mFrom;

    public ScaleInAnimationAdapter(int id, List<T> data, float mFrom) {
        super(id, data);
        this.mFrom = mFrom;
    }

    @Override
    protected Animator[] getAnimators(View view) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", mFrom, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", mFrom, 1f);
        return new ObjectAnimator[]{scaleX, scaleY};
    }
}
