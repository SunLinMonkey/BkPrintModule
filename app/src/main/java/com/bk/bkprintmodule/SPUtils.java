package com.bk.bkprintmodule;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SPUtils {

    private static final String PRINT_TYPE = "print_type";// 打印方式
    private static final String PRINT_PAPER_SELECTTION = "print_paper_selection";// 打印纸张选择
    private static final String WIFI_PRINT_IP = "wifi_print_ip";

    /**
     * 获取打印方式配置项；
     *
     * @param context
     * @return
     */
    public static int getPrintType(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getInt(PRINT_TYPE, Constants.WIFI);
    }

    /**
     * 获取打印纸张选择配置项；
     *
     * @param context
     * @return
     */
    public static int getPrintPaperSelection(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getInt(PRINT_PAPER_SELECTTION, Constants.PAPER_SELECTION_80);
    }

    /**
     * 保存打印纸张选择配置项；
     *
     * @param context ，paperSelection
     */
    public static void setPrintPaperSelection(Context context, int paperSelection) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(PRINT_PAPER_SELECTTION, paperSelection);
        editor.commit();
    }

    /**
     * 保存打印方式配置项；
     *
     * @param context
     * @param type
     */
    public static void setPrintType(Context context, int type) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(PRINT_TYPE, type);
        editor.commit();
    }

    /**
     * 获取打印小票地址
     *
     * @param context
     * @return
     */
    public static String getWifiPrintIp(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(WIFI_PRINT_IP, "");
    }

    /**
     * 设置打印小票地址
     *
     * @param context
     */
    public static void setWifiPrintIp(Context context, String strAddress) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor e = sp.edit();
        e.putString(WIFI_PRINT_IP, strAddress);
        e.commit();
    }



}
