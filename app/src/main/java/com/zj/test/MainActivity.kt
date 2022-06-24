package com.zj.test

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.zj.cf.managers.BaseFragmentManager
import com.zj.test.views.DynamicLivingImageView
import com.zj.test.views.DynamicLivingTextView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_2)
        val ll = findViewById<LinearLayout>(R.id.group)
        object : BaseFragmentManager(this, R.id.fg_content, 0, ll, Frg1(), Frg2()) {}
    }

    fun onActive(v: View) {
        v.isSelected = !v.isSelected
        if (v.isSelected) {
            (v as DynamicLivingTextView).startLivingAnim()
        } else {
            (v as DynamicLivingTextView).stopLivingAnim()
        }
    }

    fun onActiveImg(v: View) {
        v.isSelected = !v.isSelected
        if (v.isSelected) {
            (v as DynamicLivingImageView).startLivingAnim()
        } else {
            (v as DynamicLivingImageView).stopLivingAnim()
        }
    }

}