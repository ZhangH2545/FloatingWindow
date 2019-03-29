package com.zhang.floatwindow;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by zhanghui on 2019/3/29.
 */

public class utils {
    private static int statusbarheight = 0;
    private static int daohangheight = 0;

    /**
     * 获取状态栏高度
     *
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        try {
            Class<?> aClass = Class.forName("com.android.internal.R$dimen");
            Object object = aClass.newInstance();
            Field field = aClass.getField("status_bar_height");
            int x = Integer.parseInt(field.get(object).toString());
            statusbarheight = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusbarheight;
    }

    /**
     * 获取导航栏高度
     *
     * @param context
     * @return
     */
    public static int getDaoHangHeight(Context context) {
        int result = 0;
        int resourceId = 0;
        int rid = context.getResources().getIdentifier("config_showNavigationBar", "bool", "android");
        if (rid != 0) {
            resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
            if (resourceId > 0) {
                daohangheight = context.getResources().getDimensionPixelSize(resourceId);
            }
        }
        return daohangheight;
    }

    /**
     * 判断悬浮船service是不是正在运行
     *
     * @param mContext
     * @param className
     * @return
     */
    public static boolean isServiceRunning(Context mContext, String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager
                .getRunningServices(1000);
        if (!(serviceList.size() > 0)) {
            return false;
        }
        for (int i = 0; i < serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(className) == true) {
                isRunning = true;
                break;
            }
        }

        return isRunning;
    }

    public static void saveStringToMainSP(Context context, String key, String value) {
        SharedPreferences sp = context.getSharedPreferences(CommParams.SP_NAME_MAIN, 0);
        sp.edit().putString(key, value).commit();
    }

    public static String getStringFromMainSP(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(CommParams.SP_NAME_MAIN, 0);
        return sp.getString(key, "");
    }

    public static void saveIntToMainSP(Context context, String key, int value) {
        SharedPreferences sp = context.getSharedPreferences(CommParams.SP_NAME_MAIN, 0);
        sp.edit().putInt(key, value).commit();
    }

    public static int getIntFromMainSP(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(CommParams.SP_NAME_MAIN, 0);
        return sp.getInt(key, -1);
    }


}
