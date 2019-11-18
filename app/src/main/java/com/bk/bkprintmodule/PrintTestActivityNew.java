package com.bk.bkprintmodule;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.bk.bkprintmodulelib.PrinterManager;
import com.bk.bkprintmodulelib.cosntants.PekonPrinterType;
import com.bk.bkprintmodulelib.print_help.AbstractPrintStatus;
import com.bk.bkprintmodulelib.printer.startwifi.PrinterSettingManager;
import com.bk.bkprintmodulelib.printer.startwifi.PrinterSettings;

import android_serialport_api.SerialPort;

/**
 * 打印测试界面新界面
 *
 * @author Zlq 2014-3-3下午3:35:39
 */
public class PrintTestActivityNew extends Activity implements View.OnClickListener {
    private static final String TAG = "PrintTestActivity";

    private Context mContext;
    private EditText etIp1, etIp2, etIp3, etIp4, etPort, et_printType, et_etPaperSelection;
    private TextView tvPaperSelection;
    private String ipAddress = "";
    private TableLayout ll_wifiPrint;
    private int type, printPaperSelection;
    private SerialPort mSerialPort = null;


    private Button btn_reset, btn_save, btn_print;

    private TextView tvSelectPrint;
    private LinearLayout llSelectPrint;
    /**
     * 软键盘监听
     */
    private OnEditorActionListener mActionListener = new OnEditorActionListener() {

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                switch (v.getId()) {
                    case R.id.etIp1:
                        etIp2.requestFocus();
                        etIp2.requestFocusFromTouch();
                        break;
                    case R.id.etIp2:
                        etIp3.requestFocus();
                        etIp3.requestFocusFromTouch();
                        break;
                    case R.id.etIp3:
                        etIp4.requestFocus();
                        etIp4.requestFocusFromTouch();
                        break;
                    case R.id.etIp4:
                        etPort.requestFocus();
                        etPort.requestFocusFromTouch();
                        break;
                    default:
                        break;
                }

            }
            return false;
        }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_print_test_new);
        mContext = this;
        ll_wifiPrint = (TableLayout) findViewById(R.id.ll_wifiPrint);
        findView();


//        PrinterManager.getInstance().prepareLoop(this, new AbstractPrintStatus() {
//            @Override
//            public void onPrinterFinished(String finshCode, String msg) {
//
//            }
//
//            @Override
//            public void onPrintFailed(String errorCode, String errorMessage) {
//
//            }
//        });
    }


    private void findView() {

        et_printType = (EditText) findViewById(R.id.et_printType);
        et_printType.setOnClickListener(this);
        et_etPaperSelection = (EditText) findViewById(R.id.etPaperSelection);
        et_etPaperSelection.setOnClickListener(this);
        tvPaperSelection = (TextView) findViewById(R.id.tvPaperSelection);


        btn_reset = (Button) findViewById(R.id.btn_reset);
        btn_save = (Button) findViewById(R.id.btn_save);
        btn_print = (Button) findViewById(R.id.btn_print);
        btn_reset.setOnClickListener(this);
        btn_save.setOnClickListener(this);
        btn_print.setOnClickListener(this);


        tvSelectPrint = (TextView) findViewById(R.id.tv_select_print);


        PrinterSettingManager settingManager = new PrinterSettingManager(mContext);
        PrinterSettings settings = settingManager.getPrinterSettings();
        if (settings != null) {
            String name = settings.getModelName();
            String address = settings.getMacAddress();
            tvSelectPrint.setText(String.format("%s\n%s", name, address));
        }
        tvSelectPrint.setOnClickListener(this);
        llSelectPrint = (LinearLayout) findViewById(R.id.ll_select_print);
        refreshView();
    }


    public void refreshView() {
        type = SPUtils.getPrintType(this);
        printPaperSelection = SPUtils.getPrintPaperSelection(this);
//        isLdposPrint = false;
        switch (type) {
            case Constants.WIFI: {
                et_printType.setText("无线");
                btn_reset.setVisibility(View.GONE);
                btn_print.setVisibility(View.VISIBLE);
                btn_save.setVisibility(View.VISIBLE);
                ll_wifiPrint.setVisibility(View.VISIBLE);
                llSelectPrint.setVisibility(View.GONE);
                initWifiIpPort();
                PrinterManager.getInstance().saveMainPrinter(this, PekonPrinterType.PRINTER_WIFI);
                break;
            }

            case Constants.BLUETOOTH: {
                et_printType.setText("蓝牙");
                btn_reset.setVisibility(View.VISIBLE);
                btn_print.setVisibility(View.VISIBLE);
                btn_save.setVisibility(View.GONE);
                ll_wifiPrint.setVisibility(View.GONE);
                llSelectPrint.setVisibility(View.GONE);
                PrinterManager.getInstance().saveMainPrinter(this, PekonPrinterType.BLUETOOTH);
                break;
            }

            case Constants.USBPRINT: {
                et_printType.setText("usb串口");
                btn_reset.setVisibility(View.GONE);
                btn_print.setVisibility(View.VISIBLE);
                btn_save.setVisibility(View.GONE);
                ll_wifiPrint.setVisibility(View.GONE);
                llSelectPrint.setVisibility(View.GONE);
                PrinterManager.getInstance().saveMainPrinter(this, PekonPrinterType.PRINTER_USB_SERIAL);
                break;
            }

            case Constants.USBPARALLELPRINT: {
                et_printType.setText("usb并口");
                btn_reset.setVisibility(View.GONE);
                btn_print.setVisibility(View.VISIBLE);
                btn_save.setVisibility(View.GONE);
                ll_wifiPrint.setVisibility(View.GONE);
                llSelectPrint.setVisibility(View.GONE);
                PrinterManager.getInstance().saveMainPrinter(this, PekonPrinterType.PRINTER_USB_PARALLEL);
                break;
            }


            case Constants.MSWIFIPRINT: {
                et_printType.setText("wifi激光");
                btn_reset.setVisibility(View.GONE);
                btn_print.setVisibility(View.VISIBLE);
                btn_save.setVisibility(View.GONE);
                ll_wifiPrint.setVisibility(View.GONE);
                llSelectPrint.setVisibility(View.GONE);
                PrinterManager.getInstance().saveMainPrinter(this, PekonPrinterType.PRINTER_MS_WIFI);
                break;
            }


            case Constants.LDPRINT: {
//                isLdposPrint = true;
//                if (!isLdDeviceServiceLogined) {
//                    bindDeviceService();
//                }
//
                et_printType.setText("联迪");
                btn_reset.setVisibility(View.GONE);
                btn_print.setVisibility(View.VISIBLE);
                btn_save.setVisibility(View.GONE);
                ll_wifiPrint.setVisibility(View.GONE);
                llSelectPrint.setVisibility(View.GONE);
                PrinterManager.getInstance().saveMainPrinter(this, PekonPrinterType.PRINTER_LDPRINT);
                break;
            }

            case Constants.BLUETOOTHBOX: {
                et_printType.setText("蓝牙盒子");
                btn_reset.setVisibility(View.GONE);
                btn_print.setVisibility(View.VISIBLE);
                btn_save.setVisibility(View.GONE);
                ll_wifiPrint.setVisibility(View.GONE);
                llSelectPrint.setVisibility(View.GONE);
                PrinterManager.getInstance().saveMainPrinter(this, PekonPrinterType.PRINTER_BLUETOOTH_BOX);
                break;
            }

            case Constants.PARTNERPRINT: {
                et_printType.setText("拍档");
                btn_reset.setVisibility(View.GONE);
                btn_print.setVisibility(View.VISIBLE);
                btn_save.setVisibility(View.GONE);
                ll_wifiPrint.setVisibility(View.GONE);
                llSelectPrint.setVisibility(View.GONE);
                PrinterManager.getInstance().saveMainPrinter(this, PekonPrinterType.PRINTER_PARTNERPRINT);
                break;
            }

            case Constants.USB_TICKET_PRINT: {
                et_printType.setText("usb打印");
                btn_reset.setVisibility(View.GONE);
                btn_print.setVisibility(View.VISIBLE);
                btn_save.setVisibility(View.GONE);
                ll_wifiPrint.setVisibility(View.GONE);
                llSelectPrint.setVisibility(View.GONE);
                PrinterManager.getInstance().saveMainPrinter(this, PekonPrinterType.PRINTER_USB);
                break;
            }

            case Constants.PRINTER_SUMMI: {
                et_printType.setText("商米打印");
                btn_reset.setVisibility(View.GONE);
                btn_print.setVisibility(View.VISIBLE);
                btn_save.setVisibility(View.GONE);
                ll_wifiPrint.setVisibility(View.GONE);
                llSelectPrint.setVisibility(View.GONE);
                PrinterManager.getInstance().saveMainPrinter(this, PekonPrinterType.PRINTER_SUMMI);
                break;
            }

            case Constants.PRINTER_START_WIFI: {
                et_printType.setText("新wifi");
                btn_reset.setVisibility(View.GONE);
                btn_print.setVisibility(View.VISIBLE);
                btn_save.setVisibility(View.GONE);
                ll_wifiPrint.setVisibility(View.GONE);
                llSelectPrint.setVisibility(View.VISIBLE);
                PrinterManager.getInstance().saveMainPrinter(this, PekonPrinterType.PRINTER_START_WIFI);
                break;
            }

            default: {
                break;
            }
        }

        if (printPaperSelection == 2) {
            et_etPaperSelection.setText("58mm");
        } else {
            et_etPaperSelection.setText("80mm");
        }

        PrinterManager.getInstance().reGetPrinter(MApplication.getInstance());
    }

    private void initWifiIpPort() {
        etIp1 = (EditText) findViewById(R.id.etIp1);
        etIp2 = (EditText) findViewById(R.id.etIp2);
        etIp3 = (EditText) findViewById(R.id.etIp3);
        etIp4 = (EditText) findViewById(R.id.etIp4);
        etPort = (EditText) findViewById(R.id.etPort);
        etIp1.setOnEditorActionListener(mActionListener);
        etIp2.setOnEditorActionListener(mActionListener);
        etIp3.setOnEditorActionListener(mActionListener);
        etIp4.setOnEditorActionListener(mActionListener);
        etPort.setOnEditorActionListener(mActionListener);

        String strWifiPrintIp = SPUtils.getWifiPrintIp(this);
        if (strWifiPrintIp != null && !"".equals(strWifiPrintIp)) {
            String[] address = strWifiPrintIp.split(":");
            ipAddress = address[0];
            String[] ips = ipAddress.split("\\.");
            String tmpPort = address[1];
            etIp1.setText(ips[0]);
            etIp2.setText(ips[1]);
            etIp3.setText(ips[2]);
            etIp4.setText(ips[3]);
            etPort.setText(tmpPort);
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.et_printType: {
                et_printType.setClickable(false);
                showPrintTypeDialog();
                et_printType.setClickable(true);
                break;
            }
            case R.id.btn_reset: {
                printReset();
                break;
            }
            case R.id.btn_print: {
                printTest();
                break;
            }
            case R.id.btn_save: {
                wifiSave();
                break;
            }

            case R.id.etPaperSelection: {
                et_etPaperSelection.setClickable(false);
                showPrintPaperSelectionDialog();
                et_etPaperSelection.setClickable(true);
                break;
            }

            case R.id.tv_select_print: {
                printReset();
                break;
            }
        }
    }

    private void printTest() {
//        PrinterManager.getInstance().addPrintContent(new TestPrintDataAnalysis(this, "print test content"));
        PrinterManager.getInstance().printContent(this, new TestPrintDataAnalysis(this, "print test content"), 2,new AbstractPrintStatus() {

            @Override
            public void onPrinterFinished(String finshCode, String msg) {

            }

            @Override
            public void onPrintFailed(String errorCode, String errorMessage) {

            }

            @Override
            public void onConnectFailed(String errorCode, String errorMessage) {
                super.onConnectFailed(errorCode, errorMessage);
                showToast(errorMessage);
            }

            @Override
            public void onPrinterInitFailed(String errorCode, String errorMessage) {
                super.onPrinterInitFailed(errorCode, errorMessage);
                showToast(errorMessage);
            }

        });
    }

    @Override
    protected void onDestroy() {
        PrinterManager.getInstance().loopEnd();
        super.onDestroy();
    }

    private void printReset() {
        PrinterManager.getInstance().getPrint().resetPrintConnection(this, new AbstractPrintStatus() {
            @Override
            public void onPrinterFinished(String errorCode, String errorMessage) {

            }

            @Override
            public void onConnectFailed(String errorCode, String errorMessage) {
                super.onConnectFailed(errorCode, errorMessage);
                showToast(errorMessage);
            }

            @Override
            public void onPrinterInitFailed(String errorCode, String errorMessage) {
                super.onPrinterInitFailed(errorCode, errorMessage);
                showToast(errorMessage);
            }

            @Override
            public void onConnectSucceed(String code, String msg) {
                super.onConnectSucceed(code, msg);
                int mainPrinterType = PrinterManager.getInstance().getMainPrinterType();
                if (mainPrinterType == PekonPrinterType.PRINTER_START_WIFI) {
                    tvSelectPrint.setText(msg);
                }
            }


            @Override
            public void onPrintFailed(String errorCode, String errorMessage) {

            }
        });
    }


    /**
     * 保存wifi的IP地址和端口号
     */
    private void wifiSave() {
        btn_save.setClickable(false);
        String strIP1 = etIp1.getText().toString();
        String strIP2 = etIp2.getText().toString();
        String strIP3 = etIp3.getText().toString();
        String strIP4 = etIp4.getText().toString();

        try {
            int ip1 = Integer.parseInt(strIP1);
            int ip2 = Integer.parseInt(strIP2);
            int ip3 = Integer.parseInt(strIP3);
            int ip4 = Integer.parseInt(strIP4);
            if (ip1 > 255 || ip1 < 0) {
                etIp1.setText("255");
                showToast("ip错误");
                return;
            }
            if (ip2 > 255 || ip2 < 0) {
                etIp2.setText("255");
                showToast("ip错误");
                return;
            }
            if (ip3 > 255 || ip3 < 0) {
                etIp3.setText("255");
                showToast("ip错误");
                return;
            }
            if (ip4 > 255 || ip4 < 0) {
                etIp4.setText("255");
                showToast("ip错误");
                return;
            }
        } catch (NumberFormatException e) {
            showToast("ip错误");
            return;
        }
        // 端口号
        String strTmpPort = etPort.getText().toString();
        try {
            int port = Integer.parseInt(strTmpPort);
            if (port > 65535 || port < 0) {
                etPort.setText("65535");
                showToast("ip错误");
                return;
            }
        } catch (NumberFormatException e) {
            showToast("ip错误");
            return;
        }
        // 打印地址
        String strIPAddress = strIP1 + "." + strIP2 + "." + strIP3 + "." + strIP4 + ":" + strTmpPort;
        // 记录打印地址
        SPUtils.setWifiPrintIp(this, strIPAddress);

        showToast("保存成功");
        PrinterManager.getInstance().saveWifiPrinterIpAndPort(this, strIPAddress);
        btn_save.setClickable(true);
    }






    /**
     * 显示更新选项dialog
     */
    private void showPrintTypeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        int choiceItem = SPUtils.getPrintType(this) - 1;
        builder.setSingleChoiceItems(getResources().getStringArray(R.array.print_type), choiceItem,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                SPUtils.setPrintType(PrintTestActivityNew.this, Constants.WIFI);
                                dialog.dismiss();
                                refreshView();
                                break;
                            case 1:
                                SPUtils.setPrintType(PrintTestActivityNew.this, Constants.BLUETOOTH);
                                dialog.dismiss();
                                refreshView();

                                break;
                            case 2:
                                SPUtils.setPrintType(PrintTestActivityNew.this, Constants.USBPRINT);
                                dialog.dismiss();
                                refreshView();
                                break;
                            case 3:
                                SPUtils.setPrintType(PrintTestActivityNew.this, Constants.USBPARALLELPRINT);
                                dialog.dismiss();
                                refreshView();
                                break;
                            case 4:
                                SPUtils.setPrintType(PrintTestActivityNew.this, Constants.MSWIFIPRINT);
                                dialog.dismiss();
                                refreshView();
                                break;
                            case 5:
                                SPUtils.setPrintType(PrintTestActivityNew.this, Constants.LDPRINT);
                                dialog.dismiss();
                                refreshView();
                                break;
                            case 6:
                                SPUtils.setPrintType(PrintTestActivityNew.this, Constants.BLUETOOTHBOX);
                                dialog.dismiss();
                                refreshView();

                                break;
                            case 7:
                                SPUtils.setPrintType(PrintTestActivityNew.this, Constants.PARTNERPRINT);
                                dialog.dismiss();
                                refreshView();
                                break;
                            case 8:
                                SPUtils.setPrintType(PrintTestActivityNew.this, Constants.USB_TICKET_PRINT);
                                dialog.dismiss();
                                refreshView();
                                break;

                            case 9:
                                SPUtils.setPrintType(PrintTestActivityNew.this, Constants.PRINTER_SUMMI);
                                dialog.dismiss();
                                refreshView();
                                break;

                            case 10:
                                SPUtils.setPrintType(PrintTestActivityNew.this, Constants.PRINTER_START_WIFI);
                                dialog.dismiss();
                                refreshView();
                                break;

                            default:
                                break;
                        }
                    }
                }).show();
    }


    /**
     * 显示更新选项dialog
     */
    private void showPrintPaperSelectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        int choiceItem = SPUtils.getPrintPaperSelection(this);
        builder.setSingleChoiceItems(getResources().getStringArray(R.array.print_paper_selection), choiceItem,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        SPUtils.getInstance(PrintTestActivityNew.this).putString(Constants.PAPER_WIDTH_TYPE, String.valueOf(which));

                        switch (which) {
                            case 0:
                                SPUtils.setPrintPaperSelection(PrintTestActivityNew.this, Constants.PAPER_SELECTION_58);
                                dialog.dismiss();
                                refreshView();
                                break;
                            case 1:
                                SPUtils.setPrintPaperSelection(PrintTestActivityNew.this, Constants.PAPER_SELECTION_80);
                                dialog.dismiss();
                                refreshView();
                                break;
                            default:
                                break;
                        }
                    }
                }).show();
    }


    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


    // 打印小票指令
    public static class PrintCmd {
        /**
         * 初始化
         */
        public static String initialPrint() {
            byte[] result = new byte[2];
            result[0] = 27;
            result[1] = 64;
            return new String(result);
        }

        /**
         * 切纸
         */
        public static String Cut() {
            byte[] result = new byte[2];
            result[0] = 27;
            result[1] = 105;
            return new String(result);
        }
    }


}
