package com.bk.bkprintmodulelib.support_cp;


import com.bk.bkprintmodulelib.print_help.IPrintDataAnalysis;

public interface DataChannel {

    void put(IPrintDataAnalysis iPrintDataAnalysis) throws InterruptedException;

    IPrintDataAnalysis get() throws InterruptedException;

}