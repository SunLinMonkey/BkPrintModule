package com.bk.bkprintmodulelib.print_help;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Printer;

import com.android.print.sdk.PrinterType;
import com.bk.bkprintmodulelib.anotation.AnotationPrinterType;
import com.bk.bkprintmodulelib.cosntants.PekonPrinterType;


/**
 * SharedPreferences工具类
 *
 * @author Zhoujun 说明：SharedPreferences的操作工具类，需要缓存到SharedPreferences中的数据在此设置。
 */
public class SharedPrefUtil {
    private static final String WIFI_PRINT_IP = "wifi_print_ip";//
    private static final String CURRENT_BLUETOOTH_DEVICE_ADDRESS = "current_bluetooth_address";//当前已选择的蓝牙打印设备
    private static final String MAIN_PRINTER = "main_printer";
    private static final String HELP_PRINTER = "help_printer";
    private static SharedPrefUtil instance;


    public static SharedPrefUtil getInstance() {
        if (instance == null) {
            instance = new SharedPrefUtil();
        }
        return instance;
    }

    /**
     * 设置当前已经选择的蓝牙打印设备
     *
     * @param deviceAddres
     * @param context
     */
    public void setCurrentBluetoothDevice(String deviceAddres, Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        Editor e = sp.edit();
        e.putString(CURRENT_BLUETOOTH_DEVICE_ADDRESS, deviceAddres);
        e.commit();
    }


    /**
     * 设置主打印
     *
     * @param printer
     * @param context
     */
    public void setMainPrinter(@AnotationPrinterType int printer, Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        Editor e = sp.edit();
        e.putInt(MAIN_PRINTER, printer);
        e.apply();
    }

    /**
     * 设置辅助打印
     *
     * @param printer
     * @param context
     */
    public void setHelpPrinter(@AnotationPrinterType int printer, Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        Editor e = sp.edit();
        e.putInt(HELP_PRINTER, printer);
        e.apply();
    }


    /**
     * 获取主打印
     *
     * @param context
     * @return
     */
    public int getMainPrinter(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        int anInt = sp.getInt(MAIN_PRINTER, PekonPrinterType.PRINTER_SUMMI);
        return anInt;
    }

    /**
     * 获取副打印
     *
     * @param context
     * @return
     */
    public int getHelpPrinter(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        int anInt = sp.getInt(HELP_PRINTER, PekonPrinterType.PRINTER_SUMMI);
        return anInt;
    }


    /**
     * 设置打印小票地址
     *
     * @param context
     */
    public static void setWifiPrintIp(Context context, String strAddress) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        Editor e = sp.edit();
        e.putString(WIFI_PRINT_IP, strAddress);
        e.commit();
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


}
