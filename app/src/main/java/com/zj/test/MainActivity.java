package com.zj.test;

import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.zj.cf.managers.BaseFragmentManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_2);
        final LinearLayout ll = findViewById(R.id.group);
        new BaseFragmentManager(this, R.id.fg_content, 0, ll, new Frg1(), new Frg2()) {
        };
        //        FoldTextView ftv = findViewById(R.id.dtv);
        //        ftv.setText("uyqgewf qwd qw ef weq fwe  fw ef q  sdv qwf qwe f ds c d cw e sd sd c sdc w fd wf uyqgewf qwd qw ef weq fwe  fw ef q  sdv qwf qwe f ds c d cw e sd sd c sdc w fd wfuyqgewf qwd qw ef weq fwe  fw ef q  sdv qwf qwe f ds c d cw e sd sd c sdc w fd wf uyqgewf qwd qw ef weq fwe  fw ef q  sdv qwf qwe f ds c d cw e sd sd c sdc w fd wf uyqgewf qwd qw ef weq fwe  fw ef q  sdv qwf qwe f ds c d cw e sd sd c sdc w fd wf");
    }
}
