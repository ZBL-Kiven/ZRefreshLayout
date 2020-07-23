package com.zj.views.list.holders.anim;

import android.view.animation.OvershootInterpolator;

import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.zj.views.list.holders.BaseItemAnimator;

@SuppressWarnings("unused")
public class OvershootInRightAnimator extends BaseItemAnimator {

  private final float mTension;

  public OvershootInRightAnimator() {
    mTension = 2.0f;
  }

  public OvershootInRightAnimator(float mTension) {
    this.mTension = mTension;
  }

  @Override protected void animateRemoveImpl(final RecyclerView.ViewHolder holder) {
    ViewCompat.animate(holder.itemView)
        .translationX(holder.itemView.getRootView().getWidth())
        .setDuration(getRemoveDuration())
        .setListener(new DefaultRemoveVpaListener(holder))
        .setStartDelay(getRemoveDelay(holder))
        .start();
  }

  @Override protected void preAnimateAddImpl(RecyclerView.ViewHolder holder) {
    holder.itemView.setTranslationX( holder.itemView.getRootView().getWidth());
  }

  @Override protected void animateAddImpl(final RecyclerView.ViewHolder holder) {
    ViewCompat.animate(holder.itemView)
        .translationX(0)
        .setDuration(getAddDuration())
        .setInterpolator(new OvershootInterpolator(mTension))
        .setListener(new DefaultAddVpaListener(holder))
        .setStartDelay(getAddDelay(holder))
        .start();
  }
}
