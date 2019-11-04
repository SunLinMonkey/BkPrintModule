package com.bk.bkprintmodulelib.print_help;

import android.graphics.Bitmap;

import com.bk.bkprintmodulelib.anotation.AnotationCommandType;


/**
 * 打印内容单条容器，由模板解析来。
 */
public class PrintLineContentEntity {

    /**
     * 命令类型
     */
    private int command;

    /**
     * 字体大小
     */
    private int textSize;

    /**
     * 字体位置
     */
    private int textGrive;

    /**
     * 打印内容
     */
    private String content;

    /**
     * 改行连续重复打印几次
     */
    private int lineNums;

    /**
     * 需要打印的图片
     */
    private Bitmap bitmap;


    public PrintLineContentEntity() {

    }

    public PrintLineContentEntity(int command, int textSize, int textGrive, String content, int lineNums, Bitmap bitmap) {
        this.command = command;
        this.textSize = textSize;
        this.textGrive = textGrive;
        this.content = content;
        this.lineNums = lineNums;
        this.bitmap = bitmap;
    }

    public int getCommand() {
        return command;
    }

    public void setCommand(@AnotationCommandType int command) {
        this.command = command;
    }

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public int getTextGrive() {
        return textGrive;
    }

    public void setTextGrive(int textGrive) {
        this.textGrive = textGrive;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getLineNums() {
        return lineNums;
    }

    public void setLineNums(int lineNums) {
        this.lineNums = lineNums;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
