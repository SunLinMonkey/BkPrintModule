package com.bk.bkprintmodulelib.printer.parter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.Gravity;


import com.bk.bkprintmodulelib.cosntants.PrintCmd;
import com.bk.bkprintmodulelib.cosntants.StatusConstans;
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
        listener.onPrinterInitSucceed(StatusConstans.Code.SUCCESS,"");
    }

    @Override
    public void initPrintConnection(Context context, AbstractPrintStatus listener) {
        String deviceConnectString = SocketTool.getFilePath();
        if ("error".equals(deviceConnectString)) {
            SocketTool.getFilePath();
            listener.onConnectFailed("", "");
        } else {
            mSerialPort = getPartnerSerialPort();
            listener.onConnectSucceed(StatusConstans.Code.SUCCESS,"");
        }
    }

    @Override
    public void resetPrintConnection(Context context, AbstractPrintStatus listener) {

    }

    @Override
    public void printText(String content) {
        try {
            getLocalTextSize();
            getLocalGravity();
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
        try {
            pOutputStream.write(size(getHelpEntity().getTestSize()).getBytes("gbk"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void getLocalGravity() {
        try {
            pOutputStream.write(gravity(getHelpEntity().getGrivaty()).getBytes("gbk"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * 该命令在打印区域执行的排列方式
     * 设定该命令前打印缓冲区必须无打印数据
     * 设定该命令后设定绝对打印则绝对打印无效数据按排列方式打印。
     * 设定了绝对打印再设排列方式，则排列方式无效数据按绝对打印位置打印。该命令与绝对打印关系：谁先设谁有效。
     * 注意绝对打印只是当前行有效，排列方式是设定后不改变则一直有效
     * 0或48靠左，1或49居中，2或50居右
     *
     * @param gravity 排列方式
     * @return 排列
     */
    public static String gravity(int gravity) {
        byte gravityByte = 0;

        switch (gravity) {
            case TextGravity.GRAVITY_LEFT:
                gravityByte = 0;
                break;
            case TextGravity.GRAVITY_CENTER:
                gravityByte = 1;
                break;
            case TextGravity.GRAVITY_RIGHT:
                gravityByte = 2;
                break;
            default:
                gravityByte = 0;

                break;
        }
        byte[] byteArray = new byte[3];
        byteArray[0] = 27;
        byteArray[1] = 97;
        byteArray[2] = gravityByte;
        return new String(byteArray);
    }

    /**
     * 范围(0≤n≤255)（1≤垂直倍数≤8，1≤水平倍数≤8）
     * 选择字符高度使用位0到2 和选择字符宽度使用位4到7，
     *
     * @param textSize
     * @return 字体大小
     */
    public static String size(int textSize) {

        int widthMultiple;
        int heightMultiple;
        switch (textSize) {
            case TextSize.TEXT_SIZE_DOWN_2:
            case TextSize.TEXT_SIZE_DOWN_1:
            case TextSize.TEXT_SIZE_DEFAULT: {
                heightMultiple = 1;
                widthMultiple = 1;
                break;
            }

            case TextSize.TEXT_SIZE_UP_3: {
                heightMultiple = 2;
                widthMultiple = 2;
                break;
            }

            case TextSize.TEXT_SIZE_UP_4: {
                heightMultiple = 3;
                widthMultiple = 3;
                break;
            }

            default: {
                heightMultiple = 1;
                widthMultiple = 1;
                break;
            }
        }

        byte[] byteArray = new byte[3];
        byteArray[0] = 29;
        byteArray[1] = 33;
        byteArray[2] = (byte) ((widthMultiple - 1) * 16 + (heightMultiple - 1));
        return new String(byteArray);
    }

}
