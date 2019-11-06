package com.bk.bkprintmodulelib;

import android.app.Application;
import android.content.Context;
import android.support.v4.print.PrintHelper;
import android.util.Log;
import android.util.SparseArray;


import com.bk.bkprintmodulelib.anotation.AnotationPrinterType;
import com.bk.bkprintmodulelib.cosntants.CommandType;
import com.bk.bkprintmodulelib.cosntants.TextGravity;
import com.bk.bkprintmodulelib.factory.AbsDriversFactory;
import com.bk.bkprintmodulelib.factory.DriversFactory;
import com.bk.bkprintmodulelib.factory.HelpEntityFactory;
import com.bk.bkprintmodulelib.print_help.AbstractPrintStatus;
import com.bk.bkprintmodulelib.print_help.HelpEntity;
import com.bk.bkprintmodulelib.print_help.IPrintDataAnalysis;
import com.bk.bkprintmodulelib.print_help.IPrinter;
import com.bk.bkprintmodulelib.print_help.PrintLineContentEntity;
import com.bk.bkprintmodulelib.support_cp.DataChannel;
import com.bk.bkprintmodulelib.support_cp.DataChannelFIFOImpl;
import com.bk.bkprintmodulelib.support_cp.ProductRunnable;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PrinterManager {
    private static final String TAG = "9527";

    private SparseArray<IPrinter> pekonPrinters;
    private boolean isInLoopModel; //是否是生产消费模式
    private ExecutorService produceSingleThreadExecutor;
    private ExecutorService consumerSingleThreadExecutor;

    private DataChannel channel;

    private PrinterManager() {

    }

    /**
     * 初始化打印对象
     *
     * @param application
     */
    public void init(Context application) {
        AbsDriversFactory driversFactory = new DriversFactory();
        pekonPrinters = driversFactory.getPekonPrinter(application);
    }

    /**
     * 重新获取打印机类型
     *
     * @param application
     */
    public void reGetPrinter(Application application) {
        AbsDriversFactory driversFactory = new DriversFactory();
        pekonPrinters = driversFactory.getPekonPrinter(application.getApplicationContext());
    }


    private static class SingleTonHolder {
        private static PrinterManager INSTANCE = new PrinterManager();
    }

    public static PrinterManager getInstance() {
        return SingleTonHolder.INSTANCE;
    }

    public IPrinter getPrint() {
        return pekonPrinters.valueAt(0);
    }


    /**
     * 开启生产消费模式
     */
    public void prepareLoop(Context context, AbstractPrintStatus abstractPrintStatus) {
        if (null == channel) {
            channel = new DataChannelFIFOImpl();
        }

        if (null == produceSingleThreadExecutor) {
            produceSingleThreadExecutor = Executors.newSingleThreadExecutor();
        }

        if (null == consumerSingleThreadExecutor) {
            consumerSingleThreadExecutor = Executors.newSingleThreadExecutor();
        }

        isInLoopModel = true;
        IPrinter pekonPrinter = pekonPrinters.valueAt(0);
        doPrinterWithCheck(context, null, abstractPrintStatus, pekonPrinter);

        consumerSingleThreadExecutor.execute(getPrintRunnable(pekonPrinter, context, channel, abstractPrintStatus));
    }

    /**
     * 开启生产消费模式 下 生产打印内容
     */
    public void addPrintContent(IPrintDataAnalysis iPrintDataAnalysis) {
        if (isInLoopModel) {
            produceSingleThreadExecutor.execute(new ProductRunnable(iPrintDataAnalysis, channel));
        }
    }


    /**
     * 结束生产消费模式
     */
    public void loopEnd() {
        isInLoopModel = false;
        if (produceSingleThreadExecutor != null) {
            produceSingleThreadExecutor.shutdown();
        }

        if (consumerSingleThreadExecutor != null) {
            consumerSingleThreadExecutor.shutdown();
        }

        channel = null;
    }

    /**
     * 系统默认配置的打印
     *
     * @param context
     * @param iPrintDatas
     * @param abstractPrintStatus
     */
    public void printContent(Context context, IPrintDataAnalysis iPrintDatas, AbstractPrintStatus abstractPrintStatus) {

        if (isInLoopModel) {
            //生产者消费者模式不能掉用这个方法
//            throw new RuntimeException("生产者消费者模式下，该方法不能调用。");
            return;
        }

        if (pekonPrinters == null || pekonPrinters.size() <= 0) {
            return;
        }

        for (int i = 0; i < pekonPrinters.size(); i++) {
            IPrinter pekonPrinter = pekonPrinters.valueAt(i);
            doPrinterWithCheck(context, iPrintDatas, abstractPrintStatus, pekonPrinter);
        }
    }

    /**
     * 指定打印方式打印
     *
     * @param printerType
     * @param context
     * @param iPrintDatas
     * @param abstractPrintStatus
     */
    public void printContent(Context context, @AnotationPrinterType int printerType, IPrintDataAnalysis iPrintDatas, AbstractPrintStatus abstractPrintStatus) {
        if (pekonPrinters.size() <= 0) {
            return;
        }
        IPrinter pekonPrinter = pekonPrinters.get(printerType);
        if (pekonPrinter == null) {
            Log.e("9527", "不支持或未配置改打印方式");
            return;
        }
        doPrinterWithCheck(context, iPrintDatas, abstractPrintStatus, pekonPrinter);

    }

    /**
     * 带校验的打印
     *
     * @param context
     * @param iPrintDatas
     * @param abstractPrintStatus
     * @param pekonPrinter
     */
    private void doPrinterWithCheck(Context context, IPrintDataAnalysis iPrintDatas, AbstractPrintStatus abstractPrintStatus, IPrinter pekonPrinter) {
        if (!pekonPrinter.isConnected()) {
            doDriversConnection(context, iPrintDatas, abstractPrintStatus, pekonPrinter);
            return;
        }

        if (!pekonPrinter.isPrinterReady()) {
            doInitPrinter(context, iPrintDatas, abstractPrintStatus, pekonPrinter);
        } else {
            printContentAfterPrinterInit(context, pekonPrinter, iPrintDatas, abstractPrintStatus);
        }

    }

    /**
     * 连接驱动服务
     *
     * @param context
     * @param iPrintDatas
     * @param abstractPrintStatus
     * @param pekonPrinter
     */
    private void doDriversConnection(final Context context, final IPrintDataAnalysis iPrintDatas, final AbstractPrintStatus abstractPrintStatus, final IPrinter pekonPrinter) {
        pekonPrinter.initPrintConnection(context, new AbstractPrintStatus() {
            @Override
            public void onPrinterFinished() {

            }

            @Override
            public void onPrintFailed(String errorCode, String errorMessage) {

            }

            @Override
            public void onConnectSucceed() {
                Log.e("9527", "2222onConnectSucceed: ");
                super.onConnectSucceed();
                doInitPrinter(context, iPrintDatas, abstractPrintStatus, pekonPrinter);
            }

            @Override
            public void onConnectFailed(String errorCode, String errorMessage) {
                super.onConnectFailed(errorCode, errorMessage);
            }
        });
    }

    /**
     * 初始化打印机
     *
     * @param context
     * @param iPrintDatas
     * @param abstractPrintStatus
     * @param pekonPrinter
     */
    private void doInitPrinter(final Context context, final IPrintDataAnalysis iPrintDatas, final AbstractPrintStatus abstractPrintStatus, final IPrinter pekonPrinter) {
        initPrinter(pekonPrinter, context, new AbstractPrintStatus() {
            @Override
            public void onPrinterFinished() {

            }

            @Override
            public void onPrintFailed(String errorCode, String errorMessage) {

            }

            @Override
            public void onPrinterInitFailed(String errorCode, String errorMessage) {
                super.onPrinterInitFailed(errorCode, errorMessage);
            }

            @Override
            public void onPrinterInitSucceed() {
                super.onPrinterInitSucceed();
                printContentAfterPrinterInit(context, pekonPrinter, iPrintDatas, abstractPrintStatus);
            }
        });
    }

    /**
     * 在打印机初始化后，直接打印
     *
     * @param pekonPrinter
     * @param context
     * @param iPrintDatas
     * @param abstractPrintStatus
     */
    private void printContentAfterPrinterInit(Context context, IPrinter pekonPrinter, IPrintDataAnalysis iPrintDatas, AbstractPrintStatus abstractPrintStatus) {
        if (!isInLoopModel) {//生产者消费者模式下，不需要直接打印
            printTestContent(pekonPrinter, context, iPrintDatas, abstractPrintStatus);
        }
    }


    private void printTestContent(IPrinter pekonPrinter, Context context, IPrintDataAnalysis iPrintDatas, AbstractPrintStatus abstractPrintStatus) {
        doPrint(context, pekonPrinter, iPrintDatas, abstractPrintStatus);
    }

    /**
     * 正式开始解析打印
     *
     * @param pekonPrinter
     * @param context
     * @param iPrintDatas
     * @param abstractPrintStatus
     */
    private void doPrint(Context context, IPrinter pekonPrinter, IPrintDataAnalysis iPrintDatas, AbstractPrintStatus abstractPrintStatus) {
        new Thread(getPrintRunnable(pekonPrinter, context, iPrintDatas, abstractPrintStatus)).run();
    }


    /**
     * 生产者消费者模式-获取打印内容
     *
     * @param pekonPrinter
     * @param context
     * @param channel
     * @param abstractPrintStatus
     * @return
     */
    private Runnable getPrintRunnable(final IPrinter pekonPrinter, final Context context, final DataChannel channel, final AbstractPrintStatus abstractPrintStatus) {
        return new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        IPrintDataAnalysis iPrintDataAnalysis = channel.get();
                        List<PrintLineContentEntity> printDatas = iPrintDataAnalysis.getPrintDatas();
                        for (PrintLineContentEntity printData : printDatas) {
                            analysisContent(context, pekonPrinter, printData);
                        }
                        abstractPrintStatus.onPrinterFinished();
                    }

                } catch (Exception e) {
                    abstractPrintStatus.onPrintFailed("", "");
                }
            }
        };
    }


    private Runnable getPrintRunnable(final IPrinter pekonPrinter, final Context context, final IPrintDataAnalysis iPrintDatas, final AbstractPrintStatus abstractPrintStatus) {
        return new Runnable() {
            @Override
            public void run() {
                try {
                    List<PrintLineContentEntity> printDatas = iPrintDatas.getPrintDatas();
                    for (PrintLineContentEntity printData : printDatas) {
                        analysisContent(context, pekonPrinter, printData);
                    }
                    abstractPrintStatus.onPrinterFinished();
                } catch (Exception e) {
                    abstractPrintStatus.onPrintFailed("", "");
                }
            }
        };
    }


    /**
     * 解析-分发打印行内容
     *
     * @param context
     * @param pekonPrinter           打印机
     * @param printLineContentEntity 打印行对象
     */
    private void analysisContent(Context context, IPrinter pekonPrinter, PrintLineContentEntity printLineContentEntity) {

        //这里的case 记得排序，常用的在前面，不常用的在后面。能稍微提升速度
        switch (printLineContentEntity.getCommand()) {
            case CommandType.CMMAND_TEXT: {
                pekonPrinter.setPrintHelpData(printLineContentEntity.getHelpEntity());
                pekonPrinter.printText(printLineContentEntity.getContent());
                break;
            }
            case CommandType.CMMAND_BLANKLINE: {
                pekonPrinter.printBlankLine(printLineContentEntity.getLineNums());
                break;
            }
            case CommandType.CMMAND_DEVIDERLINE: {
                //todo  这里打印 一行直线，未确定是模板里面设置还是直接打印实体里面加。
                break;
            }

            case CommandType.CMMAND_CUT_PAGE: {
                pekonPrinter.cutPaper();
                break;
            }

            case CommandType.CMMAND_QRCODE: {
                pekonPrinter.setPrintHelpData(printLineContentEntity.getHelpEntity());
                pekonPrinter.printQRCode(printLineContentEntity.getContent());
                break;
            }

            case CommandType.CMMAND_BARCODE: {
                pekonPrinter.setPrintHelpData(printLineContentEntity.getHelpEntity());
                pekonPrinter.printBarCode(printLineContentEntity.getContent());
                break;
            }

            case CommandType.CMMAND_IMAGE: {
                pekonPrinter.setPrintHelpData(printLineContentEntity.getHelpEntity());
                pekonPrinter.printImage(printLineContentEntity.getBitmap());
                break;
            }

            case CommandType.CMMAND_START_PRINT: {
                pekonPrinter.startPrint(context);
                break;
            }

            case CommandType.CMMAND_CASHBOX: {
                pekonPrinter.openCashBox();
                break;
            }
            default: {
                break;
            }
        }
    }


    private void initPrinter(IPrinter pekonPrinter, Context context, AbstractPrintStatus abstractPrintStatus) {
        pekonPrinter.initPrintDriver(context, abstractPrintStatus);
    }


}
