package com.example.pekonprinter.printer_help;

public interface InitPrinterCallBack {

    void onSucceed();

    void onFailed(String errorCode, String errorMessage);
}
