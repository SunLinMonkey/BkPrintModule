package com.bk.bkprintmodulelib.print_help;

public interface PrintCallBack {

    void onSucceed();

    void onFailed(String errorCode, String errorMessage);
}
