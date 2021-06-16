package com.zj.test;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.zj.cf.fragments.BaseLinkageFragment;
import com.zj.views.DrawableTextView;
import com.zj.views.pop.CusPop;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;

public class Frg1 extends BaseLinkageFragment {
    @NotNull
    @Override
    protected View getView(@NotNull LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup) {
        return layoutInflater.inflate(R.layout.fg_first, viewGroup, false);
    }

    DrawableTextView dtv;

    private final Handler handler = new Handler(Objects.requireNonNull(Looper.myLooper())) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            //            dtv.setSelected(true);
            //            dtv.setVisibility(View.VISIBLE);
        }
    };

    @Override
    protected void onCreate() {
        super.onCreate();
        dtv = find(R.id.dtv);
        assert dtv != null;
        dtv.setOnClickListener(v -> {
            CusPop.Companion.create(v).contentId(R.layout.cus_pop_test).overrideContentGravity(Gravity.CENTER | Gravity.END).dimColor(Color.GRAY).dimMode(CusPop.DimMode.FULL_SCREEN).show((view, cusPop) -> null);
        });
        dtv.setOnBadgeClickListener(v -> {
            dtv.setDrawableBackground(new ColorDrawable(Color.CYAN));
            dtv.setBadgeText("");
            dtv.setReplaceDrawable(ContextCompat.getDrawable(v.getContext(), R.mipmap.ic_launcher));
            dtv.postInvalidate();
        });
        dtv.setOnDrawableClickListener(v -> Log.e("===== ", " 33333 "));
        handler.sendEmptyMessageDelayed(127, 2000);
    }
}
