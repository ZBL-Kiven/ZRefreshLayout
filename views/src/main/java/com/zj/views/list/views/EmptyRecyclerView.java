package com.zj.views.list.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zj.views.list.adapters.BaseAdapter;
import com.zj.views.list.adapters.BaseAdapterDataSet;
import com.zj.views.list.holders.BaseViewHolder;
import com.zj.views.list.listeners.ItemClickListener;

import java.util.List;


/**
 * Created by ZJJ on 2018/4/9.
 */

@SuppressWarnings("unused")
public final class EmptyRecyclerView<T> extends RecyclerView {

    private BaseAdapter<T> adapter;

    public EmptyRecyclerView(Context context) {
        super(context);
    }

    public EmptyRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public EmptyRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public BaseAdapter<T> getAdapter() {
        return adapter;
    }

    public void setData(@LayoutRes int itemViewId, boolean isLoadMore, @Nullable List<T> data, @Nullable final BaseAdapterDataSet<T> adapterDataSet) {
        setOverScrollMode(OVER_SCROLL_NEVER);
        if (getLayoutManager() == null) setLayoutManager(new LinearLayoutManager(getContext()));
        if (adapter == null) {
            adapter = new BaseAdapter<T>(itemViewId) {
                @Override
                protected void initData(BaseViewHolder holder, int position, @Nullable T module, List<Object> payloads) {
                    if (adapterDataSet != null) adapterDataSet.initData(holder, position, module);
                }
            };
            setAdapter(adapter);
            if (adapterDataSet != null) {
                adapter.setOnItemClickListener(new ItemClickListener<T>() {
                    @Override
                    public void onItemClick(int position, View v, @Nullable T m) {
                        adapterDataSet.onItemClick(position, v, m);
                    }

                    @Override
                    public void onItemLongClick(int position, View v, @Nullable T m) {
                        adapterDataSet.onItemLongClick(position, v, m);
                    }
                });
            }
        }
        if (isLoadMore) adapter.add(data);
        else adapter.change(data);
    }
}
