package com.bk.bkprintmodulelib.printer.usbserialport;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;


import com.bk.bkprintmodulelib.cosntants.EncodingFormat;
import com.bk.bkprintmodulelib.cosntants.PrintCmd;
import com.bk.bkprintmodulelib.cosntants.TextGravity;
import com.bk.bkprintmodulelib.cosntants.TextSize;
import com.bk.bkprintmodulelib.print_help.AbstractPrintStatus;
import com.bk.bkprintmodulelib.print_help.IPrinter;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import android_serialport_api.SerialPort;

public class USBSerialport implements IPrinter {

    private static final String TAG = "9527";
    private final String SERIAL_PROT_FILE_PATH = "/dev/ttyUSB0";
    private SerialPort mSerialPort = null;
    private OutputStream pOutputStream;

    @Override
    public void initPrintDriver(Context context, AbstractPrintStatus listener) {
//        printStr = new StringBuffer();
        pOutputStream = mSerialPort.getOutputStream();
        listener.onPrinterInitSucceed();
    }

    @Override
    public void initPrintConnection(Context context, AbstractPrintStatus listener) {
        File usbFile = new File(SERIAL_PROT_FILE_PATH);
        if (!usbFile.exists()) {
            Log.e(TAG, "initPrintConnection: 没有串口");
            listener.onConnectFailed("", "");
        } else {
            mSerialPort = getSerialPort();
            listener.onConnectSucceed();
        }
    }

    @Override
    public void resetPrintConnection(Context context, AbstractPrintStatus listener) {

    }

    @Override
    public void printText(String content) {
        printText(content, TextSize.TEXT_SIZE_DEFAULT);
    }

    @Override
    public void printText(String content, int textSize) {
        printText(content, textSize, TextGravity.GRAVITY_LEFT);
    }

    @Override
    public void printText(String content, int textSize, int gravity) {
        try {
            getLocalTextSize(textSize);
            pOutputStream.write(content.getBytes(EncodingFormat.FORMAT_GBK));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void printBarCode(String content) {

    }

    @Override
    public void printBarCode(String content, int textSize) {

    }

    @Override
    public void printQRCode(String content) {
    }

    @Override
    public void printQRCode(String content, int textSize) {
    }

    @Override
    public void printImage(Bitmap bitmap) {

    }

    @Override
    public void closePrinter(Context context) {
        destroyPort();
    }

    @Override
    public void printBlankLine(int linesNum) {
        for (int num = linesNum; num > 0; num--) {
            printBlankLine();
        }
    }

    @Override
    public void printBlankLine() {
        try {
            pOutputStream.write('\n');
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void cutPaper() {
        try {
            pOutputStream.write(PrintCmd.CutByte());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void startPrint(Context context) {
//        printWrite(printStr.toString());
        try {
            Thread.sleep(1000);//保证打印完成的睡
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        destroyPort();
    }

    @Override
    public void openCashBox() {
//        pOutputStream.write();
    }

    @Override
    public boolean isConnected() {
        return false;
    }

    @Override
    public boolean isPrinterReady() {
        return false;
    }


    private SerialPort getSerialPort() {
        try {
            if (mSerialPort == null) {
                mSerialPort = new SerialPort(new File(SERIAL_PROT_FILE_PATH), 9600, 0);
            }
        } catch (SecurityException e) {
        } catch (IOException e) {
        }
        return mSerialPort;
    }

    private void destroyPort() {
        if (mSerialPort != null) {
            mSerialPort.close();
            mSerialPort = null;
        }
    }


    /**
     * 将通用字体大小转成本打印机实体类使用的字体大小
     *
     * @param textSize
     * @return
     */
    private void getLocalTextSize(int textSize) {
        switch (textSize) {
            case TextSize.TEXT_SIZE_DOWN_2:
            case TextSize.TEXT_SIZE_DOWN_1:
            case TextSize.TEXT_SIZE_DEFAULT: {
                try {
                    pOutputStream.write(PrintCmd.initByte());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }

            case TextSize.TEXT_SIZE_UP_3: {
                try {
                    pOutputStream.write(PrintCmd.sizeBytes(1,1));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }

            case TextSize.TEXT_SIZE_UP_4: {
                try {
                    pOutputStream.write(PrintCmd.sizeBytes(2,2));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }

            default: {
                try {
                    pOutputStream.write(PrintCmd.sizeBytes(1,0));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    /**
     * 将通用布局位置转成本打印机实体类使用的布局位置
     *
     * @param gravity
     */
    private void getLocalGravity(int gravity) {

    }
}
