package com.bk.bkprintmodulelib.print_help;

import android.util.Log;

public abstract class AbstractPrintStatus {

    private static final String TAG = "9527" ;

    /**
     * 初始化连接成功
     */
    public void onConnectSucceed(){
        Log.e(TAG, this.getClass().getSimpleName()+"   onConnectSucceed: 初始化连接成功");
    }

    /**
     * 初始化连接失败
     */
    public void onConnectFailed(String errorCode,String errorMessage){
        Log.e(TAG, this.getClass().getSimpleName()+"   onConnectFailed: 初始化连接失败");
    }

    /**
     * 初始化打印机成功
     */
    public void onPrinterInitSucceed(){
        Log.e(TAG, this.getClass().getSimpleName()+"   onPrinterInitSucceed: 打印机连接成功");
   }

    /**
     * 初始化打印机失败
     */
    public void onPrinterInitFailed(String errorCode,String errorMessage){
        Log.e(TAG, this.getClass().getSimpleName()+"   onPrinterInitFailed: 打印机连接失败");
    }

    /**
     * 打印完成
     */
    public abstract void onPrinterFinished();

    /**
     * 打印失败
     */
    public abstract void onPrintFailed(String errorCode,String errorMessage);

}
