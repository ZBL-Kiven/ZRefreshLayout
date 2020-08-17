package com.zj.views.list.adapters;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.zj.views.list.holders.BaseViewHolder;
import com.zj.views.list.listeners.ItemClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZJJ on 2018/4/4.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public abstract class BaseRecyclerAdapter<VH extends BaseViewHolder, T> extends RecyclerView.Adapter<VH> {

    public BaseRecyclerAdapter() {
        data = new ArrayList<>();
    }

    public ItemClickListener onClickListener;

    public void setOnItemClickListener(ItemClickListener<T> listener) {
        this.onClickListener = listener;
    }

    private List<T> data;

    @Override
    public int getItemCount() {
        return data.size();
    }

    public int getMaxPosition() {
        return getItemCount() - 1;
    }

    public List<T> getData() {
        return data;
    }

    @Nullable
    public T getItem(int position) {
        if (data == null || data.isEmpty() || position < 0 || position >= data.size()) return null;
        return data.get(position);
    }

    public void add(T info) {
        if (info == null) return;
        this.data.add(info);
        notifyItemInserted(getMaxPosition());
    }

    public void add(T info, int position) {
        if (info == null) return;
        this.data.add(position, info);
        notifyItemInserted(position);
    }

    public void add(List<T> data) {
        if (data == null) return;
        this.data.addAll(data);
        notifyItemRangeInserted(getMaxPosition() - this.data.size(), this.getItemCount());
    }

    public void add(List<T> data, int position) {
        if (data == null) return;
        this.data.addAll(position, data);
        notifyItemRangeChanged(position, getItemCount());
    }

    public void remove(T info) {
        if (info == null) return;
        int position = data.indexOf(info);
        this.data.remove(info);
        notifyItemRangeRemoved(position, getItemCount());
    }

    public void remove(int position) {
        if (getItemCount() <= position) return;
        this.data.remove(position);
        notifyItemRangeRemoved(position, getItemCount());
    }

    public void clear() {
        data.clear();
        notifyDataSetChanged();
    }

    public void change(List<T> data) {
        clear();
        if (data == null) {
            return;
        }
        this.data.addAll(data);
        notifyItemRangeInserted(0, getItemCount());
    }
}
