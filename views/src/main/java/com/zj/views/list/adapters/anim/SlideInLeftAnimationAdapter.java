package com.zj.views.list.adapters.anim;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.view.View;

import com.zj.views.list.adapters.AnimationAdapter;

import java.util.List;


@SuppressWarnings("unused")
public abstract class SlideInLeftAnimationAdapter<T> extends AnimationAdapter<T> {

    public SlideInLeftAnimationAdapter(int id, List<T> data) {
        super(id, data);
    }

    @Override
    protected Animator[] getAnimators(View view) {
        return new Animator[]{ObjectAnimator.ofFloat(view, "translationX", -view.getRootView().getWidth(), 0)};
    }
}
