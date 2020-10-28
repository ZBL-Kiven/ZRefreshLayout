package com.zj.views.list.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
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

    private int resId = 0;

    private View content = null;

    private ViewBuilder viewBuilder = null;

    private LayoutBuilder layoutBuilder = null;

    @Nullable
    protected Context context = null;

    private LayoutInflater inflater;

    public BaseAdapter(@LayoutRes int id) {
        resId = id;
    }

    public BaseAdapter(@LayoutRes int id, List<T> data) {
        resId = id;
        change(data);
    }

    public BaseAdapter(View view) {
        content = view;
    }

    public BaseAdapter(View view, List<T> data) {
        content = view;
        change(data);
    }

    public BaseAdapter(ViewBuilder builder) {
        viewBuilder = builder;
    }

    public BaseAdapter(ViewBuilder builder, List<T> data) {
        viewBuilder = builder;
        change(data);
    }

    public BaseAdapter(LayoutBuilder builder) {
        layoutBuilder = builder;
    }

    public BaseAdapter(LayoutBuilder builder, List<T> data) {
        layoutBuilder = builder;
        change(data);
    }

    @Override
    public final BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        BaseViewHolder holder;
        if (content == null) {
            if (inflater == null) inflater = LayoutInflater.from(context);
            if (viewBuilder != null) {
                holder = new BaseViewHolder(this, viewBuilder.onCreateView(parent, inflater, viewType));
            } else if (layoutBuilder != null) {
                holder = new BaseViewHolder(this, inflater.inflate(layoutBuilder.onCreateView(context, viewType), parent, false));
            } else if (resId != 0) {
                holder = new BaseViewHolder(this, inflater.inflate(resId, parent, false));
            } else {
                throw new IllegalArgumentException();
            }
        } else {
            holder = new BaseViewHolder(this, content);
        }
        return holder;
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


    public interface ViewBuilder {
        View onCreateView(@NonNull ViewGroup parent, LayoutInflater inflater, int viewType);
    }

    public interface LayoutBuilder {
        @LayoutRes
        int onCreateView(Context context, int viewType);
    }
}
