package com.example.pekonprinter.printer_help;

public interface PrintCallBack {

    void onSucceed();

    void onFailed(String errorCode, String errorMessage);
}
