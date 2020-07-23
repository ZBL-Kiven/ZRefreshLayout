package com.zj.views.list.refresh.layout.listener;


import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;

import com.zj.views.list.refresh.layout.api.RefreshLayoutIn;
import com.zj.views.list.refresh.layout.constant.RefreshState;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;
import static androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP;
import static androidx.annotation.RestrictTo.Scope.SUBCLASSES;

public interface OnStateChangedListener {
    /**
     * 【仅限框架内调用】状态改变事件 {@link RefreshState}
     * @param refreshLayout RefreshLayout
     * @param oldState 改变之前的状态
     * @param newState 改变之后的状态
     */
    @RestrictTo({LIBRARY,LIBRARY_GROUP,SUBCLASSES})
    void onStateChanged(@NonNull RefreshLayoutIn refreshLayout, @NonNull RefreshState oldState, @NonNull RefreshState newState);
}
