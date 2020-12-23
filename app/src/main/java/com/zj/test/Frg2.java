package com.zj.test;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.zj.cf.fragments.BaseLinkageFragment;
import com.zj.views.pop.CusPop;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;

public class Frg2 extends BaseLinkageFragment {
    @NotNull
    @Override
    protected View getView(@NotNull LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup) {
        return layoutInflater.inflate(R.layout.fg_second, viewGroup, false);
    }


    @Override
    protected void onCreate() {
        super.onCreate();
        final View c = find(R.id.tv);
        assert c != null;
        c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pop = CusPop.Companion.create(getRootView()).contentId(R.layout.pop_view).instance();
                pop.show(new Function2<View, CusPop, Unit>() {
                    @Override
                    public Unit invoke(View view, CusPop cusPop) {
                        ImageView ivPic = view.findViewById(R.id.upload_pop_uploading_iv_pic);
                        SeekBar progress = view.findViewById(R.id.upload_pop_uploading_sb_progress);
                        TextView tvDesc = view.findViewById(R.id.upload_pop_uploading_tv_desc);
                        View close = view.findViewById(R.id.upload_pop_uploading_v_close);
                        View retry = view.findViewById(R.id.upload_pop_uploading_v_retry);
                        initView(ivPic, progress, tvDesc, close, retry);
                        return null;
                    }
                });
            }
        });
    }

    private CusPop pop;
    private ImageView ivPic;
    private SeekBar progress;
    private TextView tvDesc;
    private View close;
    private View retry;

    private UploadingState state;

    public void setContent(String path, String desc) {
        tvDesc.setText(desc);
        //        Glide.with(ivPic).load(path).thumbnil(0.1f).into(ivPic);
    }

    public void setState(UploadingState state) {
        this.state = state;
        initWithState();
    }

    private void initView(ImageView ivPic, SeekBar progress, TextView tvDesc, View close, View retry) {
        this.ivPic = ivPic;
        this.progress = progress;
        this.tvDesc = tvDesc;
        this.close = close;
        this.retry = retry;
        initWithState();
        progress.setProgress(state.progress);
    }

    private void initWithState() {
        progress.setVisibility(state == UploadingState.UP_LOADING ? View.VISIBLE : View.GONE);
        retry.setVisibility(state == UploadingState.FAILED ? View.VISIBLE : View.GONE);
        close.setVisibility(state != UploadingState.SUCCESS ? View.VISIBLE : View.GONE);
    }

    public enum UploadingState {

        UP_LOADING, FAILED, SUCCESS;

        private int progress = 0;

        public int getProgress() {
            return (this == UP_LOADING) ? progress : 0;
        }

        public UploadingState withProgress(int progress) {
            if (this != UP_LOADING) throw new IllegalArgumentException("only uploading state can taken progress params");
            this.progress = progress;
            return this;
        }
    }
}
