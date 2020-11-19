package com.zj.test;

import android.app.Activity;
import android.os.Bundle;

import com.zj.views.FoldTextView;

public class SecondActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((FoldTextView) findViewById(R.id.dtv)).setText("asd asd 1 ewd d f a s d \n asf asd qf qf aa f an as q n");
    }
}
