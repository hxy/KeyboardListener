package com.yidian.common.utils;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;

import androidx.annotation.DimenRes;


/**
 * author: zhb
 * date: 2022/4/6
 * desc:
 */
public class DensityUtil {
    private static DisplayMetrics sDisplayMetrics = new DisplayMetrics();
    public static final float NINE_DIVIDE_SIXTEEN = 9f / 16;
    private static Application sApp;
    private static final String TAG = DensityUtil.class.getSimpleName();
    private static Configuration mConfiguration;
    public static final int DEFAULT_ABS = 3; // 布局中获取的app宽度 与 configration 中的app宽度差值边界，如果大于该值，则更新 configration 的app宽度。保证后续获取的宽度正常！

    public static void init(Application application) {
        sApp = application;
        ((WindowManager) sApp.getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay().getMetrics(sDisplayMetrics);
        mConfiguration = sApp.getResources().getConfiguration();
    }

    public static void setConfiguration(Configuration configuration){
        mConfiguration = configuration;
    }

    /**
     * 获取屏幕宽度     --更新 由于在折叠屏多窗口情况下，屏幕宽度不一定是app宽度，所以获取app宽度
     * @return width
     */
    public static int getScreenWidth() {
        return (int) Math.ceil(mConfiguration.screenWidthDp * getDensity());
    }

    public static int getScreenWidthDp() {
        return mConfiguration.screenWidthDp;
    }

    /**
     * 获取屏幕高度     --更新 由于在折叠屏多窗口情况下，屏幕高度不一定是app高度，所以获取app高度
     * @return height
     */
    public static int getScreenHeight() {
        return (int) Math.ceil(mConfiguration.screenHeightDp * getDensity());
    }

    public static Point getRealSize() {
        Point screenSize = new Point();
        WindowManager manager = ((WindowManager) sApp.getSystemService(Context.WINDOW_SERVICE));
        if (manager == null) {
            return new Point(getScreenWidth(), getScreenHeight());
        }
        Display display = manager.getDefaultDisplay();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                display.getRealSize(screenSize);
            } else {
                display.getSize(screenSize);
            }
        } else {
            screenSize.x = display.getWidth();
            screenSize.y = display.getHeight();
        }
        return screenSize;
    }

    public static float getDpi() {
        return sDisplayMetrics.densityDpi;
    }

    public static float getDensity() {
        return sDisplayMetrics.density;
    }

    public static float getScaledDensity(){
        return sDisplayMetrics.scaledDensity;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dp2px(float dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue,
                sDisplayMetrics);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dp(float pxValue) {
        final float scale = sDisplayMetrics.density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int sp2px(float sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
                sDisplayMetrics);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 sp
     */
    public static int px2sp(float pxValue) {
        final float scale = sDisplayMetrics.scaledDensity;
        return (int) (pxValue / scale + 0.5f);
    }

    public static void resetDensity(Application app) {
        sApp = app;
        ((WindowManager) sApp.getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay().getMetrics(sDisplayMetrics);
    }

    /**
     *
     * @param id:R.dimen.XX
     * @return
     */
    public static int dp2pxUsingDimenId(@DimenRes int id) {
        return (int) Math.ceil(sApp.getApplicationContext()
                .getResources().getDimension(id));
    }

}
