package com.zj.views.list.holders;

import androidx.core.view.ViewPropertyAnimatorListener;
import androidx.recyclerview.widget.RecyclerView;

/**
 * create by ZJJ on 18/6/1
 *
 * @see BaseItemAnimator
 * if you used a BaseItemAnimator  ,you should make your holder implements it ,and made your animations
 */

public interface AnimateViewHolder {

    void preAnimateAddImpl(final RecyclerView.ViewHolder holder);

    void preAnimateRemoveImpl(final RecyclerView.ViewHolder holder);

    void animateAddImpl(final RecyclerView.ViewHolder holder, ViewPropertyAnimatorListener listener);

    void animateRemoveImpl(final RecyclerView.ViewHolder holder, ViewPropertyAnimatorListener listener);
}
