package com.zj.views.list.adapters.anim;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.view.View;

import com.zj.views.list.adapters.AnimationAdapter;

import java.util.List;


@SuppressWarnings("unused")
public abstract class SlideInBottomAnimationAdapter<T> extends AnimationAdapter<T> {

    public SlideInBottomAnimationAdapter(int id, List<T> data) {
        super(id, data);
    }

    @Override
    protected Animator[] getAnimators(View view) {
        return new Animator[]{ObjectAnimator.ofFloat(view, "translationY", view.getMeasuredHeight(), 0)};
    }
}
