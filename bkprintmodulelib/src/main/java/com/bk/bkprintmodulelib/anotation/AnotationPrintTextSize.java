package com.example.pekonprinter.annotation;

import android.support.annotation.IntDef;


import com.example.pekonprinter.constans.TextSize;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({TextSize.TEXT_SIZE_DOWN_2, TextSize.TEXT_SIZE_DOWN_1, TextSize.TEXT_SIZE_DEFAULT, TextSize.TEXT_SIZE_UP_3, TextSize.TEXT_SIZE_UP_4})
@Retention(RetentionPolicy.SOURCE)
public @interface AnotationPrintTextSize {
}
