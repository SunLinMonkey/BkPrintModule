package com.bk.bkprintmodulelib.anotation;

import android.support.annotation.IntDef;


import com.bk.bkprintmodulelib.cosntants.TextGravity;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


@IntDef({TextGravity.GRAVITY_LEFT, TextGravity.GRAVITY_RIGHT,TextGravity.GRAVITY_CENTER})
@Retention(RetentionPolicy.SOURCE)
public @interface AnotationTextGravity {
}
