package com.bk.bkprintmodulelib.printer.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;
import android.os.Handler;

import com.android.print.sdk.PrinterConstants.Connect;
import com.android.print.sdk.PrinterInstance;
import com.android.print.sdk.bluetooth.BluetoothPort;
import com.android.print.sdk.util.Utils;

public class BluetoothOperation implements IPrinterOpertion {
    public static boolean hasRegDisconnectReceiver;
    private BluetoothAdapter adapter;
    private Context mContext;
    private BluetoothDevice mDevice;
    private Handler mHandler;
    private PrinterInstance mPrinter;
    private BluetoothPort bluetoothPort;

    /**
     * bluetooth 打印
     */
    public static final int CONNECT_DEVICE = 1;
    public static final int ENABLE_BT = 2;


    public BroadcastReceiver myReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            if (action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)) {

                if (device != null && mPrinter != null && mPrinter.isConnected() && device.equals(mDevice)) {
                    close();
                }

                mHandler.obtainMessage(Connect.CLOSED).sendToTarget();
            }

        }
    };
    private IntentFilter filter;
    private String mac;

    public BluetoothOperation(Context context, Handler handler) {
        adapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothPort = new BluetoothPort();
        mContext = context;
        mHandler = handler;

        filter = new IntentFilter();

        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        hasRegDisconnectReceiver = true;

    }

    public Context getmContext() {
        return mContext;
    }

    public void open(String macAddress) {
        mac = macAddress;
        mPrinter = new BluetoothPort().btConnnect(mContext, mac, adapter, mHandler);
        Utils.saveBtConnInfo(mContext, mac);
    }

    @Override
    public void btAutoConn(Context context, Handler mHandler) {
        if (bluetoothPort == null) {
            bluetoothPort = new BluetoothPort();
        }
        mPrinter = bluetoothPort.btAutoConn(context, adapter, mHandler);
        if (mPrinter == null) {
            mHandler.obtainMessage(Connect.NODEVICE).sendToTarget();
        }

    }

    public void close() {
        if (mPrinter != null) {
            mPrinter.closeConnection();
            mPrinter = null;
        }
    }

    public PrinterInstance getPrinter() {
        return mPrinter;
    }

    @Override
    public void chooseDevice(BluetoothDeviceListDialog.OnChosenListener onChosenListener) {
        if (!adapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            ((Activity) mContext).startActivityForResult(enableIntent, 2);
        } else {
            BluetoothDeviceListDialog deviceListDialog = new BluetoothDeviceListDialog(mContext);
            deviceListDialog.setOnChosenListener(onChosenListener);
            deviceListDialog.show();
        }
    }

    @Override
    public void usbAutoConn(UsbManager manager) {
    }

    public BroadcastReceiver getMyReceiver() {
        return myReceiver;
    }

}
