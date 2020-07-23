package com.zj.views.list.refresh.layout.listener;

import android.content.Context;

import androidx.annotation.NonNull;

import com.zj.views.list.refresh.layout.api.RefreshHeader;
import com.zj.views.list.refresh.layout.api.RefreshLayoutIn;

public interface DefaultRefreshHeaderCreator {
    @NonNull
    RefreshHeader createRefreshHeader(@NonNull Context context, @NonNull RefreshLayoutIn layout);
}
