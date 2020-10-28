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

    private final List<T> data;

    public ItemClickListener<T> onClickListener;

    public BaseRecyclerAdapter() {
        data = new ArrayList<>();
    }

    public void setOnItemClickListener(ItemClickListener<T> listener) {
        this.onClickListener = listener;
    }

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
        if (position != getMaxPosition()) notifyItemRangeChanged(position, getItemCount());
    }

    public void add(List<T> data) {
        if (data == null) return;
        int curStart = getItemCount();
        this.data.addAll(data);
        int curLast = curStart + data.size();
        notifyItemRangeInserted(curStart, curLast);
        if (curLast != getMaxPosition()) notifyItemRangeChanged(curLast, getItemCount());
    }

    public void add(List<T> data, int position) {
        if (data == null || position < 0) return;
        position = Math.min(getMaxPosition(), position);
        this.data.addAll(position, data);
        int curLast = position + data.size();
        notifyItemRangeInserted(position, curLast);
        if (curLast != getMaxPosition()) notifyItemRangeChanged(curLast, getItemCount());
    }

    public void remove(T info) {
        if (info == null) return;
        remove(data.indexOf(info));
    }

    public void remove(int position) {
        if (getItemCount() <= position) return;
        this.data.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount());
    }

    public void clear() {
        data.clear();
        notifyDataSetChanged();
    }

    public void change(List<T> d) {
        int size = data.size();
        data.clear();
        notifyItemRangeRemoved(0, size);
        if (d == null) {
            return;
        }
        this.data.addAll(d);
        notifyItemRangeInserted(0, getItemCount());
    }
}
