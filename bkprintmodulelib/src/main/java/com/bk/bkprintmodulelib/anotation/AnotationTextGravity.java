package com.example.pekonprinter.annotation;

import android.support.annotation.IntDef;

import com.example.pekonprinter.constans.TextGravity;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


@IntDef({TextGravity.GRAVITY_LEFT, TextGravity.GRAVITY_RIGHT,TextGravity.GRAVITY_CENTER})
@Retention(RetentionPolicy.SOURCE)
public @interface AnotationTextGravity {
}
