package com.bk.bkprintmodulelib.print_help;

public interface ConnectionCallBack {

    void onSucceed();

    void onFailed(String errorCode, String errorMessage);
}
