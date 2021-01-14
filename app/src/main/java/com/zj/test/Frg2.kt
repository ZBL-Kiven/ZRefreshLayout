package com.zj.test

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zj.cf.fragments.BaseLinkageFragment
import com.zj.test.widget.UploadNotifyPop

class Frg2 : BaseLinkageFragment() {
    override fun getView(layoutInflater: LayoutInflater, viewGroup: ViewGroup?): View {
        return layoutInflater.inflate(R.layout.fg_second, viewGroup, false)
    }

    override fun onCreate() {
        super.onCreate()
        val c = find<View>(R.id.tv)
        val c1 = find<View>(R.id.tv1)
        val c2 = find<View>(R.id.tv2)
        val c3 = find<View>(R.id.tv3)
        c?.setOnClickListener { UploadNotifyPop.show(requireContext(), "", "asdasd") }
        c1?.setOnClickListener { startActivity(Intent(this.requireContext(), SecondActivity::class.java)) }
        c2?.setOnClickListener { UploadNotifyPop.setState(UploadNotifyPop.UploadingState.UP_LOADING.withProgress(50)) }
        c3?.setOnClickListener { UploadNotifyPop.setState(UploadNotifyPop.UploadingState.FAILED) }
    }
}