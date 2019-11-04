package com.example.pekondrivers.factory;

import android.util.SparseArray;

import com.example.pekondrivers.printer.IPrinter;

import java.util.List;

public abstract class AbsDriversFactory {

    public abstract SparseArray<IPrinter> getPekonPrinter();

}
