package com.bk.bkprintmodulelib;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.SparseArray;

import com.bk.bkprintmodulelib.anotation.AnotationPrinterType;
import com.bk.bkprintmodulelib.cosntants.CommandType;
import com.bk.bkprintmodulelib.cosntants.StatusConstans;
import com.bk.bkprintmodulelib.factory.AbsDriversFactory;
import com.bk.bkprintmodulelib.factory.DriversFactory;
import com.bk.bkprintmodulelib.print_help.AbstractPrintStatus;
import com.bk.bkprintmodulelib.print_help.IPrintDataAnalysis;
import com.bk.bkprintmodulelib.print_help.IPrinter;
import com.bk.bkprintmodulelib.print_help.PrintLineContentEntity;
import com.bk.bkprintmodulelib.print_help.SharedPrefUtil;
import com.bk.bkprintmodulelib.support_cp.DataChannel;
import com.bk.bkprintmodulelib.support_cp.DataChannelFIFOImpl;
import com.bk.bkprintmodulelib.support_cp.ProductRunnable;
import com.bk.bkprintmodulelib.utils.StringUtils;

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


    private final int PRINT_FINSH = 1;
    private final int PRINT_FAILE = 2;
    private AbstractPrintStatus abstractPrintStatus;

    /**
     * 默认打印张数
     */
    private final int DEAFAULT_PRINT_NUM = 1;

    private PrinterManager() {

    }


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String code = data.getString("code");
            String message = data.getString("message");
            switch (msg.what) {
                case PRINT_FINSH:
                    if (abstractPrintStatus != null) {
                        abstractPrintStatus.onPrintFinished(code, message);
                    }

                    break;
                case PRINT_FAILE:
                    if (abstractPrintStatus != null) {
                        abstractPrintStatus.onPrintFailed(code, message);
                    }
                    break;
            }
        }
    };

    /**
     * 保存设置的主打印机
     *
     * @param context
     * @param printer
     */
    public void saveMainPrinter(Context context, @AnotationPrinterType int printer) {
        SharedPrefUtil.getInstance().setMainPrinter(printer, context);
    }

    /**
     * 保存设置的副印机
     *
     * @param context
     * @param printer
     */
    public void saveHelpPrinter(Context context, @AnotationPrinterType int printer) {
        SharedPrefUtil.getInstance().setHelpPrinter(printer, context);
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
     * 获取主打印机的类型
     *
     * @return
     */
    public int getMainPrinterType() {
        return pekonPrinters.keyAt(0);
    }

    /**
     * 获取辅助打印机的类型
     *
     * @return
     */
    public int getHelpPrinterType() {
        if (pekonPrinters.size() > 1) {
            return pekonPrinters.keyAt(1);
        }
        return -1;
    }

    /**
     * 开启生产消费模式
     */
    public void prepareLoop(Context context, AbstractPrintStatus abstractPrintStatus) {
        if (null == channel) {
            channel = new DataChannelFIFOImpl();
        }


        if (null == produceSingleThreadExecutor || produceSingleThreadExecutor.isShutdown()) {
            produceSingleThreadExecutor = Executors.newSingleThreadExecutor();
        }

        if (null == consumerSingleThreadExecutor || consumerSingleThreadExecutor.isShutdown()) {
            consumerSingleThreadExecutor = Executors.newSingleThreadExecutor();
        }

        isInLoopModel = true;
        IPrinter pekonPrinter = pekonPrinters.valueAt(0);
        doPrinterWithCheck(context, null, DEAFAULT_PRINT_NUM, abstractPrintStatus, pekonPrinter);

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
            doPrinterWithCheck(context, iPrintDatas, DEAFAULT_PRINT_NUM, abstractPrintStatus, pekonPrinter);
        }
    }

    /**
     * 系统默认配置的打印
     *
     * @param context
     * @param iPrintDatas         打印封装数据
     * @param printNum            打印次数
     * @param abstractPrintStatus
     */
    public void printContent(Context context, IPrintDataAnalysis iPrintDatas, int printNum, AbstractPrintStatus abstractPrintStatus) {

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
            doPrinterWithCheck(context, iPrintDatas, printNum, abstractPrintStatus, pekonPrinter);
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
    public void printContent(Context context, @AnotationPrinterType int printerType, IPrintDataAnalysis iPrintDatas, int printNums, AbstractPrintStatus abstractPrintStatus) {
        if (pekonPrinters.size() <= 0) {
            return;
        }
        IPrinter pekonPrinter = pekonPrinters.get(printerType);
        if (pekonPrinter == null) {
            Log.e("9527", "不支持或未配置改打印方式");
            return;
        }
        doPrinterWithCheck(context, iPrintDatas, printNums, abstractPrintStatus, pekonPrinter);

    }

    /**
     * 带校验的打印
     *
     * @param context
     * @param iPrintDatas
     * @param abstractPrintStatus
     * @param pekonPrinter
     */
    private void doPrinterWithCheck(Context context, IPrintDataAnalysis iPrintDatas, int printNums, AbstractPrintStatus abstractPrintStatus, IPrinter pekonPrinter) {
        if (!pekonPrinter.isConnected()) {
            doDriversConnection(context, iPrintDatas, printNums, abstractPrintStatus, pekonPrinter);
            return;
        }

        if (!pekonPrinter.isPrinterReady()) {
            doInitPrinter(context, iPrintDatas, printNums, abstractPrintStatus, pekonPrinter);
        } else {
            printContentAfterPrinterInit(context, pekonPrinter, iPrintDatas, printNums, abstractPrintStatus);
        }

    }

    /**
     * 连接驱动服务
     *
     * @param context
     * @param iPrintDatas
     * @param printNums
     * @param abstractPrintStatus
     * @param pekonPrinter
     */
    private void doDriversConnection(final Context context, final IPrintDataAnalysis iPrintDatas, final int printNums, final AbstractPrintStatus abstractPrintStatus, final IPrinter pekonPrinter) {
        pekonPrinter.initPrintConnection(context, new AbstractPrintStatus() {
            @Override
            public void onPrintFailed(String errorCode, String errorMessage) {

            }

            @Override
            public void onConnectSucceed(String code, String message) {
                Log.e("9527", "2222onConnectSucceed: ");
                super.onConnectSucceed(code, message);
                doInitPrinter(context, iPrintDatas, printNums, abstractPrintStatus, pekonPrinter);
            }

            @Override
            public void onConnectFailed(String errorCode, String errorMessage) {
                super.onConnectFailed(errorCode, errorMessage);
            }

            @Override
            public void onPrintFinished(String finshCode, String msg) {

            }
        });
    }

    /**
     * 初始化打印机
     *
     * @param context
     * @param iPrintDatas
     * @param printNums
     * @param abstractPrintStatus
     * @param pekonPrinter
     */
    private void doInitPrinter(final Context context, final IPrintDataAnalysis iPrintDatas, final int printNums, final AbstractPrintStatus abstractPrintStatus, final IPrinter pekonPrinter) {
        initPrinter(pekonPrinter, context, new AbstractPrintStatus() {


            @Override
            public void onPrintFailed(String errorCode, String errorMessage) {

            }

            @Override
            public void onPrinterInitFailed(String errorCode, String errorMessage) {
                super.onPrinterInitFailed(errorCode, errorMessage);
            }

            @Override
            public void onPrintFinished(String finshCode, String msg) {

            }

            @Override
            public void onPrinterInitSucceed(String code, String message) {
                super.onPrinterInitSucceed(code, message);
                printContentAfterPrinterInit(context, pekonPrinter, iPrintDatas, printNums, abstractPrintStatus);
            }
        });
    }

    /**
     * 在打印机初始化后，直接打印
     *
     * @param context
     * @param pekonPrinter
     * @param iPrintDatas
     * @param printNums
     * @param abstractPrintStatus
     */
    private void printContentAfterPrinterInit(Context context, IPrinter pekonPrinter, IPrintDataAnalysis iPrintDatas, int printNums, AbstractPrintStatus abstractPrintStatus) {
        if (!isInLoopModel) {//生产者消费者模式下，不需要直接打印
            printContent(context, pekonPrinter, iPrintDatas, printNums, abstractPrintStatus);
        }
    }


    private void printContent(Context context, IPrinter pekonPrinter, IPrintDataAnalysis iPrintDatas, int printNums, AbstractPrintStatus abstractPrintStatus) {
        doPrint(context, pekonPrinter, iPrintDatas, printNums, abstractPrintStatus);
    }

    /**
     * 正式开始解析打印
     *
     * @param pekonPrinter
     * @param context
     * @param iPrintDatas
     * @param abstractPrintStatus
     */
    private void doPrint(Context context, IPrinter pekonPrinter, IPrintDataAnalysis iPrintDatas, int printNums, AbstractPrintStatus abstractPrintStatus) {
        new Thread(getPrintRunnable(pekonPrinter, context, iPrintDatas, printNums, abstractPrintStatus)).start();
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
                        Log.e("9527", "已获取产品");
                        List<PrintLineContentEntity> printDatas = iPrintDataAnalysis.getPrintDatas();
                        for (PrintLineContentEntity printData : printDatas) {
                            analysisContent(context, pekonPrinter, printData, DEAFAULT_PRINT_NUM);
                        }
                        backListnerPrintFinshed(abstractPrintStatus);
                    }

                } catch (Exception e) {
                    backListnerPrintFailed(e, abstractPrintStatus);
                }
            }
        };
    }


    private Runnable getPrintRunnable(final IPrinter pekonPrinter, final Context context, final IPrintDataAnalysis iPrintDatas, final int printNums, final AbstractPrintStatus abstractPrintStatus) {
        return new Runnable() {
            @Override
            public void run() {
                try {
                    for (int nums = printNums; nums > 0; nums--) {
                        int i = 0;
                        List<PrintLineContentEntity> printDatas = iPrintDatas.getPrintDatas();
                        System.out.println(printDatas.size());
                        for (PrintLineContentEntity printData : printDatas) {
                            analysisContent(context, pekonPrinter, printData, printNums);
                            System.out.println(i++);
                        }
                    }
                    backListnerPrintFinshed(abstractPrintStatus);
                } catch (Exception e) {
                    backListnerPrintFailed(e, abstractPrintStatus);
                }
            }
        };
    }

    /**
     * 打印失败的回调回主线程
     *
     * @param e
     * @param abstractPrintStatus
     */
    private void backListnerPrintFailed(Exception e, AbstractPrintStatus abstractPrintStatus) {
        this.abstractPrintStatus = abstractPrintStatus;
        Message message = Message.obtain();
        Bundle bundle = new Bundle();
        bundle.putString("code",StatusConstans.Code.FILED);
        bundle.putString("message",e.getMessage());
        message.setData(bundle);
        message.what = PRINT_FAILE;
        mHandler.sendMessage(message);
    }

    /**
     * 打印完成的回调回主线程
     *
     * @param e
     * @param abstractPrintStatus
     */
    private void backListnerPrintFinshed(AbstractPrintStatus abstractPrintStatus) {
        this.abstractPrintStatus = abstractPrintStatus;
        Message message = Message.obtain();
        Bundle bundle = new Bundle();
        bundle.putString("code",StatusConstans.Code.SUCCESS);
        bundle.putString("message","打印完成");
        message.setData(bundle);
        message.what = PRINT_FINSH;
        mHandler.sendMessage(message);
    }


    /**
     * 解析-分发打印行内容
     *
     * @param context
     * @param pekonPrinter           打印机
     * @param printLineContentEntity 打印行对象
     * @param printNums
     */
    private void analysisContent(Context context, IPrinter pekonPrinter, PrintLineContentEntity printLineContentEntity, int printNums) {

        //这里的case 记得排序，常用的在前面，不常用的在后面。能稍微提升速度
        switch (printLineContentEntity.getCommand()) {
            case CommandType.CMMAND_TEXT: {
                if (!StringUtils.isEmpty(printLineContentEntity.getContent())) {
                    pekonPrinter.setPrintHelpData(printLineContentEntity.getHelpEntity());
                    pekonPrinter.printText(printLineContentEntity.getContent());
                }
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
                if (!StringUtils.isEmpty(printLineContentEntity.getContent())) {
                    pekonPrinter.setPrintHelpData(printLineContentEntity.getHelpEntity());
                    pekonPrinter.printQRCode(printLineContentEntity.getContent());
                }
                break;
            }

            case CommandType.CMMAND_BARCODE: {
                if (!StringUtils.isEmpty(printLineContentEntity.getContent())) {
                    pekonPrinter.setPrintHelpData(printLineContentEntity.getHelpEntity());
                    pekonPrinter.printBarCode(printLineContentEntity.getContent());
                }
                break;
            }

            case CommandType.CMMAND_IMAGE: {
                if (printLineContentEntity.getBitmap() != null) {
                    pekonPrinter.printImage(printLineContentEntity.getBitmap());
                }
                break;
            }

            case CommandType.CMMAND_START_PRINT: {
                pekonPrinter.startPrint(context);
                break;
            }

            case CommandType.CMMAND_CASHBOX: {
                if (printNums == 1) {//表示最后一联的打印
                    pekonPrinter.openCashBox();
                }
                break;
            }
            default: {
                break;
            }
        }
    }


    /**
     * 给Wifi打印设置的ip和port
     *
     * @param context
     * @param ipAndPort
     */
    public void saveWifiPrinterIpAndPort(Context context, String ipAndPort) {
        // 记录打印地址
        SharedPrefUtil.setWifiPrintIp(context, ipAndPort);
    }

//    /**
//     * 给Wifi打印设置的另一种模式
//     */
//    public void openWifiUDP() {
//    }

    private void initPrinter(IPrinter pekonPrinter, Context context, AbstractPrintStatus abstractPrintStatus) {
        pekonPrinter.initPrintDriver(context, abstractPrintStatus);
    }


}
