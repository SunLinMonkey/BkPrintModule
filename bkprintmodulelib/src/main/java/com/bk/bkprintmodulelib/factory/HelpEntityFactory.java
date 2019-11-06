package com.bk.bkprintmodulelib.factory;

import com.bk.bkprintmodulelib.cosntants.TextGravity;
import com.bk.bkprintmodulelib.cosntants.TextSize;
import com.bk.bkprintmodulelib.print_help.HelpEntity;

public class HelpEntityFactory {

    public static HelpEntity getDefaultHelpEntity() {
        HelpEntity helpEntity = new HelpEntity();
        helpEntity.setTestSize(TextSize.TEXT_SIZE_DEFAULT);
        helpEntity.setBold(false);
        helpEntity.setNeedUnderLine(false);
        helpEntity.setGrivaty(TextGravity.GRAVITY_LEFT);
        return helpEntity;
    }


    public static HelpEntity getCenterDefault() {
        HelpEntity helpEntity = new HelpEntity();
        helpEntity.setTestSize(TextSize.TEXT_SIZE_DEFAULT);
        helpEntity.setBold(false);
        helpEntity.setNeedUnderLine(false);
        helpEntity.setGrivaty(TextGravity.GRAVITY_CENTER);
        return helpEntity;
    }


}
