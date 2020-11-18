package com.zj.test;

import android.app.Activity;
import android.os.Bundle;

import com.zj.views.FoldTextView;

public class SecondActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);
        ((FoldTextView) findViewById(R.id.dtv)).setText("asd \n asf asd qf qf qsc aa qs d q f ann\n q\n q\n  sd\n  f qed  sd f ew a sd  dq wd s df sd fq wd a sd a ds q ef w e  sd f df w d sdf s dfw e as qq a");
    }
}
