package com.zj.viewMob;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.zj.views.DrawableTextView;
import com.zj.views.list.refresh.layout.wrapper.RefreshContentWrapper;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Class ds = RefreshContentWrapper.class;
        final DrawableTextView v = findViewById(R.id.dtv);
        v.postDelayed(new Runnable() {
            @Override
            public void run() {
                v.setSelected(true);
            }
        }, 1000);
    }
}
