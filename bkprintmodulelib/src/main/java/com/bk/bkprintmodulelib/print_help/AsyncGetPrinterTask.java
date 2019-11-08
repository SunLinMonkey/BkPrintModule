package com.bk.bkprintmodulelib.print_help;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.starmicronics.stario.PortInfo;
import com.starmicronics.stario.StarIOPort;
import com.starmicronics.stario.StarIOPortException;

import java.util.List;

/**
 * 获取所有的在同一个网段的wifi打印机
 */
public class AsyncGetPrinterTask extends AsyncTask<Void, Void, List<PortInfo>> {

    private List<PortInfo> mPortList;   //获取所有的满足条件的打印机
    private Context mContext;
    private OnResutCallback mOnResutCallback;

    public AsyncGetPrinterTask(Context mContext, OnResutCallback onResutCallback) {
        this.mContext = mContext;
//		DialogHelper.showProgressDialog(mContext,mContext.getResources().getString(R.string.loading));
//		logHelper = new LogHelper(mContext);
        mOnResutCallback = onResutCallback;
    }

    @Override
    protected List<PortInfo> doInBackground(Void... voids) {
        try {
            mPortList = StarIOPort.searchPrinter("TCP:", mContext);
        } catch (StarIOPortException e) {
            e.printStackTrace();
//			logHelper.error(CommonApplication.getInstance().getString(R.string.get_printer_exception) + e.toString());
        }
        if (mPortList == null || mPortList.size() == 0) {
//			logHelper.error(CommonApplication.getInstance().getString(R.string.no_wifi_printer));
        }
        return mPortList;
    }

    @Override
    protected void onPostExecute(List<PortInfo> portInfos) {
        super.onPostExecute(portInfos);
//		DialogHelper.hideProgressDialog();
        if (portInfos == null || portInfos.size() == 0) {
//			ToastHelper.show(CommonApplication.getInstance().getString(R.string.no_printer));
            mOnResutCallback.Failed("未检查到打印机");
            return;
        }
        mOnResutCallback.Success(portInfos);
    }


    public interface OnResutCallback {
        void Success(List<PortInfo> portInfos);

        void Failed(String msg);
    }
}
