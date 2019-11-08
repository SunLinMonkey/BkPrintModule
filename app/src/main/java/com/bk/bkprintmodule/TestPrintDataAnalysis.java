package com.bk.bkprintmodule;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;


import com.bk.bkprintmodulelib.cosntants.TextGravity;
import com.bk.bkprintmodulelib.cosntants.TextSize;
import com.bk.bkprintmodulelib.factory.PrintContentFactory;
import com.bk.bkprintmodulelib.print_help.IPrintDataAnalysis;
import com.bk.bkprintmodulelib.print_help.PrintLineContentEntity;

import java.util.ArrayList;
import java.util.List;


/**
 * 打印测试手写模板，配置模板解析，应该在 implements IPrintDataAnalysis  后 解析成printdatas 返回给打印机
 */
public class TestPrintDataAnalysis implements IPrintDataAnalysis {

//    private final  int CAPACITY = Integer.MAX_VALUE;

    /**
     * 打印集合，用于返回给打印机解析使用
     */
    List<PrintLineContentEntity> printDatas = new ArrayList<>();



    public TestPrintDataAnalysis(Context context, String testContent) {
        printDatas.clear();
        printDatas.add(PrintContentFactory.createBlankLine());
        printDatas.add(PrintContentFactory.createText("---" + context.getString(R.string.start_print_test) + "---", TextSize.TEXT_SIZE_UP_3, TextGravity.GRAVITY_RIGHT));
        printDatas.add(PrintContentFactory.createBlankLine(2));
        printDatas.add(PrintContentFactory.createText(context.getString(R.string.our_company), TextSize.TEXT_SIZE_UP_4, TextGravity.GRAVITY_RIGHT));
        printDatas.add(PrintContentFactory.createBlankLine());
        printDatas.add(PrintContentFactory.createText(context.getString(R.string.title_hotline)+1234567, TextSize.TEXT_SIZE_DEFAULT));
        printDatas.add(PrintContentFactory.createBlankLine(2));
        printDatas.add(PrintContentFactory.createText(context.getString(R.string.end_print_test), TextSize.TEXT_SIZE_DOWN_2));
        printDatas.add(PrintContentFactory.createBlankLine());
        printDatas.add(PrintContentFactory.createText(context.getString(R.string.end_print_test)+"22", TextSize.TEXT_SIZE_DEFAULT, TextGravity.GRAVITY_CENTER));
        printDatas.add(PrintContentFactory.createBlankLine(2));
        printDatas.add(PrintContentFactory.createQRCode("23555222232323"));
        printDatas.add(PrintContentFactory.createBlankLine());
        Drawable drawable = context.getResources().getDrawable(R.mipmap.ic_launcher);
        printDatas.add(PrintContentFactory.createImage(drawable2Bitmap(drawable)));
        printDatas.add(PrintContentFactory.createText(testContent));
        printDatas.add(PrintContentFactory.createBlankLine(2));
        printDatas.add(PrintContentFactory.createBarCode("23555"));
        printDatas.add(PrintContentFactory.createBlankLine(2));
        printDatas.add(PrintContentFactory.createOpenCashBox());
        printDatas.add(PrintContentFactory.createCutPage());
        printDatas.add(PrintContentFactory.createStartCommand());
    }


    private Bitmap drawable2Bitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof NinePatchDrawable) {
            Bitmap bitmap = Bitmap
                    .createBitmap(
                            drawable.getIntrinsicWidth(),
                            drawable.getIntrinsicHeight(),
                            drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                                    : Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight());
            drawable.draw(canvas);
            return bitmap;
        } else {
            return null;
        }
    }

    @Override
    public List getPrintDatas() {
        return printDatas;
    }
}
