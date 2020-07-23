package com.zj.views.list.refresh.layout.listener;

import androidx.annotation.NonNull;

import com.zj.views.list.refresh.layout.api.RefreshLayoutIn;

public interface OnRefreshListener {
    void onRefresh(@NonNull RefreshLayoutIn refreshLayout);
}
