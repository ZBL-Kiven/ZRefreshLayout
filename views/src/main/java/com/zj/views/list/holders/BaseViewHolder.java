package com.zj.views.list.holders;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.recyclerview.widget.RecyclerView;

import com.zj.views.list.adapters.BaseRecyclerAdapter;

/**
 * Created by ZJJ on 2018/4/4.
 */

@SuppressWarnings("unused")
public class BaseViewHolder<T> extends RecyclerView.ViewHolder {

    private final SparseArray<View> parseArray;
    private final BaseRecyclerAdapter<?, T> mAdapter;

    public BaseViewHolder(BaseRecyclerAdapter<?, T> adapter, View v) {
        super(v);
        this.mAdapter = adapter;
        parseArray = new SparseArray<>();
        itemView.setOnClickListener(v1 -> {
            if (mAdapter.onClickListener != null) {
                mAdapter.onClickListener.onItemClick(getLayoutPosition(), itemView, mAdapter.getItem(getLayoutPosition()));
            }
        });
    }

    @SuppressWarnings("unchecked")
    public <V extends View> V getView(@IdRes int id) {
        V v = (V) parseArray.get(id);
        if (v == null) {
            v = itemView.findViewById(id);
            parseArray.put(id, v);
        }
        return v;
    }

    public TextView setText(@IdRes int id, String s) {
        TextView v = getView(id);
        v.setText(s);
        return v;
    }

    public TextView setTextColor(@IdRes int viewId, @ColorInt int textColor) {
        TextView view = getView(viewId);
        view.setTextColor(textColor);
        return view;
    }

    public ImageView setImageResource(@IdRes int viewId, @DrawableRes int imageResId) {
        ImageView view = getView(viewId);
        view.setImageResource(imageResId);
        return view;
    }

    public View setBackgroundColor(@IdRes int viewId, @ColorInt int color) {
        View view = getView(viewId);
        view.setBackgroundColor(color);
        return view;
    }

    public View setBackgroundRes(@IdRes int viewId, @DrawableRes int backgroundRes) {
        View view = getView(viewId);
        view.setBackgroundResource(backgroundRes);
        return view;
    }


    public ImageView setImageDrawable(@IdRes int viewId, Drawable drawable) {
        ImageView view = getView(viewId);
        view.setImageDrawable(drawable);
        return view;
    }

    public ImageView setImageBitmap(@IdRes int viewId, Bitmap bitmap) {
        ImageView view = getView(viewId);
        view.setImageBitmap(bitmap);
        return view;
    }
}
