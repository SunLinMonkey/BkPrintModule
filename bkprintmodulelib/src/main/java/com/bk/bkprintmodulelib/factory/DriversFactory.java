package com.bk.bkprintmodulelib.factory;

import android.util.SparseArray;

import com.bk.bkprintmodulelib.cosntants.PekonPrinterType;
import com.bk.bkprintmodulelib.print_help.IPrinter;
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


    private String openPrinter = PekonPrinterType.PRINTER_WIFI + "&&" + PekonPrinterType.PRINTER_SUMMI;

    @Override
    public SparseArray<IPrinter> getPekonPrinter() {
        SparseArray<IPrinter> printers = new SparseArray<>();

        if (!openMultiPrinter) {
//            IPrinter iPrinter = new BluetoothPrinter();
            IPrinter iPrinter = new SummiPrinter();
//            IPrinter iPrinter = new USBParallelPortPrinter();
//            IPrinter iPrinter = new WIFIPrinter();
//            IPrinter iPrinter = new StartWIFIPrinter();
//            IPrinter iPrinter = new USBSerialport();
//            IPrinter iPrinter = new PartnerPrinter();
            printers.put(PekonPrinterType.PRINTER_SUMMI,iPrinter);
        } else {
            getMultiPrinter(printers);
        }

        return printers;
    }

    /**
     * 一次获取多打印集合
     * @param printers
     */
    private void getMultiPrinter(SparseArray<IPrinter> printers) {
        String[] split = openPrinter.split("&&");
        for (int i = split.length - 1; i >= 0; i--) {

            switch (Integer.valueOf(split[i])) {
                case PekonPrinterType.PRINTER_WIFI: {
                    IPrinter iPrinter  = new StartWIFIPrinter();
                    printers.put(PekonPrinterType.PRINTER_WIFI, iPrinter);
                    break;
                }
                case PekonPrinterType.BLUETOOTH: {
                    IPrinter iPrinter  = new BluetoothPrinter();
                    printers.put(PekonPrinterType.BLUETOOTH, iPrinter);
                    break;
                }

                case PekonPrinterType.PRINTER_SUMMI: {
                    IPrinter iPrinter  = new SummiPrinter();
                    printers.put(PekonPrinterType.PRINTER_SUMMI, iPrinter);
                    break;
                }
                case PekonPrinterType.PRINTER_USB_SERIAL: {
                    IPrinter iPrinter  = new USBSerialport();
                    printers.put(PekonPrinterType.PRINTER_USB_SERIAL, iPrinter);
                    break;
                }
                case PekonPrinterType.PRINTER_USB_PARALLEL: {
                    IPrinter iPrinter  = new USBParallelPortPrinter();
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
                    IPrinter iPrinter  = new PartnerPrinter();
                    printers.put(PekonPrinterType.PRINTER_PARTNERPRINT, iPrinter);
                    break;
                }
                case PekonPrinterType.PRINTER_USB: {
                    IPrinter iPrinter  = new USBPrinter();
                    printers.put(PekonPrinterType.PRINTER_USB, iPrinter);
                    break;
                }

                case PekonPrinterType.PRINTER_MS_WIFI: {
                    break;
                }
            }

        }
    }
}
