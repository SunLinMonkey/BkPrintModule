package com.bk.bkprintmodulelib.printer.parter;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by syd on 2017/2/13.
 */

public final class SocketTool {
    static String ip = "127.0.0.1";
    static int port = 6010;
    static String deviceConnectString = "error";

    static String cmd1 = "bls -l /sys/bus/usb-serial/devices/ |grep 1-1.3.3:1.0";
    static String cmd2 = "bls -l /sys/bus/usb-serial/devices/ |grep 1-1.3.4:1.0";

    public static String getTtyusbInternal() {
        return setEnable(cmd2);
    }

    public static String getTtyusbExternal() {
        return setEnable(cmd1);
    }

    public static String setEnable(String cmd) {
        try {
            Socket socket = new Socket(ip, port);
            BufferedInputStream in = new BufferedInputStream(socket.getInputStream());
            BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream());

            out.write(cmd.getBytes());
            out.flush();

            byte[] result = getDataFromSocket(in, 1280);
            if (socket != null) {
                socket.close();
                out.close();
                in.close();
                out = null;
                in = null;
                socket = null;
            }
            if (result[0] == 111) {
                return "error";
            }
            if (result[0] == 102) {
                return "error";
            }
            if (result[0] == 114) {
                String test = new String(result);
                if (test.indexOf("ttyUSB") < 0) {
                    return "error";
                }
                test = test.substring(test.lastIndexOf("/") + 1, test.length() - 1);

                return test;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
        return "error";
    }

    public static byte[] getDataFromSocket(BufferedInputStream in, int length) throws IOException, InterruptedException {
        int i = 0;
        int readed;
        while (true) {
            readed = in.available();
            if (readed > 0) break;
            Thread.sleep(500L);
            i++;
            if (i > 8) {
                return new byte[0];
            }

        }

        byte[] buf = new byte[length];

        int count = 0;
        i = 0;
        while (count < length) {
            readed = in.available();
            if (readed > 0) {
                count += in.read(buf, count, length - count);
                i = 0;
            } else {
                i++;
                Thread.sleep(50L);
            }
            if (i > 5) {
                break;
            }
        }
        byte[] buf1 = new byte[count];

        System.arraycopy(buf, 0, buf1, 0, count);
        return buf1;
    }
    public static String getFilePath(){
        try {
            Thread mThread = new Thread() {
                public void run() {
                    int RunLoop = 0;
                    deviceConnectString = "error";
                    while (true) {
                        try {
                            deviceConnectString = SocketTool.getTtyusbInternal();
                        } catch (Exception e) {

                        }
                        RunLoop++;
                        if (deviceConnectString != "error") {
                            break;
                        }
                        if (RunLoop > 1)
                            break;
                        try {
                            Thread.sleep(200L);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            mThread.start();
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } catch (Exception e) {

        }
        return deviceConnectString;
    }
}
