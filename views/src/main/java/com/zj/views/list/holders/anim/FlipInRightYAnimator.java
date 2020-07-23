package com.zj.views.list.holders.anim;

import android.view.animation.Interpolator;

import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.zj.views.list.holders.BaseItemAnimator;

@SuppressWarnings("unused")
public class FlipInRightYAnimator extends BaseItemAnimator {

  public FlipInRightYAnimator() {
  }

  public FlipInRightYAnimator(Interpolator interpolator) {
    mInterpolator = interpolator;
  }

  @Override protected void animateRemoveImpl(final RecyclerView.ViewHolder holder) {
    ViewCompat.animate(holder.itemView)
        .rotationY(-90)
        .setDuration(getRemoveDuration())
        .setInterpolator(mInterpolator)
        .setListener(new DefaultRemoveVpaListener(holder))
        .setStartDelay(getRemoveDelay(holder))
        .start();
  }

  @Override protected void preAnimateAddImpl(RecyclerView.ViewHolder holder) {
    ViewCompat.setRotationY(holder.itemView, -90);
  }

  @Override protected void animateAddImpl(final RecyclerView.ViewHolder holder) {
    ViewCompat.animate(holder.itemView)
        .rotationY(0)
        .setDuration(getAddDuration())
        .setInterpolator(mInterpolator)
        .setListener(new DefaultAddVpaListener(holder))
        .setStartDelay(getAddDelay(holder))
        .start();
  }
}
