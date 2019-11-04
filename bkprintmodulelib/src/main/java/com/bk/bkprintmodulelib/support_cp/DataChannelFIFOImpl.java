package com.bk.bkprintmodulelib.support_cp;


import android.util.Log;

import com.bk.bkprintmodulelib.print_help.IPrintDataAnalysis;


/**
 * 先进先出的队列实现
 */
public class DataChannelFIFOImpl implements DataChannel {

    private static final int CAPACITY = 3;
    private IPrintDataAnalysis[] dataArray = new IPrintDataAnalysis[CAPACITY];
    private volatile int head;
    private volatile int tail;
    private volatile int count;

    @Override
    public synchronized void put(IPrintDataAnalysis data) throws InterruptedException {
        while (count >= CAPACITY) {
            Log.e("9527", "put: 桌子放满了面包，不能再放了");
            wait();
        }
        Log.e("9527", "put: "+dataArray.length);
        dataArray[tail] = data;
        tail = (tail + 1) % CAPACITY;
        count++;
        notifyAll();
    }


    @Override
    public synchronized IPrintDataAnalysis get() throws InterruptedException {
        while (count <= 0) {
            Log.e("9527", "get: 面包被吃货吃光，等待厨师生产");
            wait();
        }
        Log.e("9527", "get: "+dataArray.length);
        IPrintDataAnalysis data = dataArray[head];
        count--;
        head = (head + 1) % CAPACITY;
        notifyAll();
        return data;
    }
}