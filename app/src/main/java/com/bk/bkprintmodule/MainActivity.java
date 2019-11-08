package com.bk.bkprintmodule;

import android.app.Activity;
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

        SharedPrefUtil.getInstance().setMainPrinter(PekonPrinterType.PRINTER_USB,this);
        PrinterManager.getInstance().init(this);

//        PrinterManager.getInstance().prepareLoop(this, new AbstractPrintStatus() {
//            @Override
//            public void onPrinterFinished() {
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
            public void onPrinterFinished() {
                showToast("打印完成");
                Log.e(TAG, "onPrinterFinished: 打印完成");
            }

            @Override
            public void onPrintFailed(String errorCode, String errorMessage) {
                showToast("打印失败");
                Log.e(TAG, "onPrinterFinished: 打印失败");
            }

            @Override
            public void onConnectSucceed() {
                showToast(" 服务连接成功");
                Log.e(TAG, "onPrinterFinished: 服务连接成功");
            }

            @Override
            public void onConnectFailed(String errorCode, String errorMessage) {
                showToast(" 服务连接失败");
                Log.e(TAG, "onPrinterFinished: 服务连接失败");
            }

            @Override
            public void onPrinterInitFailed(String errorCode, String errorMessage) {
                showToast("打印机初始化失败");
                Log.e(TAG, "onPrinterFinished: 打印机初始化失败");
            }

            @Override
            public void onPrinterInitSucceed() {
                showToast("打印机初始化成功");
                Log.e(TAG, "onPrinterFinished: 打印机初始化成功");
            }
        });
    }


    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    public void resetPrinter(View view) {

        PrinterManager.getInstance().getPrint().resetPrintConnection(this, new AbstractPrintStatus() {

            @Override
            public void onPrinterFinished() {
                showToast("打印完成");
                Log.e(TAG, "onPrinterFinished: 打印完成");
            }

            @Override
            public void onPrintFailed(String errorCode, String errorMessage) {
                showToast("打印失败");
                Log.e(TAG, "onPrinterFinished: 打印失败");
            }

            @Override
            public void onConnectSucceed() {
                super.onConnectSucceed();
                showToast("连接成功");
                Log.e(TAG, "onPrinterFinished: 连接成功");
            }

            @Override
            public void onConnectFailed(String errorCode, String errorMessage) {
                super.onConnectFailed(errorCode, errorMessage);
                showToast("连接失败");
                Log.e(TAG, "onPrinterFinished: 连接失败");
            }

            @Override
            public void onPrinterInitFailed(String errorCode, String errorMessage) {
                super.onPrinterInitFailed(errorCode, errorMessage);
                showToast("初始化失败");
                Log.e(TAG, "onPrinterFinished: 初始化失败");
            }

            @Override
            public void onPrinterInitSucceed() {
                super.onPrinterInitSucceed();
                showToast("初始化成功");
                Log.e(TAG, "onPrinterFinished: 初始化成功");
            }
        });
    }
}
