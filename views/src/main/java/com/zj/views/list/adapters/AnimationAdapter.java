package com.zj.views.list.adapters;

import android.animation.Animator;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;

import com.zj.views.list.ViewHelper;
import com.zj.views.list.holders.BaseViewHolder;

import java.util.List;

/**
 * create by ZJJ on 18/6/1
 * <p>
 * if you need an animation with your list data loading,
 * use this adapter and build the method getAnimators();
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public abstract class AnimationAdapter<T> extends BaseAdapter<T> {

    private int mDuration = 300;
    private Interpolator mInterpolator = new LinearInterpolator();
    private int mLastPosition = -1;
    private boolean isFirstOnly = true;

    public AnimationAdapter(@LayoutRes int id) {
        super(id);
    }

    public AnimationAdapter(@LayoutRes int id, List<T> data) {
        super(id, data);
    }

    public AnimationAdapter(View view) {
        super(view);
    }

    public AnimationAdapter(View view, List<T> data) {
        super(view, data);
    }

    public AnimationAdapter(ViewBuilder builder) {
        super(builder);
    }

    public AnimationAdapter(ViewBuilder builder, List<T> data) {
        super(builder, data);
    }

    public AnimationAdapter(LayoutBuilder builder) {
        super(builder);
    }

    public AnimationAdapter(LayoutBuilder builder, List<T> data) {
        super(builder, data);
    }

    @Override
    protected final void initData(BaseViewHolder<T> holder, int position, @Nullable T module, List<Object> payloads) {
        int adapterPosition = holder.getAbsoluteAdapterPosition();
        bindData(holder, position, module, payloads);
        if ((!isFirstOnly || adapterPosition > mLastPosition) && (payloads == null || payloads.isEmpty())) {
            for (Animator anim : getAnimators(holder.itemView)) {
                anim.setInterpolator(mInterpolator);
                anim.setDuration(mDuration).start();
            }
            mLastPosition = adapterPosition;
        } else {
            ViewHelper.clear(holder.itemView);
        }
    }

    public void setDuration(int duration) {
        mDuration = duration;
    }

    public void setInterpolator(Interpolator interpolator) {
        mInterpolator = interpolator;
    }

    public void setStartPosition(int start) {
        mLastPosition = start;
    }

    protected abstract Animator[] getAnimators(View view);

    public abstract void bindData(BaseViewHolder<T> holder, int position, @Nullable T module, List<Object> payloads);

    public void setFirstOnly(boolean firstOnly) {
        isFirstOnly = firstOnly;
    }
}
