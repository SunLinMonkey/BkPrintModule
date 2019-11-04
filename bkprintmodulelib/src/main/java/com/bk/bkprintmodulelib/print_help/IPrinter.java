package com.example.pekonprinter.printer_help;

import android.content.Context;
import android.graphics.Bitmap;

import com.example.pekonprinter.annotation.AnotationPrintTextSize;
import com.example.pekonprinter.annotation.AnotationTextGravity;


public interface IPrinter {

    /**
     * 初始化打印
     */
    void initPrintDriver(Context context, AbstractPrintStatus listener);

    /**
     * 初始化打印机连接
     */
    void initPrintConnection(Context context, AbstractPrintStatus listener);


    /**
     * 重新连接打印机
     */
    void resetPrintConnection(Context context, AbstractPrintStatus listener);

    /**
     * 打印文字
     *
     * @param content 打印内容
     */
    void printText(String content);

    /**
     * 打印文字
     *
     * @param content  打印内容
     * @param textSize 打印字体大小
     */
    void printText(String content, @AnotationPrintTextSize int textSize);


    /**
     * 打印文字
     *
     * @param content  打印内容
     * @param textSize 打印字体大小
     * @param gravity 打印位置
     */
    void printText(String content, @AnotationPrintTextSize int textSize, @AnotationTextGravity int gravity);
    /**
     * 打印一维码
     *
     * @param content 打印内容
     */
    void printBarCode(String content);

    /**
     * 打印一维码
     *
     * @param content  打印内容
     * @param textSize 打印一维码大小
     */
    void printBarCode(String content, @AnotationPrintTextSize int textSize);

    /**
     * 打印二维码
     *
     * @param content 打印内容
     */
    void printQRCode(String content);

    /**
     * 打印二维码
     *
     * @param content  打印内容
     * @param textSize 打印二维码大小
     */
    void printQRCode(String content, @AnotationPrintTextSize int textSize);


    void printImage(Bitmap bitmap);


    void closePrinter(Context context);


    /**
     * 打印多行空白行
     *
     * @param linesNum 行数
     */
    void printBlankLine(int linesNum);

    /**
     * 打印空白行
     */
    void printBlankLine();


    /**
     * 切纸
     */
    void cutPaper();

    /**
     * 开始打印
     */
    void startPrint(Context context);

    /**
     * 开钱箱
     */
    void openCashBox();

    /**
     * 驱动服务是否已连接
     *
     * @return
     */
    boolean isConnected();

    /**
     * 打印机是否准备好
     *
     * @return
     */
    boolean isPrinterReady();
}
