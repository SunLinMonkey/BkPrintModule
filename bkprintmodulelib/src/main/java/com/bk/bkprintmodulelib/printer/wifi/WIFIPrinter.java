package com.bk.bkprintmodulelib.printer.wifi;

import android.content.Context;
import android.graphics.Bitmap;


import com.bk.bkprintmodulelib.cosntants.PrintCmd;
import com.bk.bkprintmodulelib.cosntants.TextGravity;
import com.bk.bkprintmodulelib.cosntants.TextSize;
import com.bk.bkprintmodulelib.print_help.AbstractPrintStatus;
import com.bk.bkprintmodulelib.print_help.IPrinter;
import com.bk.bkprintmodulelib.printer.startwifi.PrinterSettingManager;
import com.bk.bkprintmodulelib.printer.startwifi.PrinterSettings;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;


/**
 * 这里有点特殊，有两种类型的打印。都特么叫WiFi,我就放一起了。算是个秉坤化的东西把
 */
public class WIFIPrinter implements IPrinter {

    private StringBuffer printString;

    @Override
    public void initPrintDriver(Context context, AbstractPrintStatus listener) {
        printString = new StringBuffer();
        printString.append(PrintCmd.initialPrint());
        listener.onPrinterInitSucceed();
    }

    @Override
    public void initPrintConnection(Context context, AbstractPrintStatus listener) {
        PrinterSettingManager settingManager = new PrinterSettingManager(context);
        PrinterSettings settings = settingManager.getPrinterSettings();
        if (settings == null) {
            listener.onConnectFailed("", "请连接打印机");
            return;
        }
        listener.onConnectSucceed();
    }

    @Override
    public void resetPrintConnection(final Context context, AbstractPrintStatus listener) {

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
        getLocalGravity(gravity);
        printString.append(content);
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

    }

    @Override
    public void printBlankLine(int linesNum) {
        for (int num = linesNum; num > 0; num--) {
            printBlankLine();
        }
    }

    @Override
    public void printBlankLine() {
        printString.append(" \n");
    }

    @Override
    public void cutPaper() {
        printString.append(PrintCmd.CutString());
    }

    @Override
    public void startPrint(Context context) {
        boolean isUDP = false;
        if (isUDP) {
            new BroadCastUdp(printString.toString()).start();
        } else {
            startPrintWifi();
        }
    }

    private void startPrintWifi() {
        try {
            Socket printClient = null;
            PrintWriter printWrite = null;
            BufferedReader printRead = null;

            printClient = getSocket();

            if (printClient.isConnected()) {
                printRead = new BufferedReader(new InputStreamReader(printClient.getInputStream()));
                printWrite = new PrintWriter(
                        new BufferedWriter(new OutputStreamWriter(printClient.getOutputStream(), "GBK")), true);

                if (!printClient.isOutputShutdown()) {
                    printWrite.println("");// 第一行为空不然不会打印
                    printWrite.println(printString.toString());
                }
            }

            printRead.close();
            printWrite.close();
            printClient.close();

        } catch (UnknownHostException e) {
            // 连接失败
            e.printStackTrace();
        } catch (SocketTimeoutException e) {
            // 连接超时
            e.printStackTrace();
        } catch (IOException e) {
            // 连接失败
            e.printStackTrace();
        }
    }

    private Socket getSocket() throws IOException {
        String strWifiPrintIp = null;//SharedPrefUtil.getWifiPrintIp(cxt);//todo 获取存的IP
        if (strWifiPrintIp == null || "".equals(strWifiPrintIp)) {
            strWifiPrintIp = "192.168.1.87:9100";
        }
        String[] address = strWifiPrintIp.split(":");
        String ipAddress = address[0];
        String tmpPort = address[1];
        int port = Integer.parseInt(tmpPort);
        return new Socket(ipAddress, port);
    }

    @Override
    public void openCashBox() {
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
     * 将通用布局位置转成本打印机实体类使用的布局位置
     *
     * @param gravity
     */
    private void getLocalGravity(int gravity) {
        switch (gravity) {
            case TextGravity.GRAVITY_LEFT: {
                printString.append(PrintCmd.alignLeft());
                break;
            }
            case TextGravity.GRAVITY_RIGHT: {
                printString.append(PrintCmd.alignRight());
                break;
            }
            case TextGravity.GRAVITY_CENTER: {
                printString.append(PrintCmd.alignCenter());
                break;
            }
        }
    }

}
