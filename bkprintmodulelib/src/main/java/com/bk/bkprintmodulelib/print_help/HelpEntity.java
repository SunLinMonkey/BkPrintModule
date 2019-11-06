package com.bk.bkprintmodulelib.print_help;

import com.bk.bkprintmodulelib.anotation.AnotationPrintTextSize;
import com.bk.bkprintmodulelib.anotation.AnotationTextGravity;

public class HelpEntity {
    /**
     * 字体大小
     */
    private int testSize;

    /**
     * 位置，居中 左 右
     */
    private int grivaty;

    /**
     * 是否加粗
     */
    private boolean isBold;

    /**
     * 是否需要下划线
     */
    private boolean isNeedUnderLine;


    public int getTestSize() {
        return testSize;
    }

    public void setTestSize(@AnotationPrintTextSize int testSize) {
        this.testSize = testSize;
    }

    public int getGrivaty() {
        return grivaty;
    }

    public void setGrivaty(@AnotationTextGravity int grivaty) {
        this.grivaty = grivaty;
    }

    public boolean isBold() {
        return isBold;
    }

    public void setBold(boolean bold) {
        isBold = bold;
    }

    public boolean isNeedUnderLine() {
        return isNeedUnderLine;
    }

    public void setNeedUnderLine(boolean needUnderLine) {
        isNeedUnderLine = needUnderLine;
    }
}
