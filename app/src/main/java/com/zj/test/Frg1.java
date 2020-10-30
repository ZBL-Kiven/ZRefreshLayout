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

public class Frg1 extends BaseLinkageFragment {
    @NotNull
    @Override
    protected View getView(@NotNull LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup) {
        return layoutInflater.inflate(R.layout.activity_second, viewGroup, false);
    }

    DrawableTextView dtv;
    StringBuilder s = new StringBuilder();
    boolean isSelected = false;

    private final Handler handler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            s.append("a");
            dtv.setText(s.toString());
            isSelected = !isSelected;
            dtv.setSelected(isSelected);
        }
    };

    @Override
    protected void onCreate() {
        super.onCreate();
        dtv = find(R.id.dtv);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    handler.sendEmptyMessage(127);
                }
            }
        }).start();
    }
}
