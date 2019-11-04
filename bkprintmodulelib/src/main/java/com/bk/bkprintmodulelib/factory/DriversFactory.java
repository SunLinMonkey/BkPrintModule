package com.bk.bkprintmodulelib.factory;

import android.content.Context;
import android.util.SparseArray;

import com.bk.bkprintmodulelib.anotation.AnotationPrinterType;
import com.bk.bkprintmodulelib.cosntants.PekonPrinterType;
import com.bk.bkprintmodulelib.print_help.IPrinter;
import com.bk.bkprintmodulelib.print_help.SharedPrefUtil;
import com.bk.bkprintmodulelib.printer.bluetooth.BluetoothPrinter;
import com.bk.bkprintmodulelib.printer.parter.PartnerPrinter;
import com.bk.bkprintmodulelib.printer.startwifi.StartWIFIPrinter;
import com.bk.bkprintmodulelib.printer.summi.SummiPrinter;
import com.bk.bkprintmodulelib.printer.usb.USBPrinter;
import com.bk.bkprintmodulelib.printer.usbparallelport.USBParallelPortPrinter;
import com.bk.bkprintmodulelib.printer.usbserialport.USBSerialport;


public class DriversFactory extends AbsDriversFactory {

    /**
     * 是否启用一单多端打印支持
     */
    private boolean openMultiPrinter = false;


    @Override
    public SparseArray<IPrinter> getPekonPrinter(Context context) {
        SparseArray<IPrinter> printers = new SparseArray<>();

        int mainPrinter = SharedPrefUtil.getInstance().getMainPrinter(context);
        getPrinter(printers, mainPrinter);

        if (openMultiPrinter) {
            mainPrinter = SharedPrefUtil.getInstance().getHelpPrinter(context);
            getPrinter(printers, mainPrinter);
        }
        return printers;
    }

    /**
     * 一次获取多打印集合
     *
     * @param printers
     */
    private void getPrinter(SparseArray<IPrinter> printers, @AnotationPrinterType int printerType) {


        switch (printerType) {
            case PekonPrinterType.PRINTER_WIFI: {
                IPrinter iPrinter = new StartWIFIPrinter();
                printers.put(PekonPrinterType.PRINTER_WIFI, iPrinter);
                break;
            }
            case PekonPrinterType.BLUETOOTH: {
                IPrinter iPrinter = new BluetoothPrinter();
                printers.put(PekonPrinterType.BLUETOOTH, iPrinter);
                break;
            }

            case PekonPrinterType.PRINTER_SUMMI: {
                IPrinter iPrinter = new SummiPrinter();
                printers.put(PekonPrinterType.PRINTER_SUMMI, iPrinter);
                break;
            }
            case PekonPrinterType.PRINTER_USB_SERIAL: {
                IPrinter iPrinter = new USBSerialport();
                printers.put(PekonPrinterType.PRINTER_USB_SERIAL, iPrinter);
                break;
            }
            case PekonPrinterType.PRINTER_USB_PARALLEL: {
                IPrinter iPrinter = new USBParallelPortPrinter();
                printers.put(PekonPrinterType.PRINTER_USB_PARALLEL, iPrinter);
                break;
            }
            case PekonPrinterType.PRINTER_LDPRINT: {
                break;
            }
            case PekonPrinterType.PRINTER_BLUETOOTH_BOX: {
                break;
            }
            case PekonPrinterType.PRINTER_PARTNERPRINT: {
                IPrinter iPrinter = new PartnerPrinter();
                printers.put(PekonPrinterType.PRINTER_PARTNERPRINT, iPrinter);
                break;
            }
            case PekonPrinterType.PRINTER_USB: {
                IPrinter iPrinter = new USBPrinter();
                printers.put(PekonPrinterType.PRINTER_USB, iPrinter);
                break;
            }

            case PekonPrinterType.PRINTER_MS_WIFI: {
                break;
            }

        }
    }
}
