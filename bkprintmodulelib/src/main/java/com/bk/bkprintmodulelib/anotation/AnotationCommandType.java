package com.bk.bkprintmodulelib.anotation;


import android.support.annotation.IntDef;


import com.bk.bkprintmodulelib.cosntants.CommandType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({CommandType.CMMAND_TEXT, CommandType.CMMAND_BLANKLINE, CommandType.CMMAND_CASHBOX
        , CommandType.CMMAND_DEVIDERLINE, CommandType.CMMAND_BARCODE, CommandType.CMMAND_QRCODE, CommandType.CMMAND_IMAGE,CommandType.CMMAND_CUT_PAGE,CommandType.CMMAND_START_PRINT})
@Retention(RetentionPolicy.SOURCE)
public @interface AnotationCommandType {
}
