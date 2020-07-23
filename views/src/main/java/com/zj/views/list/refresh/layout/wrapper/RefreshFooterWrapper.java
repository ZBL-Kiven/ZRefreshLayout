package com.zj.views.list.refresh.layout.wrapper;

import android.annotation.SuppressLint;
import android.view.View;

import com.zj.views.list.refresh.layout.api.RefreshFooter;
import com.zj.views.list.refresh.layout.simple.SimpleComponent;

@SuppressLint("ViewConstructor")
public class RefreshFooterWrapper extends SimpleComponent implements RefreshFooter {

    public RefreshFooterWrapper(View wrapper) {
        super(wrapper);
    }

}
