package com.bk.bkprintmodulelib.factory;

import android.graphics.Bitmap;

import com.bk.bkprintmodulelib.cosntants.CommandType;
import com.bk.bkprintmodulelib.print_help.PrintLineContentEntity;


/**
 * 打印内容工厂
 */
public class PrintContentFactory {


    public static PrintLineContentEntity createText(String text, int textSize, int textGravity) {
        PrintLineContentEntity printLineContentEntity = new PrintLineContentEntity();
        printLineContentEntity.setCommand(CommandType.CMMAND_TEXT);
        printLineContentEntity.setContent(text);
        printLineContentEntity.setTextGrive(textGravity);
        printLineContentEntity.setTextSize(textSize);
        return printLineContentEntity;
    }


    public static PrintLineContentEntity createText(String text, int textSize) {
        PrintLineContentEntity printLineContentEntity = new PrintLineContentEntity();
        printLineContentEntity.setCommand(CommandType.CMMAND_TEXT);
        printLineContentEntity.setContent(text);
        printLineContentEntity.setTextSize(textSize);
        return printLineContentEntity;
    }

    public static PrintLineContentEntity createText(String text) {
        PrintLineContentEntity printLineContentEntity = new PrintLineContentEntity();
        printLineContentEntity.setCommand(CommandType.CMMAND_TEXT);
        printLineContentEntity.setContent(text);
        return printLineContentEntity;
    }


    public static PrintLineContentEntity createBlankLine(int nums) {
        PrintLineContentEntity printLineContentEntity = new PrintLineContentEntity();
        printLineContentEntity.setCommand(CommandType.CMMAND_BLANKLINE);
        printLineContentEntity.setLineNums(nums);
        return printLineContentEntity;
    }


    public static PrintLineContentEntity createBlankLine() {
        return createBlankLine(1);
    }


    public static PrintLineContentEntity createQRCode(String content) {
        PrintLineContentEntity printLineContentEntity = new PrintLineContentEntity();
        printLineContentEntity.setCommand(CommandType.CMMAND_QRCODE);
        printLineContentEntity.setContent(content);
        return printLineContentEntity;
    }


    public static PrintLineContentEntity createQRCode(String content, int textSize) {
        PrintLineContentEntity printLineContentEntity = new PrintLineContentEntity();
        printLineContentEntity.setCommand(CommandType.CMMAND_QRCODE);
        printLineContentEntity.setContent(content);
        printLineContentEntity.setTextSize(textSize);
        return printLineContentEntity;
    }


    public static PrintLineContentEntity createBarCode(String content) {
        PrintLineContentEntity printLineContentEntity = new PrintLineContentEntity();
        printLineContentEntity.setCommand(CommandType.CMMAND_BARCODE);
        printLineContentEntity.setContent(content);
        return printLineContentEntity;
    }


    public static PrintLineContentEntity createBarCode(String content, int textSize) {
        PrintLineContentEntity printLineContentEntity = new PrintLineContentEntity();
        printLineContentEntity.setCommand(CommandType.CMMAND_BARCODE);
        printLineContentEntity.setContent(content);
        printLineContentEntity.setTextSize(textSize);
        return printLineContentEntity;
    }


    public static PrintLineContentEntity createImage(Bitmap content, int textSize) {
        PrintLineContentEntity printLineContentEntity = new PrintLineContentEntity();
        printLineContentEntity.setCommand(CommandType.CMMAND_IMAGE);
        printLineContentEntity.setBitmap(content);
        printLineContentEntity.setTextSize(textSize);
        return printLineContentEntity;
    }

    public static PrintLineContentEntity createImage(Bitmap content) {
        PrintLineContentEntity printLineContentEntity = new PrintLineContentEntity();
        printLineContentEntity.setCommand(CommandType.CMMAND_IMAGE);
        printLineContentEntity.setBitmap(content);
        return printLineContentEntity;
    }


    public static PrintLineContentEntity createOpenCashBox() {
        PrintLineContentEntity printLineContentEntity = new PrintLineContentEntity();
        printLineContentEntity.setCommand(CommandType.CMMAND_CASHBOX);
        return printLineContentEntity;
    }


    public static PrintLineContentEntity createCutPage() {
        PrintLineContentEntity printLineContentEntity = new PrintLineContentEntity();
        printLineContentEntity.setCommand(CommandType.CMMAND_CUT_PAGE);
        return printLineContentEntity;
    }


    public static PrintLineContentEntity createStartCommand() {
        PrintLineContentEntity printLineContentEntity = new PrintLineContentEntity();
        printLineContentEntity.setCommand(CommandType.CMMAND_START_PRINT);
        return printLineContentEntity;
    }


}
