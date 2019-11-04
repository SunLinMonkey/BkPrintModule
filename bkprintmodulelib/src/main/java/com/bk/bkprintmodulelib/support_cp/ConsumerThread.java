package com.bk.bkprintmodulelib.support_cp;


import com.bk.bkprintmodulelib.print_help.IPrintDataAnalysis;

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