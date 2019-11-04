package com.bk.bkprintmodulelib.print_help;

public interface InitPrinterCallBack {

    void onSucceed();

    void onFailed(String errorCode, String errorMessage);
}
