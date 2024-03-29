package com.bk.bkprintmodulelib.support_cp;

import android.util.Log;

import com.bk.bkprintmodulelib.print_help.IPrintDataAnalysis;


public class ProductRunnable implements Runnable {

    private IPrintDataAnalysis iPrintDataAnalysis;
    private DataChannel dataChannel;

    public ProductRunnable(IPrintDataAnalysis iPrintDataAnalysis, DataChannel dataChannel) {
        this.iPrintDataAnalysis = iPrintDataAnalysis;
        this.dataChannel = dataChannel;
    }

    @Override
    public void run() {
        try {
            Log.e("9527", "ProductRunnable run: ");
            dataChannel.put(iPrintDataAnalysis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
