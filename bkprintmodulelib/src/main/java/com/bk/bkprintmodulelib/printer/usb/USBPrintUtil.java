package com.bk.bkprintmodulelib.printer.usb;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;


import com.bk.bkprintmodulelib.print_help.PrintCallBack;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;

public class USBPrintUtil {
    UsbDeviceConnection connection;

    private UsbManager mUsbManager;
    private UsbDevice mDevice;
    private PendingIntent mPermissionIntent;

    public USBPrintUtil(Context context) {

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void createConn(Context context, PrintCallBack listener) {
        getPrintUSBConnection(context);
        mPermissionIntent = PendingIntent.getBroadcast(context, 0, new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED), 0);
        if (mDevice == null){
//            Toast.makeText().show(R.string.no_find_usb_print);

            //todo 吐司提醒
            listener.onFailed("","USB打印连接失败");
            return;
        }
        mUsbManager.requestPermission(mDevice, mPermissionIntent);
        listener.onSucceed();
    }

    private void getPrintUSBConnection(Context context) {
        mUsbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        while (deviceIterator.hasNext()) {
            UsbDevice device = deviceIterator.next();
            int count = device.getConfigurationCount();
            for (int i = 0;i<count;i++) {
                if (device.getInterface(i).getInterfaceClass() == UsbConstants.USB_CLASS_PRINTER){
                    mDevice = device;
                }
            }
        }
    }

    @SuppressLint("NewApi")
    public void printMessage(Context context, String msg) {
        final String printdata = msg;
        final UsbEndpoint mEndpointBulkOut;
        if (mDevice == null) return;
        if (mUsbManager.hasPermission(mDevice)) {
            UsbInterface intf = mDevice.getInterface(0);
            for (int i = 0; i < intf.getEndpointCount(); i++) {
                UsbEndpoint ep = intf.getEndpoint(i);
                if (ep.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK) {
                    if (ep.getDirection() == UsbConstants.USB_DIR_OUT) {
                        mEndpointBulkOut = ep;
                        connection = mUsbManager.openDevice(mDevice);
                        if (connection != null) {
                            Log.e("Connection:", " connected");
                        }
                        boolean forceClaim = true;
                        connection.claimInterface(intf, forceClaim);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                byte[] bytes = new byte[0];
                                try {
                                    bytes = printdata.getBytes("gbk");
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                                int b = connection.bulkTransfer(mEndpointBulkOut, bytes, bytes.length, 100000);
                                Log.i("Return Status", "b-->" + b);
                            }
                        }).start();
                        connection.releaseInterface(intf);
                        break;
                    }
                }
            }
        } else {
            mUsbManager.requestPermission(mDevice, mPermissionIntent);
        }
    }

    @SuppressLint("NewApi")
    public void closeConnection(Context context) {
        BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                    UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (device != null) {
                        Toast.makeText(context, "Device closed", Toast.LENGTH_SHORT).show();
                        connection.close();
                    }
                }
            }
        };
    }
}
