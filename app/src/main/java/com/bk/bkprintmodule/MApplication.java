package com.bk.bkprintmodule;

import android.app.Application;

import com.bk.bkprintmodulelib.PrinterManager;

public class MApplication extends Application {

    private static MApplication instance;

    public static MApplication getInstance() {
        if (null == instance) {
            instance = new MApplication();
        }
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        PrinterManager.getInstance().init(this);
    }
}
