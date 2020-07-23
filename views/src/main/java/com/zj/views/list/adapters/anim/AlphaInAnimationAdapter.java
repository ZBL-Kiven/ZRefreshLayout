package com.zj.views.list.adapters.anim;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.view.View;

import com.zj.views.list.adapters.AnimationAdapter;

import java.util.List;


@SuppressWarnings("unused")
public abstract class AlphaInAnimationAdapter<T> extends AnimationAdapter<T> {

    private final float mFrom;

    public AlphaInAnimationAdapter(int id, List<T> data, float mFrom) {
        super(id, data);
        this.mFrom = mFrom;
    }

    @Override
    protected Animator[] getAnimators(View view) {
        return new Animator[]{ObjectAnimator.ofFloat(view, "alpha", mFrom, 1f)};
    }
}
