package com.example.pekonprinter.constans;

public enum BarcodeType {

    UPC_A(0, "UPC-A"),

    UPC_E(1, "UPC-E"),

    EAN13(2, "JAN13(EAN13)"),

    EAN8(3, "JAN8(EAN8)"),

    CODE39(4, "CODE39"),

    ITF(5, "ITF"),

    CODABAR(6, "CODABAR"),

    CODE93(7, "CODE93"),

    CODE128(8, "CODE128");


    private int code;
    private String type;

    BarcodeType(int code, String type) {
        this.code = code;
        this.type = type;
    }

}
