package com.example.pekonprinter.printer_help;

public interface ConnectionCallBack {

    void onSucceed();

    void onFailed(String errorCode, String errorMessage);
}
