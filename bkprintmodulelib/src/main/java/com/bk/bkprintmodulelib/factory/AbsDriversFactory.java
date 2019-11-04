package com.bk.bkprintmodulelib.factory;


import android.util.SparseArray;

import com.bk.bkprintmodulelib.print_help.IPrinter;

public abstract class AbsDriversFactory {

    public abstract SparseArray<IPrinter> getPekonPrinter();

}
