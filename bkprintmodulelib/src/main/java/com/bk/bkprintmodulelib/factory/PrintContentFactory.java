package com.bk.bkprintmodulelib.factory;

import android.graphics.Bitmap;

import com.bk.bkprintmodulelib.cosntants.CommandType;
import com.bk.bkprintmodulelib.print_help.HelpEntity;
import com.bk.bkprintmodulelib.print_help.PrintLineContentEntity;


/**
 * 打印内容工厂
 */
public class PrintContentFactory {

    public static PrintLineContentEntity createText(String text, int textSize, int textGravity, boolean isNeedUnderLine, boolean isNeedBold) {
        PrintLineContentEntity printLineContentEntity = new PrintLineContentEntity();
        printLineContentEntity.setCommand(CommandType.CMMAND_TEXT);
        printLineContentEntity.setContent(text);
        HelpEntity defaultHelpEntity = HelpEntityFactory.getDefaultHelpEntity();
        defaultHelpEntity.setGrivaty(textGravity);
        defaultHelpEntity.setBold(isNeedBold);
        defaultHelpEntity.setNeedUnderLine(isNeedUnderLine);
        defaultHelpEntity.setTestSize(textSize);
        printLineContentEntity.setHelpEntity(defaultHelpEntity);
        return printLineContentEntity;
    }


    public static PrintLineContentEntity createText(String text, int textSize, int textGravity) {
        PrintLineContentEntity printLineContentEntity = new PrintLineContentEntity();
        printLineContentEntity.setCommand(CommandType.CMMAND_TEXT);
        printLineContentEntity.setContent(text);
        HelpEntity defaultHelpEntity = HelpEntityFactory.getDefaultHelpEntity();
        defaultHelpEntity.setGrivaty(textGravity);
        defaultHelpEntity.setTestSize(textSize);
        printLineContentEntity.setHelpEntity(defaultHelpEntity);
        return printLineContentEntity;
    }


    public static PrintLineContentEntity createText(String text, int textSize) {
        PrintLineContentEntity printLineContentEntity = new PrintLineContentEntity();
        printLineContentEntity.setCommand(CommandType.CMMAND_TEXT);
        printLineContentEntity.setContent(text);
        HelpEntity defaultHelpEntity = HelpEntityFactory.getDefaultHelpEntity();
        defaultHelpEntity.setTestSize(textSize);
        printLineContentEntity.setHelpEntity(defaultHelpEntity);
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
        HelpEntity defaultHelpEntity = HelpEntityFactory.getDefaultHelpEntity();
        defaultHelpEntity.setTestSize(textSize);
        printLineContentEntity.setHelpEntity(defaultHelpEntity);
        return printLineContentEntity;
    }


    public static PrintLineContentEntity createBarCode(String content) {
        PrintLineContentEntity printLineContentEntity = new PrintLineContentEntity();
        printLineContentEntity.setCommand(CommandType.CMMAND_BARCODE);
        printLineContentEntity.setContent(content);
        return printLineContentEntity;
    }


    public static PrintLineContentEntity createImage(Bitmap content, int textSize) {
        PrintLineContentEntity printLineContentEntity = new PrintLineContentEntity();
        printLineContentEntity.setCommand(CommandType.CMMAND_IMAGE);
        printLineContentEntity.setBitmap(content);
        HelpEntity defaultHelpEntity = HelpEntityFactory.getDefaultHelpEntity();
        defaultHelpEntity.setTestSize(textSize);
        printLineContentEntity.setHelpEntity(defaultHelpEntity);
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
