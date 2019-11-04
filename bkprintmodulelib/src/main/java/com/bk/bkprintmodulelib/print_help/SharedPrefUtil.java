package com.example.pekonprinter.printer_help;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;


/**
 * SharedPreferences工具类
 *
 * @author Zhoujun 说明：SharedPreferences的操作工具类，需要缓存到SharedPreferences中的数据在此设置。
 */
public class SharedPrefUtil {
    private static final String CURRENT_BLUETOOTH_DEVICE_ADDRESS = "current_bluetooth_address";//当前已选择的蓝牙打印设备
    private static SharedPrefUtil instance;

    public SharedPrefUtil(Context context) {

    }

    public static SharedPrefUtil getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPrefUtil(context);
        }
        return instance;
    }

    /**
     * 设置当前已经选择的蓝牙打印设备
     *
     * @param deviceAddres
     * @param context
     */
    public static void setCurrentBluetoothDevice(String deviceAddres, Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        Editor e = sp.edit();
        e.putString(CURRENT_BLUETOOTH_DEVICE_ADDRESS, deviceAddres);
        e.commit();
    }


}
