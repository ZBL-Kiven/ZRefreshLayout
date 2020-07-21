package com.zj.views;

import android.content.res.Resources;

@SuppressWarnings({"unused", "WeakerAccess"})
public class DPUtils {
    /**
     * Conversion from dp units to px (pixels) according to the resolution of the phone
     *
     * @param dpValue virtual pixels
     * @return pixels
     */
    public static int dp2px(float dpValue) {
        return (int) (0.5f + dpValue * Resources.getSystem().getDisplayMetrics().density);
    }

    /**
     * Conversion from px (pixels) to dp according to the resolution of the phone
     *
     * @param pxValue pixels
     * @return virtual pixels
     */
    public static float px2dp(int pxValue) {
        return (pxValue / Resources.getSystem().getDisplayMetrics().density);
    }

    public static float px2sp(float pxValue) {
        return pxValue / Resources.getSystem().getDisplayMetrics().scaledDensity;
    }

    public static int sp2px(float spValue) {
        return (int) (spValue * Resources.getSystem().getDisplayMetrics().scaledDensity + 0.5f);
    }
}
