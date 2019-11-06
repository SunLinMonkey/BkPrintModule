package com.bk.bkprintmodulelib.print_help;

import android.graphics.Bitmap;

import com.bk.bkprintmodulelib.anotation.AnotationCommandType;
import com.bk.bkprintmodulelib.factory.HelpEntityFactory;


/**
 * 打印内容单条容器，由模板解析来。
 */
public class PrintLineContentEntity {

    /**
     * 命令类型
     */
    private int command;

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

    /**
     * 打印辅助属性对象
     */
    private HelpEntity helpEntity;

    public PrintLineContentEntity() {

    }

    public PrintLineContentEntity(int command, HelpEntity helpEntity, String content, int lineNums, Bitmap bitmap) {
        this.command = command;
        this.helpEntity = helpEntity;
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

    public HelpEntity getHelpEntity() {
        if (helpEntity==null){
            return HelpEntityFactory.getDefaultHelpEntity();
        }
        return helpEntity;
    }

    public void setHelpEntity(HelpEntity helpEntity) {
        this.helpEntity = helpEntity;
    }
}
