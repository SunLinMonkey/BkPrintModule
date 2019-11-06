package com.bk.bkprintmodulelib.printer.parter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;


import com.bk.bkprintmodulelib.cosntants.PrintCmd;
import com.bk.bkprintmodulelib.cosntants.TextGravity;
import com.bk.bkprintmodulelib.cosntants.TextSize;
import com.bk.bkprintmodulelib.factory.HelpEntityFactory;
import com.bk.bkprintmodulelib.print_help.AbstractPrintStatus;
import com.bk.bkprintmodulelib.print_help.HelpEntity;
import com.bk.bkprintmodulelib.print_help.IPrinter;
import com.bk.bkprintmodulelib.printer.BasePrinter;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import android_serialport_api.SerialPort;

public class PartnerPrinter extends BasePrinter implements IPrinter {

    private static final String TAG = "9527";
    private SerialPort mSerialPort = null;
    private OutputStream pOutputStream;
    private StringBuffer printStr;

    @Override
    public void initPrintDriver(Context context, AbstractPrintStatus listener) {
        pOutputStream = mSerialPort.getOutputStream();
        printStr = new StringBuffer();
        listener.onPrinterInitSucceed();
    }

    @Override
    public void initPrintConnection(Context context, AbstractPrintStatus listener) {
        String deviceConnectString = SocketTool.getFilePath();
        if ("error".equals(deviceConnectString)) {
            SocketTool.getFilePath();
            listener.onConnectFailed("", "");
        } else {
            mSerialPort = getPartnerSerialPort();
            listener.onConnectSucceed();
        }
    }

    @Override
    public void resetPrintConnection(Context context, AbstractPrintStatus listener) {

    }

    @Override
    public void printText(String content) {
        try {
            pOutputStream.write(content.getBytes("GBK"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void printBarCode(String content) {

    }


    @Override
    public void printQRCode(String content) {
        print2DBarcode(content);
    }


    @Override
    public void printImage(Bitmap bitmap) {

    }

    @Override
    public void closePrinter(Context context) {
        if (pOutputStream != null) {
            try {
                pOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (mSerialPort != null) {
            mSerialPort.close();
            mSerialPort = null;
        }

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

    }

    @Override
    public void openCashBox() {
        try {
            pOutputStream.write(PrintCmd.openPartnerBoxByte());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isConnected() {
        return false;
    }

    @Override
    public boolean isPrinterReady() {
        return false;
    }

    private SerialPort getPartnerSerialPort() {
        try {
            if (mSerialPort == null) {
                if (mSerialPort == null) {
                    String deviceConnectString = SocketTool.getFilePath();
                    mSerialPort = new SerialPort(new File("/dev/" + deviceConnectString), 57600, 0);
                }
            }
        } catch (SecurityException e) {
        } catch (Exception e) {
        }
        return mSerialPort;
    }

    /**
     * 拍档打印二维码指令、方法
     *
     * @param str
     * @return
     */
    public int print2DBarcode(String str) {
        byte[] data;
        try {
            data = str.getBytes("gbk");
            byte[] buffer = new byte[11];
            buffer[0] = 29;
            buffer[1] = 90;
            buffer[2] = (byte) 2;
            buffer[4] = 27;
            buffer[5] = 90;
            buffer[6] = (byte) 10;
            buffer[7] = (byte) 0x51;
            buffer[8] = (byte) 3;
            buffer[9] = (byte) (data.length % 256);
            buffer[10] = (byte) (data.length / 256);
            pOutputStream.write(buffer);
            pOutputStream.write(data);
            pOutputStream.write(new byte[]{10});
            return 1;
        } catch (Exception e) {
            Log.d("拍档打印", "SetWhitePrint " + e.getMessage());
        }
        return -1;
    }

    @Override
    protected void getLocalTextSize() {

    }

    @Override
    protected void getLocalGravity() {

    }
}
