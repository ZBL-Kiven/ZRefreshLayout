package com.zj.test;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.zj.cf.fragments.BaseLinkageFragment;
import com.zj.views.DrawableTextView;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class Frg1 extends BaseLinkageFragment {
    @NotNull
    @Override
    protected View getView(@NotNull LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup) {
        return layoutInflater.inflate(R.layout.activity_second, viewGroup, false);
    }

    DrawableTextView dtv;

    private final Handler handler = new Handler(Objects.requireNonNull(Looper.myLooper())) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            dtv.setSelected(true);
            dtv.setVisibility(View.VISIBLE);
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
                v.setSelected(!v.isSelected());
            }
        });
        handler.sendEmptyMessageDelayed(127, 2000);
    }
}
