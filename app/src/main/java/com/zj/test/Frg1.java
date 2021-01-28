package com.zj.test;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.zj.cf.fragments.BaseLinkageFragment;
import com.zj.views.DrawableTextView;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

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
        dtv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("===== ", " 11111 ");
                dtv.setDrawableBackground(new ColorDrawable(Color.CYAN));
                dtv.setBadgeText("");
                dtv.setReplaceDrawable(ContextCompat.getDrawable(v.getContext(), R.mipmap.ic_launcher));
                dtv.postInvalidate();
            }
        });
        dtv.setOnBadgeClickListener(new DrawableTextView.BadgeClickListener() {
            @Override
            public void onClick(DrawableTextView v) {
                Log.e("===== ", " 22222 ");
            }
        });
        dtv.setOnDrawableClickListener(new DrawableTextView.DrawableClickListener() {
            @Override
            public void onClick(DrawableTextView v) {
                Log.e("===== ", " 33333 ");
            }
        });
        handler.sendEmptyMessageDelayed(127, 2000);
    }
}
