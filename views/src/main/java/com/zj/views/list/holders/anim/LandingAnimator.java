package com.zj.views.list.holders.anim;

import android.view.animation.Interpolator;

import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.zj.views.list.holders.BaseItemAnimator;

@SuppressWarnings("unused")
public class LandingAnimator extends BaseItemAnimator {

  public LandingAnimator() {
  }

  public LandingAnimator(Interpolator interpolator) {
    mInterpolator = interpolator;
  }

  @Override protected void animateRemoveImpl(final RecyclerView.ViewHolder holder) {
    ViewCompat.animate(holder.itemView)
        .alpha(0)
        .scaleX(1.5f)
        .scaleY(1.5f)
        .setDuration(getRemoveDuration())
        .setInterpolator(mInterpolator)
        .setListener(new DefaultRemoveVpaListener(holder))
        .setStartDelay(getRemoveDelay(holder))
        .start();
  }

  @Override protected void preAnimateAddImpl(RecyclerView.ViewHolder holder) {
    holder.itemView.setAlpha( 0);
    holder.itemView.setScaleX( 1.5f);
    holder.itemView.setScaleY( 1.5f);
  }

  @Override protected void animateAddImpl(final RecyclerView.ViewHolder holder) {
    ViewCompat.animate(holder.itemView)
        .alpha(1)
        .scaleX(1)
        .scaleY(1)
        .setDuration(getAddDuration())
        .setInterpolator(mInterpolator)
        .setListener(new DefaultAddVpaListener(holder))
        .setStartDelay(getAddDelay(holder))
        .start();
  }
}
