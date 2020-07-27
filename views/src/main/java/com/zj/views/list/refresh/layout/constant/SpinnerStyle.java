package com.zj.views.list.refresh.layout.constant;

public class SpinnerStyle {

    public static final SpinnerStyle Translate = new SpinnerStyle(0, true, false);

    public static final SpinnerStyle FixedBehind = new SpinnerStyle(2, false, false);
    public static final SpinnerStyle FixedFront = new SpinnerStyle(3, true, false);
    public static final SpinnerStyle MatchLayout = new SpinnerStyle(4, true, false);

    public static final SpinnerStyle[] values = new SpinnerStyle[]{
            Translate, //平行移动 HeaderView高度不会改变，
            FixedBehind, //固定在背后 HeaderView高度不会改变，
            FixedFront, //固定在前面 HeaderView高度不会改变，
            MatchLayout//填满布局 HeaderView高度不会改变，尺寸充满 RefreshLayout
    };

    public final int ordinal;
    public final boolean front;
    public final boolean scale;

    private SpinnerStyle(int ordinal, boolean front, boolean scale) {
        this.ordinal = ordinal;
        this.front = front;
        this.scale = scale;
    }
}
