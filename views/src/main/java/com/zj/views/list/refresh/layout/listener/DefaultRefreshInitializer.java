package com.zj.views.list.refresh.layout.listener;

import android.content.Context;

import androidx.annotation.NonNull;

import com.zj.views.list.refresh.layout.api.RefreshLayoutIn;

public interface DefaultRefreshInitializer {
    void initialize(@NonNull Context context, @NonNull RefreshLayoutIn layout);
}
