package com.example.pekonprinter.annotation;


import android.support.annotation.IntDef;

import com.example.pekonprinter.constans.PekonPrinterType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;



//public static final int PRINTER_WIFI = 1;// wifi打印
//public static final int BLUETOOTH = 2;// 蓝牙打印
//public static final int PRINTER_USB_SERIAL = 3;// USB串口打印
//public static final int PRINTER_USB_PARALLEL = 4;// USB串口打印
//public static final int PRINTER_MS_WIFI = 5;// 美素wifi打印
//public static final int PRINTER_LDPRINT = 6;// 联迪打印打印
//public static final int PRINTER_BLUETOOTH_BOX = 7;// 蓝牙盒子打印
//public static final int PRINTER_PARTNERPRINT = 8;// 拍档usb打印
//public static final int PRINTER_USB = 9;// USB打印
//
//public static final int PRINTER_SUMMI = 10;// 商米打印

@IntDef({PekonPrinterType.PRINTER_WIFI, PekonPrinterType.BLUETOOTH, PekonPrinterType.PRINTER_USB_SERIAL
        , PekonPrinterType.PRINTER_USB_PARALLEL, PekonPrinterType.PRINTER_MS_WIFI
        , PekonPrinterType.PRINTER_LDPRINT, PekonPrinterType.PRINTER_BLUETOOTH_BOX
        , PekonPrinterType.PRINTER_PARTNERPRINT, PekonPrinterType.PRINTER_USB, PekonPrinterType.PRINTER_SUMMI})
@Retention(RetentionPolicy.SOURCE)
public @interface AnotationPrinterType {
}
