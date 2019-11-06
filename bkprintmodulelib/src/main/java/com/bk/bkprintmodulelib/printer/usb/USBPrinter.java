package com.bk.bkprintmodulelib.printer.usb;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.RequiresApi;


import com.bk.bkprintmodulelib.cosntants.PrintCmd;
import com.bk.bkprintmodulelib.cosntants.TextSize;
import com.bk.bkprintmodulelib.factory.HelpEntityFactory;
import com.bk.bkprintmodulelib.print_help.AbstractPrintStatus;
import com.bk.bkprintmodulelib.print_help.HelpEntity;
import com.bk.bkprintmodulelib.print_help.IPrinter;
import com.bk.bkprintmodulelib.print_help.PrintCallBack;
import com.bk.bkprintmodulelib.printer.BasePrinter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class USBPrinter extends BasePrinter implements IPrinter {

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
                listener.onPrinterInitFailed(errorCode, errorMessage);
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
        getLocalTextSize();
        printMessage.append(content);
    }

    @Override
    public void printBarCode(String content) {

    }


    @Override
    public void printQRCode(String content) {
        try {
            printMessage.append(PrintCmd.generate2DBarcodePartner(content.getBytes("GBK").length, 8));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void printImage(Bitmap bitmap) {

    }

    @Override
    public void closePrinter(Context context) {
        if (usbPrintUtil != null) {
            usbPrintUtil.closeConnection(context);
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
        printMessage.append("\n\n\n");
    }

    @Override
    public void cutPaper() {

    }

    @Override
    public void startPrint(Context context) {
        if (printMessage == null) {
            listener.onPrintFailed("", "");
            return;
        }
        usbPrintUtil.printMessage(context, printMessage.toString());
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
                printMessage.append(PrintCmd.size(1, 1));
                break;
            }

            case TextSize.TEXT_SIZE_UP_3: {
                printMessage.append(PrintCmd.size(2, 2));
                break;
            }

            case TextSize.TEXT_SIZE_UP_4: {
                printMessage.append(PrintCmd.size(3, 3));
                break;
            }

            default: {
                printMessage.append(PrintCmd.size(1, 1));
                break;
            }
        }
    }

    @Override
    protected void getLocalTextSize() {
        getLocalTextSize(getHelpEntity().getTestSize());
    }

    @Override
    protected void getLocalGravity() {

    }
}
