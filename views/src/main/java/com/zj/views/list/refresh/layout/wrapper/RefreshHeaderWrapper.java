package com.zj.views.list.refresh.layout.wrapper;

import android.annotation.SuppressLint;
import android.view.View;

import com.zj.views.list.refresh.layout.api.RefreshHeader;
import com.zj.views.list.refresh.layout.simple.SimpleComponent;

/**
 * 刷新头部包装
 */
@SuppressLint("ViewConstructor")
public class RefreshHeaderWrapper extends SimpleComponent implements RefreshHeader {

    public RefreshHeaderWrapper(View wrapper) {
        super(wrapper);
    }

}
