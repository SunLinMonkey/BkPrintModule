package com.example.pekonprinter.printer.bluetooth;

import android.content.Context;
import android.hardware.usb.UsbManager;
import android.os.Handler;

import com.android.print.sdk.PrinterInstance;

public interface IPrinterOpertion {
	public void open(String macAddress);

	public void close();

	public void chooseDevice(BluetoothDeviceListDialog.OnChosenListener onChoosedListener);

	public PrinterInstance getPrinter();

	public void usbAutoConn(UsbManager manager);

	public void btAutoConn(Context context, Handler mHandler);

	public Context getmContext();
}
