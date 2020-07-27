package com.zj.viewMob;

import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.zj.cf.managers.BaseFragmentManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_2);
        LinearLayout ll = findViewById(R.id.group);
        new BaseFragmentManager(this, R.id.fg_content, 0, ll, new Frg1(), new Frg1()) {

        };
    }
}
