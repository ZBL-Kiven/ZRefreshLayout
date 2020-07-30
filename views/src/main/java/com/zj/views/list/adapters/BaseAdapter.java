package com.zj.views.list.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zj.views.list.holders.BaseViewHolder;

import java.util.List;

/**
 * Created by ZJJ on 2018/4/4.
 */

public abstract class BaseAdapter<T> extends BaseRecyclerAdapter<BaseViewHolder, T> {

    private final int resId;

    @Nullable
    protected Context context = null;

    private LayoutInflater inflater;

    protected BaseAdapter(@LayoutRes int id) {
        resId = id;
    }

    BaseAdapter(@LayoutRes int id, List<T> data) {
        resId = id;
        change(data);
    }

    @Override
    public final BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        if (inflater == null) inflater = LayoutInflater.from(context);
        return new BaseViewHolder(this, inflater.inflate(resId, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        BaseAdapter.this.onBindViewHolder(holder, position, null);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position, @Nullable List<Object> payloads) {
        initData(holder, position, getItem(position), payloads);
    }

    protected abstract void initData(BaseViewHolder holder, int position, @Nullable T module, @Nullable List<Object> payloads);


}
