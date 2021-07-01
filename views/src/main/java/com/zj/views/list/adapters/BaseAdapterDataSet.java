package com.zj.views.list.adapters;

import android.view.View;

import androidx.annotation.Nullable;

import com.zj.views.list.holders.BaseViewHolder;


/**
 * Created by ZJJ on 2018/4/9.
 */
@SuppressWarnings("unused")
public abstract class BaseAdapterDataSet<T> {

    public abstract void initData(BaseViewHolder<T> holder, int position, @Nullable T module);

    public void onItemClick(int position, View v, @Nullable T m) {

    }

    public void onItemLongClick(int position, View v, @Nullable T m) {

    }
}
