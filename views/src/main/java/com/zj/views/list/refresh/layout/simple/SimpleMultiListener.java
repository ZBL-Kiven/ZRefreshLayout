package com.zj.views.list.refresh.layout.simple;


import androidx.annotation.NonNull;

import com.zj.views.list.refresh.layout.api.RefreshFooter;
import com.zj.views.list.refresh.layout.api.RefreshHeader;
import com.zj.views.list.refresh.layout.api.RefreshLayoutIn;
import com.zj.views.list.refresh.layout.constant.RefreshState;
import com.zj.views.list.refresh.layout.listener.OnMultiListener;

public class SimpleMultiListener implements OnMultiListener {

    @Override
    public void onHeaderMoving(RefreshHeader header, boolean isDragging, float percent, int offset, int headerHeight, int maxDragHeight) {

    }

    @Override
    public void onHeaderReleased(RefreshHeader header, int headerHeight, int maxDragHeight) {

    }

    @Override
    public void onHeaderStartAnimator(RefreshHeader header, int footerHeight, int maxDragHeight) {

    }

    @Override
    public void onHeaderFinish(RefreshHeader header, boolean success) {

    }

    @Override
    public void onFooterMoving(RefreshFooter footer, boolean isDragging, float percent, int offset, int footerHeight, int maxDragHeight) {

    }

    @Override
    public void onFooterReleased(RefreshFooter footer, int footerHeight, int maxDragHeight) {

    }

    @Override
    public void onFooterStartAnimator(RefreshFooter footer, int headerHeight, int maxDragHeight) {

    }

    @Override
    public void onFooterFinish(RefreshFooter footer, boolean success) {

    }

    @Override
    public void onRefresh(@NonNull RefreshLayoutIn refreshLayout) {

    }

    @Override
    public void onLoadMore(@NonNull RefreshLayoutIn refreshLayout) {

    }

    @Override
    public void onStateChanged(@NonNull RefreshLayoutIn refreshLayout, @NonNull RefreshState oldState, @NonNull RefreshState newState) {

    }

}
