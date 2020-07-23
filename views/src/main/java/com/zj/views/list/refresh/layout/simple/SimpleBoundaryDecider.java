package com.zj.views.list.refresh.layout.simple;

import android.graphics.PointF;
import android.view.View;

import com.zj.views.list.refresh.layout.listener.ScrollBoundaryDecider;
import com.zj.views.list.refresh.layout.util.SmartUtil;

public class SimpleBoundaryDecider implements ScrollBoundaryDecider {

    public PointF mActionEvent;
    public ScrollBoundaryDecider boundary;
    public boolean mEnableLoadMoreWhenContentNotFull = true;
    @Override
    public boolean canRefresh(View content) {
        if (boundary != null) {
            return boundary.canRefresh(content);
        }
        return SmartUtil.canRefresh(content, mActionEvent);
    }

    @Override
    public boolean canLoadMore(View content) {
        if (boundary != null) {
            return boundary.canLoadMore(content);
        }
        return SmartUtil.canLoadMore(content, mActionEvent, mEnableLoadMoreWhenContentNotFull);
    }
}
