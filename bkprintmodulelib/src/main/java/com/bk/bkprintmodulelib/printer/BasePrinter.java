package com.bk.bkprintmodulelib.printer;

import com.bk.bkprintmodulelib.factory.HelpEntityFactory;
import com.bk.bkprintmodulelib.print_help.HelpEntity;

public abstract class BasePrinter {

    /**
     * 打印辅助信息，如 字体大小，居中等
     */
    private HelpEntity helpEntity;

    private HelpEntity lastHelpEntity = HelpEntityFactory.getDefaultHelpEntity();


    protected boolean isConnected = false;


    protected boolean isPrinterReady = false;


    public void setHelpEntity(HelpEntity helpEntity) {
        lastHelpEntity = this.helpEntity;
        this.helpEntity = helpEntity;
    }

    /**
     * USB并口用的
     *
     * @return
     */
    protected boolean isTextSizeChanged() {
        if (lastHelpEntity == null) {
            return true;
        }
        return lastHelpEntity.getTestSize() != helpEntity.getTestSize();
    }

    protected HelpEntity getHelpEntity() {
        if (helpEntity == null) {
            return HelpEntityFactory.getDefaultHelpEntity();
        }
        return helpEntity;
    }


    protected abstract void getLocalTextSize();

    protected abstract void getLocalGravity();


}
