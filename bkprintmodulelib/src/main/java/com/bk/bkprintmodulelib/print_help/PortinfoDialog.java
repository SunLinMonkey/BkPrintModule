package com.bk.bkprintmodulelib.print_help;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.bk.bkprintmodulelib.R;
import com.starmicronics.stario.PortInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 网络打印机的弹窗
 * 如果有多个话自动弹出
 */
public class PortinfoDialog extends Dialog {

    private List<PortInfo> mData; //数据源
    private Context mContext;

    private OnResultBack mOnResultBack;

    public PortinfoDialog(@NonNull Context context, List<PortInfo> data, OnResultBack mOnResultBack) {
        super(context);
        this.mContext = context;
        this.mData = data;
        this.mOnResultBack = mOnResultBack;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.dialog_portinfo, null);
        setContentView(view);
        ListView lv_wifis = (ListView) view.findViewById(R.id.lv_wifi);

        ArrayList<String> data = initData();

        ArrayAdapter arrayAdapter = new ArrayAdapter<>(mContext, R.layout.recyclerview_string, R.id.tv_name, data);
        //listview视图加载适配器
        lv_wifis.setAdapter(arrayAdapter);
        //为列表视图中选中的项添加响应事件
        lv_wifis.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PortInfo portInfo = mData.get(position);
                dismiss();
                if (mOnResultBack != null) {
                    mOnResultBack.onSuccess(portInfo);
                }
            }
        });

        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = mContext.getResources().getDisplayMetrics(); // 获取屏幕宽、高用
        lp.width = (int) (d.widthPixels * 0.6); // 高度设置为屏幕的0.8
        lp.height = (int) (d.heightPixels * 0.6); // 高度设置为屏幕的0.8
        dialogWindow.setAttributes(lp);
    }

    private ArrayList<String> initData() {
        ArrayList data = new ArrayList<String>();
        for (PortInfo mDatum : mData) {
            data.add(mDatum.getModelName() + "       " + mDatum.getMacAddress() + "       " + mDatum.getPortName());
        }
        return data;
    }

    public interface OnResultBack {
        void onSuccess(PortInfo portInfo);
    }
}
