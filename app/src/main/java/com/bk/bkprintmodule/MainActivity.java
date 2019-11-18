package com.bk.bkprintmodule;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bk.bkprintmodulelib.PrinterManager;
import com.bk.bkprintmodulelib.cosntants.PekonPrinterType;
import com.bk.bkprintmodulelib.print_help.AbstractPrintStatus;
import com.bk.bkprintmodulelib.print_help.SharedPrefUtil;


public class MainActivity extends Activity {

    private static final String TAG = "9527";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startActivity(new Intent(this,PrintTestActivityNew.class));
//        PrinterManager.getInstance().prepareLoop(this, new AbstractPrintStatus() {
//            @Override
//            public void onPrintFinished() {
//
//            }
//
//            @Override
//            public void onPrintFailed(String errorCode, String errorMessage) {
//
//            }
//        });

    }

    public void initPrinter(View view) {
//        for (int i = 4; i > 0; i--) {
//            try {
//                PrinterManager.getInstance().addPrintContent(new TestPrintDataAnalysis(this, "测试打印"));
//                long l = (long) (Math.random() * 1000);
//                Log.e(TAG, "给值22 initPrinter: " + l);
//                Thread.sleep(l);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }

        print();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PrinterManager.getInstance().loopEnd();
    }

    private void print() {
        PrinterManager.getInstance().printContent( this, new TestPrintDataAnalysis(this,"测试打印"), new AbstractPrintStatus() {
            @Override
            public void onPrintFinished(String errorCode, String errorMessage) {
                showToast("打印完成");
                Log.e(TAG, "onPrintFinished: 打印完成");
            }

            @Override
            public void onPrintFailed(String errorCode, String errorMessage) {
                showToast("打印失败");
                Log.e(TAG, "onPrintFailed: 打印失败:"+errorMessage);
            }

            @Override
            public void onConnectSucceed(String errorCode, String errorMessage) {
                showToast(" 服务连接成功");
                Log.e(TAG, "onPrintFinished: 服务连接成功");
            }

            @Override
            public void onConnectFailed(String errorCode, String errorMessage) {
                showToast(" 服务连接失败");
                Log.e(TAG, "onPrintFinished: 服务连接失败");
            }

            @Override
            public void onPrinterInitFailed(String errorCode, String errorMessage) {
                showToast("打印机初始化失败");
                Log.e(TAG, "onPrintFinished: 打印机初始化失败");
            }

            @Override
            public void onPrinterInitSucceed(String errorCode, String errorMessage) {
                showToast("打印机初始化成功");
                Log.e(TAG, "onPrintFinished: 打印机初始化成功");
            }
        });
    }


    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    public void resetPrinter(View view) {
        PrinterManager.getInstance().saveWifiPrinterIpAndPort(this,"192.168.30.193:2088");
        PrinterManager.getInstance().getPrint().resetPrintConnection(this, new AbstractPrintStatus() {
            @Override
            public void onPrintFinished(String errorCode, String errorMessage) {
                showToast("打印完成");
                Log.e(TAG, "onPrintFinished: 打印完成");
            }

            @Override
            public void onPrintFailed(String errorCode, String errorMessage) {
                showToast("打印失败");
                Log.e(TAG, "onPrintFailed: 打印失败:"+errorMessage);
            }

            @Override
            public void onConnectSucceed(String errorCode, String errorMessage) {
                super.onConnectSucceed(errorCode,errorMessage);
                showToast("连接成功");
                Log.e(TAG, "onPrintFinished: 连接成功");
            }

            @Override
            public void onConnectFailed(String errorCode, String errorMessage) {
                super.onConnectFailed(errorCode, errorMessage);
                showToast("连接失败");
                Log.e(TAG, "onPrintFinished: 连接失败");
            }

            @Override
            public void onPrinterInitFailed(String errorCode, String errorMessage) {
                super.onPrinterInitFailed(errorCode, errorMessage);
                showToast("初始化失败");
                Log.e(TAG, "onPrintFinished: 初始化失败");
            }

            @Override
            public void onPrinterInitSucceed(String errorCode, String errorMessage) {
                super.onPrinterInitSucceed(errorCode,errorMessage);
                showToast("初始化成功");
                Log.e(TAG, "onPrintFinished: 初始化成功");
            }
        });
    }
}
