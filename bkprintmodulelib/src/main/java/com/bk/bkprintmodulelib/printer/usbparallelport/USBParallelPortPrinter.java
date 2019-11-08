package com.bk.bkprintmodulelib.printer.usbparallelport;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.bk.bkprintmodulelib.cosntants.PrintCmd;
import com.bk.bkprintmodulelib.cosntants.TextGravity;
import com.bk.bkprintmodulelib.cosntants.TextSize;
import com.bk.bkprintmodulelib.print_help.AbstractPrintStatus;
import com.bk.bkprintmodulelib.print_help.HelpEntity;
import com.bk.bkprintmodulelib.print_help.IPrinter;
import com.bk.bkprintmodulelib.printer.BasePrinter;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import android_serialport_api.ParallelProt;


public class USBParallelPortPrinter extends BasePrinter implements IPrinter {
    private static final String TAG = "9527";

    private final String PARALLEL_PROT_FILE_PATH = "/dev/lp0";
    private ParallelProt parallelPort;
    private OutputStream pOutputStream;
    private StringBuffer printStr;

    @Override
    public void initPrintDriver(Context context, AbstractPrintStatus listener) {
        printStr = new StringBuffer();
        pOutputStream = parallelPort.getOutputStream();
        listener.onPrinterInitSucceed();
    }

    private ParallelProt getParallelPort() {
        try {
            if (parallelPort == null) {
                parallelPort = new ParallelProt(new File(PARALLEL_PROT_FILE_PATH), 9600, 0);
            }
        } catch (SecurityException e) {
        } catch (IOException e) {
        }
        return parallelPort;
    }

    @Override
    public void initPrintConnection(Context context, AbstractPrintStatus listener) {
        File usbFile = new File(PARALLEL_PROT_FILE_PATH);
        if (!usbFile.exists()) {
//            showShortToast(getString(R.string.usb_control_sole_not_exsit));
            Log.e(TAG, "initPrintConnection: 没有并口");
            //todo 不存在提醒
        } else {
            parallelPort = getParallelPort();
            listener.onConnectSucceed();
        }
    }

    @Override
    public void resetPrintConnection(Context context, AbstractPrintStatus listener) {

    }

    @Override
    public void printText(String content) {
        getLocalGravity();
        getLocalTextSize();
        printStr.append(content);
    }


    @Override
    public void printBarCode(String content) {
        getLocalGravity(TextGravity.GRAVITY_CENTER);
        getLocalTextSize(TextSize.TEXT_SIZE_UP_3);
        printStr.append(new String(PrintCmd.printBarcode(content)));
        printBlankLine();
        printBlankLine();
    }


    @Override
    public void printQRCode(String content) {

    }


    @Override
    public void printImage(Bitmap bitmap) {
    }

    @Override
    public void closePrinter(Context context) {
        destroyParallel();
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
        printStr.append("'\n'");
    }

    @Override
    public void cutPaper() {
        printBlankLine();
        printStr.append(PrintCmd.cut());
    }

    @Override
    public void startPrint(Context context) {
        try {
            printWrite(printStr.toString());
            Thread.sleep(1000);
            destroyParallel();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void openCashBox() {
        printStr.append("-n -e '\\x1b\\x70\\x00\\x3c\\xff'");// 开钱箱
    }

    @Override
    public boolean isConnected() {
        return false;
    }

    @Override
    public boolean isPrinterReady() {
        return false;
    }

    private void printWrite(String str) throws UnsupportedEncodingException, IOException {
        String printStr = "echo " + str + " > " + PARALLEL_PROT_FILE_PATH;
        try {
            pOutputStream.write(printStr.getBytes("GBK"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void destroyParallel() {
        try {
            if (parallelPort != null) {
                parallelPort = null;
            }
            if (pOutputStream != null) {
                pOutputStream.close();
            }

        } catch (IOException e) {
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
                printStr.append(PrintCmd.init());
                break;
            }

            case TextSize.TEXT_SIZE_UP_3: {
                printStr.append(PrintCmd.size(2, 2));
                break;
            }

            case TextSize.TEXT_SIZE_UP_4: {
                printStr.append(PrintCmd.size(2, 2));
                break;
            }

            default: {
                printStr.append(PrintCmd.init());
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

        switch (gravity) {
            case TextGravity.GRAVITY_CENTER: {
                printStr.append(PrintCmd.alignCenter());
                break;
            }
            case TextGravity.GRAVITY_LEFT: {
                printStr.append(PrintCmd.alignLeft());
                break;
            }
            case TextGravity.GRAVITY_RIGHT: {
                printStr.append(PrintCmd.alignRight());
                break;
            }
            default: {
                printStr.append(PrintCmd.alignCenter());
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
        getLocalGravity(getHelpEntity().getGrivaty());
    }
}
