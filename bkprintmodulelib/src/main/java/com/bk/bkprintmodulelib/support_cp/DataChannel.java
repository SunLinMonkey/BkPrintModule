package com.example.pekonprinter.suppor_cosumer_produce;


import com.example.pekonprinter.printer_help.IPrintDataAnalysis;

public interface DataChannel {

    void put(IPrintDataAnalysis iPrintDataAnalysis) throws InterruptedException;

    IPrintDataAnalysis get() throws InterruptedException;

}