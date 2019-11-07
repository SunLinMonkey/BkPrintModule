package com.bk.bkprintmodulelib.printer.bluetooth;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Message;

import com.android.print.sdk.Barcode;
import com.android.print.sdk.PrinterConstants;
import com.android.print.sdk.PrinterInstance;
import com.bk.bkprintmodulelib.cosntants.TextGravity;
import com.bk.bkprintmodulelib.cosntants.TextSize;
import com.bk.bkprintmodulelib.print_help.AbstractPrintStatus;
import com.bk.bkprintmodulelib.print_help.HelpEntity;
import com.bk.bkprintmodulelib.print_help.IPrinter;
import com.bk.bkprintmodulelib.printer.BasePrinter;

import java.io.File;
import java.lang.ref.WeakReference;

public class BluetoothPrinter extends BasePrinter implements IPrinter {
    private IPrinterOpertion myOpertion;
    private PrinterInstance mPrinter;


    class MyHandler extends android.os.Handler {

        WeakReference<Context> mActivityReference;
        WeakReference<AbstractPrintStatus> listenerWeakReference;

        MyHandler(Context activity, AbstractPrintStatus initStatusListener) {
            mActivityReference = new WeakReference<>(activity);
            if (initStatusListener != null) {
                this.listenerWeakReference = new WeakReference<>(initStatusListener);
            }
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PrinterConstants.Connect.SUCCESS:
                    mPrinter = myOpertion.getPrinter();
                    if (mPrinter != null) {
                        mPrinter.init();
                        initSucceed();
                    } else {
                        initFailed();

                    }
                    break;
                case PrinterConstants.Connect.FAILED:
                    initFailed();
                    break;
                case PrinterConstants.Connect.CLOSED:
                    break;
                default:
                    break;
            }
        }

        private void initFailed() {
            if (listenerWeakReference != null && listenerWeakReference.get() != null) {
                listenerWeakReference.get().onConnectFailed("", "蓝牙初始化失败");
            }
            isConnected = false;
        }

        private void initSucceed() {
            isConnected = true;
            if (listenerWeakReference != null && listenerWeakReference.get() != null) {
                listenerWeakReference.get().onConnectSucceed();
            }
        }
    }


    @Override
    public void initPrintDriver(Context context, AbstractPrintStatus listener) {
        if (mPrinter != null) {
            mPrinter.init();
            listener.onPrinterInitSucceed();
        }
    }

    @Override
    public void initPrintConnection(Context context, AbstractPrintStatus listener) {
        MyHandler mHandler = new MyHandler(context, listener);

        File file = new File(context.getFilesDir(), "btinfo.properties");
        if (!file.exists()) {
            isConnected = false;
            btSetting(context, mHandler);
        } else {
            if (!isConnected) {
                myOpertion = new BluetoothOperation(context, mHandler);
                myOpertion.btAutoConn(context, mHandler);
            } else {
                listener.onConnectSucceed();
            }
        }
    }

    @Override
    public void resetPrintConnection(Context context, AbstractPrintStatus listener) {
        MyHandler mHandler = new MyHandler(context, listener);
        btSetting(context, mHandler);
    }


    private void btSetting(Context activity, MyHandler mHandler) {
        if (myOpertion != null) {
            myOpertion.close();
        }
        myOpertion = new BluetoothOperation(activity, mHandler);

        myOpertion.chooseDevice(new BluetoothDeviceListDialog.OnChosenListener() {
            @Override
            public void onChosen(final String address, boolean re_pair, String name) {
                new Thread(new Runnable() {
                    public void run() {
                        myOpertion.open(address);
                    }
                }).start();

            }
        });
    }


    @Override
    public void printText(String content) {
        getLocalTextSize();
        getLocalGravity();
        mPrinter.printText(content);
    }

    @Override
    public void printBarCode(String content) {
        Barcode barcode = new Barcode(PrinterConstants.BarcodeType.CODE39, 2, 130, 20, content);
        mPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_CENTER);
        mPrinter.printBarCode(barcode);
    }


    @Override
    public void printQRCode(String content) {
        Barcode barcode = new Barcode(PrinterConstants.BarcodeType.QRCODE, 2, 3, 6, content);
        mPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_CENTER);
        mPrinter.printBarCode(barcode);
    }

    @Override
    public void printImage(Bitmap bitmap) {
        mPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_CENTER);
        mPrinter.printImage(bitmap);
    }

    @Override
    public void closePrinter(Context context) {
        myOpertion.close();
    }

    @Override
    public void setPrintHelpData(HelpEntity helpEntity) {
        setHelpEntity(helpEntity);
    }

    @Override
    public void printBlankLine(int linesNum) {
        for (int num = linesNum; num > 0; num--) {
            printBlankLine();
        }
    }

    @Override
    public void printBlankLine() {
        mPrinter.printText("\n");
    }

    @Override
    public void cutPaper() {
        mPrinter.cutPaper();
    }

    @Override
    public void startPrint(Context context) {

    }

    @Override
    public void openCashBox() {
        mPrinter.openCashbox(true, true);
    }

    @Override
    public boolean isConnected() {
        return false;
    }

    @Override
    public boolean isPrinterReady() {
        return false;
    }


    /**
     * 将通用字体大小转成本打印机实体类使用的字体大小
     *
     * @return
     */
    protected void getLocalTextSize() {
        HelpEntity helpEntity = getHelpEntity();
        switch (helpEntity.getTestSize()) {
            case TextSize.TEXT_SIZE_DOWN_2:
            case TextSize.TEXT_SIZE_DOWN_1:
            case TextSize.TEXT_SIZE_DEFAULT: {
                mPrinter.setCharacterMultiple(0, 0);
                break;
            }

            case TextSize.TEXT_SIZE_UP_3: {
                mPrinter.setCharacterMultiple(1, 1);
                break;
            }

            case TextSize.TEXT_SIZE_UP_4: {
                mPrinter.setCharacterMultiple(2, 2);
                break;
            }

            default: {
                mPrinter.setCharacterMultiple(0, 0);
                break;
            }
        }
    }

    @Override
    protected void getLocalGravity() {
        HelpEntity helpEntity = getHelpEntity();
        switch (helpEntity.getGrivaty()) {
            case TextGravity.GRAVITY_CENTER: {
                mPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_CENTER);
                break;
            }
            case TextGravity.GRAVITY_LEFT: {
                mPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_LEFT);
                break;
            }
            case TextGravity.GRAVITY_RIGHT: {
                mPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_RIGHT);
                break;
            }
            default: {
                mPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_CENTER);
                break;
            }
        }
    }
}
