package com.bk.bkprintmodulelib.printer.startwifi;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.Toast;


import com.bk.bkprintmodulelib.cosntants.EncodingFormat;
import com.bk.bkprintmodulelib.cosntants.TextGravity;
import com.bk.bkprintmodulelib.cosntants.TextSize;
import com.bk.bkprintmodulelib.factory.HelpEntityFactory;
import com.bk.bkprintmodulelib.print_help.AbstractPrintStatus;
import com.bk.bkprintmodulelib.print_help.AsyncGetPrinterTask;
import com.bk.bkprintmodulelib.print_help.HelpEntity;
import com.bk.bkprintmodulelib.print_help.IPrinter;
import com.bk.bkprintmodulelib.printer.BasePrinter;
import com.starmicronics.stario.PortInfo;
import com.starmicronics.starioextension.ICommandBuilder;
import com.starmicronics.starioextension.StarIoExt;

import java.nio.charset.Charset;
import java.util.List;

public class StartWIFIPrinter extends BasePrinter implements IPrinter {

    private Charset encoding = Charset.forName(EncodingFormat.FORMAT_GB2312);  //中文字体不乱码
    private PrinterSettings settings;
    private ICommandBuilder builder;

    @Override
    public void initPrintDriver(Context context, AbstractPrintStatus listener) {
        StarIoExt.Emulation emulation = ModelCapability.getEmulation(settings.getModelIndex());
        builder = StarIoExt.createCommandBuilder(emulation);
        listener.onPrinterInitSucceed();
        builder.beginDocument();
    }

    @Override
    public void initPrintConnection(Context context, AbstractPrintStatus listener) {
        PrinterSettingManager settingManager = new PrinterSettingManager(context);
        settings = settingManager.getPrinterSettings();
        if (settings == null) {
//            ToastHelper.show(R.string.please_connect_printer);
            listener.onConnectFailed("", "请连接打印机");
            //todo 提示
            return;
        }
        listener.onConnectSucceed();
    }

    @Override
    public void resetPrintConnection(final Context context, AbstractPrintStatus listener) {
        new AsyncGetPrinterTask(context, new AsyncGetPrinterTask.OnResutCallback() {
            @Override
            public void Success(List<PortInfo> portInfos) {
                if (portInfos == null) {
                    return;
                }
                if (portInfos.size() == 1) {
                    PortInfo portInfo = portInfos.get(0);
                    String macAddress = portInfo.getMacAddress();
                    String modelName = portInfo.getModelName();
                    String portName = portInfo.getPortName();
//                    tvSelectPrint.setText(modelName + "\n" + macAddress);
                    registerPrinter(context, portName, macAddress, modelName);
                } else {
//                    if (portinfoDialog != null){
//                        portinfoDialog.dismiss();
//                        portinfoDialog = null;
//                    }
//                    portinfoDialog = new PortinfoDialog(PrintTestActivity.this, portInfos, new PortinfoDialog.OnResultBack() {
//                        @Override
//                        public void onSuccess(PortInfo portInfo) {
//                            tvSelectPrint.setText(portInfo.getModelName() + "\n" + portInfo.getMacAddress());
//                            registerPrinter(portInfo.getPortName(),portInfo.getMacAddress(),portInfo.getModelName());
//                        }
//                    });
//                    portinfoDialog.show();

                }
            }

            @Override
            public void Failed(String msg) {
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
            }
        }).execute();
    }

    /**
     * Register printer information to SharedPreference.
     */
    private void registerPrinter(Context context, String portName, String macAddress, String modelName) {
        PrinterSettingManager settingManager = new PrinterSettingManager(context);
//        int paperSize =  SharedPrefUtil.getPrintPaperSelection(this); // Constants.PAPER_SELECTION_80
        int paperSize = 1;
        settingManager.storePrinterSettings(
                0,  //modelIndex为3的时候默认是网络打印
                new PrinterSettings(3, portName, "", macAddress, modelName, true, paperSize)
        );
    }


    @Override
    public void printText(String content) {
        getLocalTextSize();
        getLocalGravity();
        builder.append(content.getBytes(encoding));
    }


    @Override
    public void printBarCode(String content) {
        builder.appendBarcode(content.getBytes(), ICommandBuilder.BarcodeSymbology.Code39, ICommandBuilder.BarcodeWidth.Mode4, 8, true);
    }

    @Override
    public void printQRCode(String content) {
        builder.appendQrCode(content.getBytes(), ICommandBuilder.QrCodeModel.No1, ICommandBuilder.QrCodeLevel.M, 8);
    }


    @Override
    public void printImage(Bitmap bitmap) {
        builder.appendBitmap(bitmap, true);
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
        for (int num = linesNum; num > 0; num--) {
            printBlankLine();
        }
    }

    @Override
    public void printBlankLine() {
        builder.append("\n".getBytes(encoding));
    }

    @Override
    public void cutPaper() {
        builder.appendCutPaper(ICommandBuilder.CutPaperAction.PartialCutWithFeed);
    }

    @Override
    public void startPrint(Context context) {
        if (builder != null) {
            builder.endDocument();
            byte[] commands = builder.getCommands();
            Communication.sendCommands(this, commands, settings.getPortName(), settings.getPortSettings(), 10000, context, mCallback);     // 10000mS!!!
        }

    }

    @Override
    public void openCashBox() {

    }

    @Override
    public boolean isConnected() {
        return false;
    }

    @Override
    public boolean isPrinterReady() {
        return false;
    }

    /**
     * 将通用布局位置转成本打印机实体类使用的布局位置
     *
     * @param gravity
     */
    private void getLocalGravity(int gravity) {
        switch (gravity) {
            case TextGravity.GRAVITY_LEFT: {
                builder.appendAlignment(ICommandBuilder.AlignmentPosition.Left);
                break;
            }
            case TextGravity.GRAVITY_RIGHT: {
                builder.appendAlignment(ICommandBuilder.AlignmentPosition.Right);
                break;
            }
            case TextGravity.GRAVITY_CENTER: {
                builder.appendAlignment(ICommandBuilder.AlignmentPosition.Center);
                break;
            }
            default: {
                builder.appendAlignment(ICommandBuilder.AlignmentPosition.Center);
                break;
            }

        }
    }


    /**
     * 将通用字体大小转成本打印机实体类使用的字体大小
     *
     * @param textSize
     * @return
     */
    private void getLocalTextSize(int textSize) {
        switch (textSize) {
            case TextSize.TEXT_SIZE_DOWN_2: {
                break;
            }

            case TextSize.TEXT_SIZE_DOWN_1: {
                break;
            }

            case TextSize.TEXT_SIZE_DEFAULT: {
                builder.appendEmphasis(false);  //是否强调字体
                builder.appendMultipleHeight(1);   //设置字体的高度
                builder.appendCharacterSpace(1);   //设置字体的间距
                break;
            }

            case TextSize.TEXT_SIZE_UP_3: {
                builder.appendEmphasis(true);  //是否强调字体
                builder.appendMultipleHeight(2);   //设置字体的高度
                builder.appendCharacterSpace(5);   //设置字体的间距
                break;
            }

            case TextSize.TEXT_SIZE_UP_4: {
                builder.appendEmphasis(true);  //是否强调字体
                builder.appendMultipleHeight(2);   //设置字体的高度
                builder.appendCharacterSpace(5);   //设置字体的间距
                break;
            }

            default: {
                break;
            }
        }
    }


    private final Communication.SendCallback mCallback = new Communication.SendCallback() {
        @Override
        public void onStatus(boolean result, Communication.Result communicateResult) {
            String msg;

            switch (communicateResult) {
                case Success:
                    msg = "Success!";
                    break;
                case ErrorOpenPort:
                    msg = "Fail to openPort";
                    break;
                case ErrorBeginCheckedBlock:
                    msg = "Printer is offline (beginCheckedBlock)";
                    break;
                case ErrorEndCheckedBlock:
                    msg = "Printer is offline (endCheckedBlock)";
                    break;
                case ErrorReadPort:
                    msg = "Read port error (readPort)";
                    break;
                case ErrorWritePort:
                    msg = "Write port error (writePort)";
                    break;
                default:
                    msg = "Unknown error";
                    break;
            }
        }
    };

    @Override
    protected void getLocalTextSize() {
        getLocalTextSize(getHelpEntity().getTestSize());
    }

    @Override
    protected void getLocalGravity() {
        getLocalGravity(getHelpEntity().getGrivaty());
    }
}
