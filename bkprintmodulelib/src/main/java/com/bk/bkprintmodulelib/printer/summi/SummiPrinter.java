package com.bk.bkprintmodulelib.printer.summi;

import android.content.Context;
import android.graphics.Bitmap;

import com.bk.bkprintmodulelib.cosntants.TextGravity;
import com.bk.bkprintmodulelib.cosntants.TextSize;
import com.bk.bkprintmodulelib.factory.HelpEntityFactory;
import com.bk.bkprintmodulelib.print_help.AbstractPrintStatus;
import com.bk.bkprintmodulelib.print_help.ConnectionCallBack;
import com.bk.bkprintmodulelib.print_help.HelpEntity;
import com.bk.bkprintmodulelib.print_help.IPrinter;
import com.bk.bkprintmodulelib.print_help.InitPrinterCallBack;
import com.bk.bkprintmodulelib.printer.BasePrinter;


/**
 * 商米AIDL 调用打印实体类
 */
public class SummiPrinter extends BasePrinter implements IPrinter {

    private final int TEXT_SIZE_23 = 23;
    private final int TEXT_SIZE_46 = 46;
    private final int TEXT_SIZE_55 = 55;
    private final int TEXT_SIZE_18 = 18;
    private final int TEXT_SIZE_12 = 12;
    private final int TEXT_SIZE_DEFAULT = TEXT_SIZE_23;


    private boolean isConnected = false;
    private boolean isPrinterReady = false;

    @Override
    public void initPrintDriver(Context context, final AbstractPrintStatus listener) {
        SummiAidlUtil.getInstance().initPrinter(new InitPrinterCallBack() {
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

    @Override
    public void initPrintConnection(final Context context, final AbstractPrintStatus listener) {
        SummiAidlUtil.getInstance().connectPrinterService(context, new ConnectionCallBack() {
            @Override
            public void onSucceed() {
                listener.onConnectSucceed();
                isConnected = true;
            }

            @Override
            public void onFailed(String errorCode, String errorMessage) {
                listener.onConnectFailed(errorCode, errorMessage);
                isConnected = false;
            }
        });
    }

    @Override
    public void resetPrintConnection(Context context, AbstractPrintStatus listener) {

    }

    @Override
    public void printText(String content) {
        HelpEntity helpEntity = getHelpEntity();
        int localTextSize = getLocalTextSize(helpEntity.getTestSize());
        SummiAidlUtil.getInstance().printText(content, localTextSize, helpEntity.isBold(), helpEntity.getGrivaty(), helpEntity.isNeedUnderLine(), null);
    }

    @Override
    public void printBarCode(String content) {
        //只有 CODE39、CODABAR、CODE93、CODE128 能打  已建立对于的BARCODETYPE 类,用的时候记得对应处理
        SummiAidlUtil.getInstance().printBarCode(content, 8, 162, 2, 2, null);
    }


    @Override
    public void printQRCode(String content) {
        SummiAidlUtil.getInstance().printQr(content, 8, 160);
    }


    @Override
    public void printImage(Bitmap bitmap) {
        SummiAidlUtil.getInstance().printBitmap(bitmap, null);
    }

    @Override
    public void closePrinter(Context context) {

    }

    @Override
    public void setPrintHelpData(HelpEntity helpEntity) {
        setHelpEntity(helpEntity);
    }

    @Override
    public void printBlankLine(int linesNum) {
        SummiAidlUtil.getInstance().print1Line(linesNum);
    }

    @Override
    public void printBlankLine() {
        SummiAidlUtil.getInstance().print1Line();
    }

    @Override
    public void cutPaper() {
        SummiAidlUtil.getInstance().cutPaper();
    }

    @Override
    public void startPrint(Context context) {

    }

    @Override
    public void openCashBox() {
        SummiAidlUtil.getInstance().openDrawer();
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
    private int getLocalTextSize(int textSize) {
        switch (textSize) {
            case TextSize.TEXT_SIZE_DOWN_2: {
                return TEXT_SIZE_12;
            }

            case TextSize.TEXT_SIZE_DOWN_1: {
                return TEXT_SIZE_18;
            }

            case TextSize.TEXT_SIZE_DEFAULT: {
                return TEXT_SIZE_23;
            }

            case TextSize.TEXT_SIZE_UP_3: {
                return TEXT_SIZE_46;
            }

            case TextSize.TEXT_SIZE_UP_4: {
                return TEXT_SIZE_55;
            }

            default: {
                return TEXT_SIZE_DEFAULT;
            }
        }
    }

    @Override
    protected void getLocalTextSize() {
//        getLocalTextSize(getHelpEntity().getTestSize());
    }

    @Override
    protected void getLocalGravity() {

    }
}
