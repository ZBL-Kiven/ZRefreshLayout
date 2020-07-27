package com.zj.viewMob;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zj.cf.fragments.BaseLinkageFragment;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Frg1 extends BaseLinkageFragment {
    @NotNull
    @Override
    protected View getView(@NotNull LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup) {
        return layoutInflater.inflate(R.layout.activity_second, viewGroup, false);
    }
}
