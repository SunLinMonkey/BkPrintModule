package com.example.pekonprinter.printer.bluetooth;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


import com.example.pekonprinter.R;
import com.example.pekonprinter.printer_help.SharedPrefUtil;

import java.util.Set;

public class BluetoothDeviceListDialog extends Dialog {
    private static final String TAG = "DeviceListActivity";
    public static String EXTRA_DEVICE_ADDRESS = "device_address";
    public static String EXTRA_RE_PAIR = "re_pair";
    public static String EXTRA_DEVICE_NAME = "device_name";

    private Context context;
    // Member fields
    private BluetoothAdapter mBtAdapter;
    private final OnMenuItemClickListener mOnMenuItemClickListener = new OnMenuItemClickListener() {
        public boolean onMenuItemClick(MenuItem item) {
            String info = ((TextView) ((AdapterContextMenuInfo) item.getMenuInfo()).targetView).getText().toString();
            String address = info.substring(info.length() - 17);
            String name = info.substring(0, 5);
            switch (item.getItemId()) {
                case 0:// repair and connect
                    returnToPreviousActivity(address, true, name);
                    break;
                case 1:// connect
                case 2:// pair and connect
                    returnToPreviousActivity(address, false, name);
                    break;
            }
            return false;
        }
    };
    private ArrayAdapter<String> mPairedDevicesArrayAdapter;
    private ListView pairedListView;
    private Button scanButton;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                String itemName = device.getName() + " ( " + context.getResources().getText(device.getBondState() == BluetoothDevice.BOND_BONDED ? R.string.has_paired : R.string.not_paired) + " )" + "\n"
                        + device.getAddress();

                mPairedDevicesArrayAdapter.remove(itemName);
                mPairedDevicesArrayAdapter.add(itemName);
                mPairedDevicesArrayAdapter.notifyDataSetChanged();
                pairedListView.setEnabled(true);
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                setTitle(R.string.select_device);
                if (mPairedDevicesArrayAdapter.getCount() == 0) {
                    String noDevices = context.getResources().getText(R.string.none_found).toString();
                    mPairedDevicesArrayAdapter.add(noDevices);
                    mPairedDevicesArrayAdapter.notifyDataSetChanged();
                    pairedListView.setEnabled(false);
                }
                scanButton.setEnabled(true);
            }
        }
    };
    private OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {

            String info = ((TextView) v).getText().toString();
            System.out.println("message:" + info);
            String address = info.substring(info.length() - 17);
            String name = info.substring(0, info.length() - 17);
            System.out.println("name:" + name);
            SharedPrefUtil.getInstance(context).setCurrentBluetoothDevice(address, context);
            returnToPreviousActivity(address, false, name);
        }
    };
    private OnItemLongClickListener mDeviceLongClickListener = new OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            // if return true, don't call method onCreateContextMenu
            return false;
        }
    };
    private OnCreateContextMenuListener mCreateContextMenuListener = new OnCreateContextMenuListener() {
        @Override
        public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo arg2) {
            menu.setHeaderTitle(R.string.select_options);

            String info = ((TextView) (((AdapterContextMenuInfo) arg2).targetView)).getText().toString();
            System.out.println("message1:" + info);
            // if(((AdapterContextMenuInfo)arg2).position < pairedDeviceNum)
            if (info.contains(" ( " + context.getResources().getText(R.string.has_paired) + " )")) {
                menu.add(0, 0, 0, R.string.rePaire_connect).setOnMenuItemClickListener(mOnMenuItemClickListener);
                menu.add(0, 1, 1, R.string.connect_paired).setOnMenuItemClickListener(mOnMenuItemClickListener);
            } else {
                menu.add(0, 2, 2, R.string.paire_connect).setOnMenuItemClickListener(mOnMenuItemClickListener);
            }
        }
    };

    public BluetoothDeviceListDialog(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.device_list);
        setTitle(R.string.select_device);

        initView();
    }

    private void initView() {
        scanButton = (Button) findViewById(R.id.button_scan);
        scanButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                doDiscovery();
                v.setEnabled(false);
            }
        });

        mPairedDevicesArrayAdapter = new ArrayAdapter<String>(context, R.layout.device_item);

        pairedListView = (ListView) findViewById(R.id.paired_devices);
        pairedListView.setAdapter(mPairedDevicesArrayAdapter);
        pairedListView.setOnItemClickListener(mDeviceClickListener);
        pairedListView.setOnItemLongClickListener(mDeviceLongClickListener);
        pairedListView.setOnCreateContextMenuListener(mCreateContextMenuListener);

        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                mPairedDevicesArrayAdapter.add(device.getName() + " ( " + context.getResources().getText(R.string.has_paired) + " )" + "\n" + device.getAddress());
            }
        }
    }

    @Override
    protected void onStop() {
        if (mBtAdapter != null && mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }
        context.unregisterReceiver(mReceiver);
        super.onStop();
    }


    @Override
    protected void onStart() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        context.registerReceiver(mReceiver, filter);
        super.onStart();
    }

    /**
     * Start device discover with the BluetoothAdapter
     */
    private void doDiscovery() {
        Log.d(TAG, "doDiscovery()");

        // Indicate scanning in the title
        setTitle(R.string.scanning);

        // If we're already discovering, stop it
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }

        mPairedDevicesArrayAdapter.clear();
        // Request discover from BluetoothAdapter
        mBtAdapter.startDiscovery();
    }


    private void returnToPreviousActivity(String address, boolean re_pair, String name) {
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }

        onChosenListener.onChosen(address, re_pair, name);
        dismiss();

    }


    private OnChosenListener onChosenListener;

    public void setOnChosenListener(OnChosenListener onChosenListener) {
        this.onChosenListener = onChosenListener;
    }

    public interface OnChosenListener {

        void onChosen(String address, boolean re_pair, String name);
    }


}
