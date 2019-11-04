package com.example.pekonprinter.suppor_cosumer_produce;

import java.util.Random;

public class ProducerThread extends Thread {

    public static int index;
    private String threadName;
    private DataChannel dataChannel;
    private Random random;

    public ProducerThread(String threadName, DataChannel dataChannel, long randomSeed) {
        this.threadName = threadName;
        this.dataChannel = dataChannel;
        this.random = new Random(randomSeed);
        setName(threadName);
    }


    @Override
    public void run() {
        try {
            while (true) {
                Thread.sleep(random.nextInt(500));
                Data data = new Data();
                data.setName(String.format("%s产生的第%s个面包", threadName, getIndex()));
//                dataChannel.put(data);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private synchronized int getIndex() {
        return ++index;
    }
}