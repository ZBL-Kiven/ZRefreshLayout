package com.zj.test;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zj.cf.fragments.BaseLinkageFragment;
import com.zj.views.pop.CusPop;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;

public class Frg2 extends BaseLinkageFragment {
    @NotNull
    @Override
    protected View getView(@NotNull LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup) {
        return layoutInflater.inflate(R.layout.fg_second, viewGroup, false);
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        final View c = find(R.id.tv);
        assert c != null;
        c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CusPop.Companion.create(getRootView()).dimMode(CusPop.DimMode.CONTENT).gravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL).contentId(R.layout.pop_view).show(new Function2<View, CusPop, Unit>() {
                    @Override
                    public Unit invoke(View view, CusPop cusPop) {
                        return null;
                    }
                });
            }
        });
    }
}
