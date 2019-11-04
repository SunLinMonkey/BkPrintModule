package com.example.pekonprinter.printer.wifi;

/**
 * ip检查工具
 */
public class IPCheckUtils {


    /**
     * 检查IP
     * @param strIP1T
     * @param strIP2T
     * @param strIP3T
     * @param strIP4T
     */
    public static boolean checkIp(String strIP1T,String strIP2T,String strIP3T,String strIP4T){

        try {
            int ip1 = Integer.parseInt(strIP1T);
            int ip2 = Integer.parseInt(strIP2T);
            int ip3 = Integer.parseInt(strIP3T);
            int ip4 = Integer.parseInt(strIP4T);

            if (checkIpSingle(ip1)) return false;
            if (checkIpSingle(ip2)) return false;
            if (checkIpSingle(ip3)) return false;
            if (checkIpSingle(ip4)) return false;


        } catch (NumberFormatException e) {
//            showShortToast(resources.getString(R.string.ip_error));
            return false;
        }

        return true;


    }

    private static boolean checkIpSingle(int ip1) {
        if (ip1 > 255 || ip1 < 0) {
//            showShortToast(resources.getString(R.string.ip_error));
            return true;
        }
        return false;
    }

    /**
     * 检查端口
     * @param portNum
     */
    public static boolean checkPort(String portNum){
        // 端口号
        try {
            int port = Integer.parseInt(portNum);
            if (port > 65535 || port < 0) {
//                showShortToast(resources.getString(R.string.port_error));
                return false;
            }
        } catch (NumberFormatException e) {
//            showShortToast(resources.getString(R.string.port_error));
            return false;
        }
        return true;
    }
}
