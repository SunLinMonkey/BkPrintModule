package com.example.pekonprinter.suppor_cosumer_produce;


import com.example.pekonprinter.printer_help.IPrintDataAnalysis;

public class ConsumerThread extends Thread {

    private DataChannel dataChannel;


    public ConsumerThread( DataChannel dataChannel) {

        this.dataChannel = dataChannel;

    }


    @Override
    public void run() {

        try {
            while (true) {
                IPrintDataAnalysis data = dataChannel.get();
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}