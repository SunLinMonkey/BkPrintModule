package com.bk.bkprintmodulelib.cosntants;

/**
 * 打印小票byte数组指令
 */
public class PrintCmd {
    /**
     * 初始化
     */
    public static String initialPrint() {
        byte[] result = new byte[2];
        result[0] = 27;
        result[1] = 64;
        return new String(result);
    }

    /**
     * 切纸
     */
    public static String CutString() {
        return new String(CutByte());
    }

    /**
     * 切纸
     */
    public static byte[] CutByte() {
        byte[] result = new byte[2];
        result[0] = 27;
        result[1] = 105;
        return result;
    }

    public static String openCashBox() {
        return "-n -e '\\x1b\\x70\\x00\\x3c\\xff'";
    }

    /**
     * 居中对齐
     */
    public static String alignCenter() {
        byte[] result = new byte[3];
        result[0] = 27;
        result[1] = 97;
        result[2] = 1;
        return new String(result);
    }

    /**
     * 左对齐
     */
    public static String alignLeft() {
        byte[] result = new byte[3];
        result[0] = 27;
        result[1] = 97;
        result[2] = 48;
        return new String(result);
    }

    /**
     * 右对齐
     */
    public static String alignRight() {
        byte[] result = new byte[3];
        result[0] = 27;
        result[1] = 97;
        result[2] = 2;
        return new String(result);
    }

    /**
     * 字体大小
     */
    public static String doubleFont() {
        byte[] result = new byte[3];
        result[0] = 29;
        result[1] = 33;
        result[2] = 17;
        return new String(result);
    }

    /**
     * 字体大小横向2倍
     */
    public static String doubleCancel1() {
        byte[] result = new byte[3];
        result[0] = 29;
        result[1] = 33;
        result[2] = 16;
        return new String(result);
    }

    /**
     * 字体大小为默认
     */
    public static String doubleCancel() {
        byte[] result = new byte[3];
        result[0] = 29;
        result[1] = 33;
        result[2] = 8;
        return new String(result);
    }

    /**
     * 制表
     */
    public static String tableLine24() {
        byte[] result = new byte[4];
        result[0] = 27;
        result[1] = 68;
        result[2] = 12;
        result[3] = 24;
        return new String(result);
    }

    /**
     * 制表
     */
    public static String tableLine32() {
        byte[] result = new byte[4];
        result[0] = 27;
        result[1] = 68;
        result[2] = 16;
        result[3] = 32;
        return new String(result);
    }

    /**
     * 制表
     */
    public static String Line() {
        byte[] result = new byte[1];
        result[0] = 9;
        return new String(result);
    }


    /**
     * 选择标准行距（包括字高）为3.75mm（30*0.125mm）。
     * 3.75mm 行距中：字高3mm（24 步*0.125mm）空行0.75mm（6 步*0.125mm）故一行高度为3.75mm
     * @return 标准行距
     */
    public static String standardRowSpacing(){
        byte[] byteArray = new byte[2];
        byteArray[0] = 27;
        byteArray[1] = 50;
        return new String(byteArray);
    }

    /**
     * 所设参数带字高度，如果设置 n≤18H（一倍字符高度）则按打印不空行处理，n≥18H（一倍字符高度）时多出的步数作为空行步数
     * @param rowStep 行距的步数
     * @return 行距
     */
    public static String rowSpacing(int rowStep){
        byte[] byteArray = new byte[3];
        byteArray[0] = 27;
        byteArray[1] = 51;
        byteArray[2] = (byte)rowStep;
        return new String(byteArray);
    }

    /**
     * 左边间隔是（n1*0.125mm），和右边间隔是（n2*0.125mm）
     * 当一行放不下左间距+字宽+右间距时换下一行
     * 这个命令在正常字符大小时设定左右两边间隔大小正常，
     * 当双倍宽时，字符间隔是正常间隔的两倍，
     * 当字符宽度被放大n 倍时，间隔同样被放大n 倍，即设定的右间隔跟字符宽度成正比
     * @param leftStep 左间距步数
     * @param rightStep 右间距步数
     * @return 字符的左右间隔
     */
    public static String leftRightSpacing(int leftStep,int rightStep){
        byte[] byteArray = new byte[4];
        byteArray[0] = 28;
        byteArray[1] = 83;
        byteArray[2] = (byte)leftStep;
        byteArray[3] = (byte)rightStep;
        return new String(byteArray);
    }

    /**
     * 设定字符右边的间隔，间隔为n*0.125mm（n*0.0049”）
     * 当字宽加间隔不能放得下一行打印该行并将该字写在下一行
     * 字符的右间距是在双倍宽模式下时是正常间隔的两倍，
     * 当字符宽度被放大n 倍时，右边间隔同样被横向放大n 倍，即设定的右间隔跟字符宽度成正比
     * @param rightStep 右间隔步数
     * @return 字符的右边间隔(未验证通过)
     */
    public static String rightSpacing(int rightStep){
        byte[] byteArray = new byte[3];
        byteArray[0] = 27;
        byteArray[1] = 32;
        byteArray[2] = (byte)rightStep;
        return new String(byteArray);
    }

    /**
     * 使用nL 和nH 命令设置左边空白
     * 左边空白设定为[（nl+nh*256）*0.125mm]
     * 设定该命令前打印缓冲区必须无打印数据。
     * 如果设置超过能打印区域，则设定左边界为打印区域，当有数据来时则靠右边腾出一个字宽+字间距写下一个字符
     * @param lowStep
     * @param highStep
     * @return 左边空白(未验证通过)
     */
    public static String leftBlank(int lowStep,int highStep){
        byte[] byteArray = new byte[4];
        byteArray[0] = 29;
        byteArray[1] = 76;
        byteArray[2] = (byte)lowStep;
        byteArray[3] = (byte)highStep;
        return new String(byteArray);
    }

    /**
     * 范围(0≤n≤255)（1≤垂直倍数≤8，1≤水平倍数≤8）
     * 选择字符高度使用位0到2 和选择字符宽度使用位4到7，
     * @param widthMultiple 倍宽系数
     * @param heightMultiple 倍高系数
     * @return 字体大小
     */
    public static String size(int widthMultiple,int heightMultiple){
        return new String(sizeBytes(widthMultiple,heightMultiple));
    }

    /**
     * 范围(0≤n≤255)（1≤垂直倍数≤8，1≤水平倍数≤8）
     * 选择字符高度使用位0到2 和选择字符宽度使用位4到7，
     * @param widthMultiple 倍宽系数
     * @param heightMultiple 倍高系数
     * @return 字体大小
     */
    public static  byte[] sizeBytes(int widthMultiple,int heightMultiple){
        byte[] byteArray = new byte[3];
        byteArray[0] = 29;
        byteArray[1] = 33;
        if(widthMultiple <= 0){
            widthMultiple = 1;
        }else if(widthMultiple >= 9){
            widthMultiple = 8;
        }
        if(heightMultiple <= 0){
            heightMultiple = 1;
        }else if(heightMultiple >= 9){
            heightMultiple = 8;
        }
        byteArray[2] = (byte)((widthMultiple - 1)  * 16 + (heightMultiple - 1));
        return byteArray;
    }

    /**
     * 该命令在打印区域执行的排列方式
     * 设定该命令前打印缓冲区必须无打印数据
     * 设定该命令后设定绝对打印则绝对打印无效数据按排列方式打印。
     * 设定了绝对打印再设排列方式，则排列方式无效数据按绝对打印位置打印。该命令与绝对打印关系：谁先设谁有效。
     * 注意绝对打印只是当前行有效，排列方式是设定后不改变则一直有效
     * 0或48靠左，1或49居中，2或50居右
     * @param gravity 排列方式
     * @return 排列
     */
    public static String gravity(String gravity){
        return gravity(gravity,-1);
    }

    /**
     * 该命令在打印区域执行的排列方式
     * 设定该命令前打印缓冲区必须无打印数据
     * 设定该命令后设定绝对打印则绝对打印无效数据按排列方式打印。
     * 设定了绝对打印再设排列方式，则排列方式无效数据按绝对打印位置打印。该命令与绝对打印关系：谁先设谁有效。
     * 注意绝对打印只是当前行有效，排列方式是设定后不改变则一直有效
     * 0或48靠左，1或49居中，2或50居右
     * @param gravity 排列方式
     * @return 排列
     */
    public static String gravity(String gravity,int printType){
        byte gravityByte = 0;
        if(gravity != null){
            switch (gravity){
                case Gravity.LEFT:
                    if(printType == -1 || printType != PekonPrinterType.PRINTER_USB_PARALLEL){
                        gravityByte = 0;
                    }else{
                        gravityByte = 48;
                    }
                    break;
                case Gravity.CENTER:
                    gravityByte = 1;
                    break;
                case Gravity.RIGHT:
                    gravityByte = 2;
                    break;
                default:
                    if(printType == -1 || printType != PekonPrinterType.PRINTER_USB_PARALLEL){
                        gravityByte = 0;
                    }else{
                        gravityByte = 48;
                    }
                    break;
            }
        }
        byte[] byteArray = new byte[3];
        byteArray[0] = 27;
        byteArray[1] = 97;
        byteArray[2] = gravityByte;
        return new String(byteArray);
    }

    /**
     * 清除打印缓冲区的数据,清除各项命令设置（加重，双击，下划线，特定大小，相反打印等）。
     * 不清除接收缓冲区。
     * @return 初始化打印机
     */
    public static String init(){
        return new String(initByte());
    }

    /**
     * 清除打印缓冲区的数据,清除各项命令设置（加重，双击，下划线，特定大小，相反打印等）。
     * 不清除接收缓冲区。
     * @return 初始化打印机
     */
    public static  byte[] initByte(){
        byte[] byteArray = new byte[2];
        byteArray[0] = 27;
        byteArray[1] = 64;
        return byteArray;
    }


    /**
     * 切纸操作，把纸全切断
     * @return 全切纸
     */
    public static String cut(){
        byte[] byteArray = new byte[2];
        byteArray[0] = 27;
        byteArray[1] = 105;
        return new String(byteArray);
    }

    /**
     * 切纸操作，把纸全切断
     * @return 全切纸
     */
    public static byte[]  cutByteArray(){
        byte[] byteArray = new byte[2];
        byteArray[0] = 27;
        byteArray[1] = 105;
        return byteArray;
    }

    /**
     * 切纸操作，保留一点不切
     * 切纸刀必须要有半切纸的功能，如果无半切功能则全切处理
     * @return 半切纸
     */
    public static String halfCut(){
        byte[] byteArray = new byte[2];
        byteArray[0] = 27;
        byteArray[1] = 109;
        return new String(byteArray);
    }

    /**
     * 商米蓝牙切纸
     * @return
     */
    public static String bluetoothCut(){
        byte[] byteArray = new byte[4];
        byteArray[0] = 29;
        byteArray[1] = 86;
        byteArray[2] = 66;
        byteArray[3] = 0;
        return new String(byteArray);
    }

    /**
     * 开钱箱
     * m n1 n2 可以为随意的值（后三位）
     * 在执行开锁命令操作期间不能重复发送开钱箱命令，开钱箱期间重复发送的开钱箱，命令被屏蔽不执行。
     * @return 开钱箱
     */
    public static String openMoneyBox(){
        byte[] byteArray = new byte[5];
        byteArray[0] = 27;
        byteArray[1] = 112;
        byteArray[2] = 0;
        byteArray[3] = 0;
        byteArray[4] = 0;
        return new String(byteArray);
    }



    /**
     * 蓝色商米开钱箱
     * @return
     */
    public static String openBluetoothBox(){
        byte[] openCashbox = new byte[5];
        openCashbox[0] = 0x10;
        openCashbox[1] = 0x14;
        openCashbox[2] = 0x00;
        openCashbox[3] = 0x00;
        openCashbox[4] = 0x00;
        return new String(openCashbox);
    }

    /**
     * 拍档开钱箱
     * @return
     */
    public static String openPartnerBoxString(){
        return new String(openPartnerBoxByte());
    }

    /**
     * 拍档开钱箱
     * @return
     */
    public static byte[] openPartnerBoxByte(){
        byte[] openCashBox = new byte[5];
        openCashBox[0] = 0x1B;
        openCashBox[1] = 0x70;
        openCashBox[2] = 0x00;
        openCashBox[3] = 0x3c;
        openCashBox[4] = 0x01;
        return openCashBox;
    }




    /**
     * 拍档打印二维码指令、方法
     * @param
     * @return
     */
    public static String generate2DBarcodePartner(int barcode2DStringBytesLength,int size) {
        StringBuilder builder = new StringBuilder();
//		byte[] data;
        try {
//			data = barcode2DString.getBytes("gbk");
            byte[] buffer = new byte[10];
            buffer[0] = 29;
            buffer[1] = 90;
            //生成二维码的类型PDF417、DATAMATRIX(Ecc200)、QR-CODE、Micro PDF417、Truncated PDF417
            buffer[2] = (byte) 2;

            buffer[3] = 27;
            buffer[4] = 90;

            buffer[5] = (byte) 0;
            buffer[6] = (byte) 0x51;

            buffer[7] = (byte) size;//用来控制二维码的大小
            buffer[8] = (byte) (barcode2DStringBytesLength % 256);
            buffer[9] = (byte) (barcode2DStringBytesLength / 256);
            builder.append(new String(buffer));
//			builder.append(new String(data));
//			builder.append(new String(new byte[]{10}));
            return builder.toString();
        } catch (Exception e) {
        }
        return "";
    }


    public static byte[] printBarcode(String stBarcode) {
        int iLength = stBarcode.length() + 4;
        byte[] returnText = new byte[iLength];

        returnText[0] = 0x1D;
        returnText[1] = 'k';
        returnText[2] = 0x45;
        returnText[3] = (byte) stBarcode.length(); // 条码长度；

        System.arraycopy(stBarcode.getBytes(), 0, returnText, 4,
                stBarcode.getBytes().length);

        return returnText;
    }


    interface Gravity{
        String LEFT = "left";//居左
        String CENTER = "center";//居中
        String RIGHT = "right";//居右
    }

}