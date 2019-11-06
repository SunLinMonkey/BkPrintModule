package com.bk.bkprintmodulelib.printer.summi;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.os.RemoteException;


import com.bk.bkprintmodulelib.anotation.AnotationTextGravity;
import com.bk.bkprintmodulelib.cosntants.TextGravity;
import com.bk.bkprintmodulelib.print_help.ConnectionCallBack;
import com.bk.bkprintmodulelib.print_help.ESCUtil;
import com.bk.bkprintmodulelib.print_help.InitPrinterCallBack;

import woyou.aidlservice.jiuiv5.ICallback;
import woyou.aidlservice.jiuiv5.IWoyouService;


public class SummiAidlUtil {
    private static final String SERVICE＿PACKAGE = "woyou.aidlservice.jiuiv5";
    private static final String SERVICE＿ACTION = "woyou.aidlservice.jiuiv5.IWoyouService";

    private IWoyouService woyouService;
    private Context context;

    private ConnectionCallBack initStatusListener;

    private SummiAidlUtil() {
    }

    private static class SingleTonHolder {
        private static SummiAidlUtil INSTANCE = new SummiAidlUtil();
    }

    public static SummiAidlUtil getInstance() {
        return SummiAidlUtil.SingleTonHolder.INSTANCE;
    }

    /**
     * 连接服务
     *
     * @param context context
     * @param listener
     */
    public void connectPrinterService(Context context, ConnectionCallBack listener) {
        this.context = context;
        Intent intent = new Intent();
        intent.setPackage(SERVICE＿PACKAGE);
        intent.setAction(SERVICE＿ACTION);

        this.initStatusListener = listener;
        context.startService(intent);
        boolean connect = context.bindService(intent, connService, Context.BIND_AUTO_CREATE);
        if (!connect){
            initStatusListener.onFailed("","商米打印连接失败");
        }
    }


    public boolean isConnect() {
        return woyouService != null;
    }


    private ServiceConnection connService = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            woyouService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            woyouService = IWoyouService.Stub.asInterface(service);
            initStatusListener.onSucceed();
        }
    };


    /**
     * 初始化打印机
     */
    public void initPrinter(final InitPrinterCallBack initCallBack) {
        if (woyouService == null) {
            return;
        }

        try {
            woyouService.printerInit(new ICallback() {
                @Override
                public void onRunResult(boolean isSuccess, int code, String msg) throws RemoteException {
                    if (isSuccess){
                        initCallBack.onSucceed();
                    }else{
                        initCallBack.onFailed("","商米打印机初始化失败");
                    }
                }

                @Override
                public IBinder asBinder() {
                    return null;
                }
            });
            initCallBack.onSucceed();
        } catch (RemoteException e) {
            initCallBack.onFailed("","商米打印机初始化失败");
            e.printStackTrace();
        }
    }

    /**
     * 打印二维码
     */
    public void printQr(String data, int modulesize, int errorlevel) {
        try {
            woyouService.setAlignment(1, null);
            woyouService.printQRCode(data, modulesize, errorlevel, null);
            woyouService.lineWrap(1, null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    /**
     * 打印条形码
     */
    public void printBarCode(String data, int symbology, int height, int width, int textposition, ICallback iCallback) {
        try {
            woyouService.printBarCode(data, symbology, height, width, textposition, iCallback);
            woyouService.lineWrap(1, null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    /**
     * 打印文字
     */
    public void printText(String content, float size, boolean isBold, @AnotationTextGravity int gravity, boolean isUnderLine, ICallback callback) {
        try {
            if (isBold) {
                woyouService.sendRAWData(ESCUtil.boldOn(), null);
            } else {
                woyouService.sendRAWData(ESCUtil.boldOff(), null);
            }

            if (isUnderLine) {
                woyouService.sendRAWData(ESCUtil.underlineWithOneDotWidthOn(), null);
            } else {
                woyouService.sendRAWData(ESCUtil.underlineOff(), null);
            }


            if (TextGravity.GRAVITY_CENTER == gravity){
                woyouService.sendRAWData(ESCUtil.alignCenter(), null);
            }else if (TextGravity.GRAVITY_RIGHT == gravity){
                woyouService.sendRAWData(ESCUtil.alignRight(), null);
            }else{
                woyouService.sendRAWData(ESCUtil.alignLeft(), null);
            }

            woyouService.printTextWithFont(content, null, size, callback);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    /*
     *打印图片
     */
    public void printBitmap(Bitmap bitmap, ICallback iCallback) {
        try {
            woyouService.setAlignment(1, null);
            woyouService.printBitmap(bitmap, iCallback);
            woyouService.lineWrap(1, null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }


    /*
     * 空打一行！
     */
    public void print1Line() {
        try {
            woyouService.lineWrap(1, null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    public void cutPaper() {
        try {
            woyouService.cutPaper(new ICallback() {
                @Override
                public IBinder asBinder() {
                    return null;
                }

                @Override
                public void onRunResult(boolean isSuccess, int code, String msg) throws RemoteException {

                }


            });
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /*
     * 空打lines行！
     */
    public void print1Line(int lines) {
        try {
            woyouService.lineWrap(lines, null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /*
     * 开钱箱！
     */
    public void openDrawer() {
        try {
            woyouService.openDrawer(null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

}
