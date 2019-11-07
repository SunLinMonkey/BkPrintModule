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
import com.bk.bkprintmodulelib.printer.wifi.WIFIPrinter;


public class DriversFactory extends AbsDriversFactory {

    @Override
    public SparseArray<IPrinter> getPekonPrinter(Context context) {
        boolean openMultiPrinter = false;
        SparseArray<IPrinter> printers = new SparseArray<>();

        getPrinter(printers, SharedPrefUtil.getInstance().getMainPrinter(context));

        if (openMultiPrinter) {
            getPrinter(printers, SharedPrefUtil.getInstance().getHelpPrinter(context));
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
                IPrinter iPrinter = new WIFIPrinter();
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

            case PekonPrinterType.PRINTER_START_WIFI: {
                IPrinter iPrinter = new StartWIFIPrinter();
                printers.put(PekonPrinterType.PRINTER_START_WIFI, iPrinter);
                break;
            }

            case PekonPrinterType.PRINTER_MS_WIFI: {
                break;
            }

            case PekonPrinterType.PRINTER_LDPRINT: {
                break;
            }
            case PekonPrinterType.PRINTER_BLUETOOTH_BOX: {
                break;
            }

        }
    }
}
