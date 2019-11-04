package com.example.pekonprinter.printer.usb;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.RequiresApi;


import com.example.pekonprinter.constans.PrintCmd;
import com.example.pekonprinter.constans.TextSize;
import com.example.pekonprinter.printer_help.AbstractPrintStatus;
import com.example.pekonprinter.printer_help.IPrinter;
import com.example.pekonprinter.printer_help.PrintCallBack;

import java.io.UnsupportedEncodingException;

public class USBPrinter implements IPrinter {

    private USBPrintUtil usbPrintUtil;
    private StringBuffer printMessage;
    private AbstractPrintStatus listener;

    private boolean isConnected = false;
    private boolean isPrinterReady = false;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void initPrintDriver(Context context, final AbstractPrintStatus listener) {
        printMessage = new StringBuffer();
        this.listener = listener;
        usbPrintUtil = new USBPrintUtil(context);
        usbPrintUtil.createConn(context, new PrintCallBack() {
            @Override
            public void onSucceed() {
                listener.onPrinterInitSucceed();
                isPrinterReady = true;
            }

            @Override
            public void onFailed(String errorCode, String errorMessage) {
                listener.onPrinterInitFailed(errorCode,errorMessage);
                isPrinterReady = false;
            }
        });

    }

//      usbPrintUtil.printMessage(mContext,printStr.toString());
//                usbPrintUtil.closeConnection(mContext);

    @Override
    public void initPrintConnection(Context context, AbstractPrintStatus listener) {
        listener.onConnectSucceed();
        isConnected = true;
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

        printMessage.append(PrintCmd.size(1,1));

        printMessage.append(content);
    }

    @Override
    public void printText(String content, int textSize, int gravity) {

    }

    @Override
    public void printBarCode(String content) {

    }

    @Override
    public void printBarCode(String content, int textSize) {

    }

    @Override
    public void printQRCode(String content) {
        try {
            printMessage.append(PrintCmd.generate2DBarcodePartner(content.getBytes("GBK").length,8));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void printQRCode(String content, int textSize) {

    }

    @Override
    public void printImage(Bitmap bitmap) {

    }

    @Override
    public void closePrinter(Context context) {
        if (usbPrintUtil!=null){
            usbPrintUtil.closeConnection(context);
        }
    }

    @Override
    public void printBlankLine(int linesNum) {
        for (int num = linesNum; num > 0; num--) {
            printBlankLine();
        }
    }

    @Override
    public void printBlankLine() {
        printMessage.append("\n\n\n");
    }

    @Override
    public void cutPaper() {

    }

    @Override
    public void startPrint(Context context) {
        if (printMessage==null){
            listener.onPrintFailed("","");
            return;
        }
        usbPrintUtil.printMessage(context,printMessage.toString());
    }

    @Override
    public void openCashBox() {

    }

    @Override
    public boolean isConnected() {
        return isConnected;
    }

    @Override
    public boolean isPrinterReady() {
        return isPrinterReady;
    }
}
