package com.example.pekonprinter.printer.startwifi;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.util.Pair;

import com.starmicronics.stario.StarIOPort;
import com.starmicronics.stario.StarIOPortException;
import com.starmicronics.stario.StarPrinterStatus;
import com.starmicronics.starioextension.IPeripheralCommandParser;
import com.starmicronics.starioextension.IPeripheralCommandParser.ParseResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"UnusedParameters", "UnusedAssignment", "WeakerAccess"})
public class Communication {
    @SuppressWarnings("unused")
    public enum Result {
        Success,
        ErrorUnknown,
        ErrorOpenPort,
        ErrorBeginCheckedBlock,
        ErrorEndCheckedBlock,
        ErrorWritePort,
        ErrorReadPort,
    }

    interface StatusCallback {
        void onStatus(StarPrinterStatus result);
    }

    interface FirmwareInformationCallback {
        void onFirmwareInformation(Map<String, String> firmwareInformationMap);
    }

    interface SerialNumberCallback {
        void onSerialNumber(Result communicateResult, String serialNumber);
    }

    public interface SendCallback {
        void onStatus(boolean result, Result communicateResult);
    }

    interface PrintRedirectionCallback {
        void onStatus(List<Pair<String, Result>> communicateResultList);
    }

    public static void sendCommands(Object lock, byte[] commands, StarIOPort port, SendCallback callback) {
        SendCommandThread thread = new SendCommandThread(lock, commands, port, callback);
        thread.start();
    }

    public static void sendCommandsDoNotCheckCondition(Object lock, byte[] commands, StarIOPort port, SendCallback callback) {
        SendCommandDoNotCheckConditionThread thread = new SendCommandDoNotCheckConditionThread(lock, commands, port, callback);
        thread.start();
    }

    public static void sendCommands(Object lock, byte[] commands, String portName, String portSettings, int timeout, Context context, SendCallback callback) {
        SendCommandThread thread = new SendCommandThread(lock, commands, portName, portSettings, timeout, context, callback);
        thread.start();
    }

    public static void sendCommandsDoNotCheckCondition(Object lock, byte[] commands, String portName, String portSettings, int timeout, Context context, SendCallback callback) {
        SendCommandDoNotCheckConditionThread thread = new SendCommandDoNotCheckConditionThread(lock, commands, portName, portSettings, timeout, context, callback);
        thread.start();
    }

    public static void sendCommandsForRedirection(Object lock, byte[] commands, List<PrinterSettings> printerSettingsList, int timeout, Context context, PrintRedirectionCallback callback) {
        SendCommandForRedirectionThread thread = new SendCommandForRedirectionThread(lock, commands, printerSettingsList, timeout, context, callback);
        thread.start();
    }

    public static void retrieveStatus(Object lock, String portName, String portSettings, int timeout, Context context, StatusCallback callback) {
        RetrieveStatusThread thread = new RetrieveStatusThread(lock, portName, portSettings, timeout, context, callback);
        thread.start();
    }

    public static void getFirmwareInformation(Object lock, String portName, String portSettings, int timeout, Context context, FirmwareInformationCallback callback) {
        GetFirmwareInformationThread thread = new GetFirmwareInformationThread(lock, portName, portSettings, timeout, context, callback);
        thread.start();
    }

    public static void getSerialNumber(Object lock, String portName, String portSettings, int timeout, Context context, SerialNumberCallback callback) {
        GetSerialNumberThread thread = new GetSerialNumberThread(lock, portName, portSettings, timeout, context, callback);
        thread.start();
    }

    public static void parseDoNotCheckCondition(Object lock, IPeripheralCommandParser parser, String portName, String portSettings, int timeout, Context context, SendCallback callback) {
        ParseDoNotCheckConditionThread thread = new ParseDoNotCheckConditionThread(lock, parser, portName, portSettings, timeout, context, callback);
        thread.start();
    }

    public static void parseDoNotCheckCondition(Object lock, IPeripheralCommandParser parser, StarIOPort port, SendCallback callback) {
        ParseDoNotCheckConditionThread thread = new ParseDoNotCheckConditionThread(lock, parser, port, callback);
        thread.start();
    }

    public static void setUsbSerialNumber(Object lock, byte[] usbSerialNumber, boolean enable, String portName, String portSettings, int timeout, Context context, SendCallback callback) {
        SetUsbSerialNumberThread thread = new SetUsbSerialNumberThread(lock, usbSerialNumber, enable, portName, portSettings, timeout, context, callback);
        thread.start();
    }

    public static void initializeUsbSerialNumber(Object lock, boolean enable, String portName, String portSettings, int timeout, Context context, SendCallback callback) {
        InitializeUsbSerialNumberThread thread = new InitializeUsbSerialNumberThread(lock, enable, portName, portSettings, timeout, context, callback);
        thread.start();
    }
}

class SendCommandThread extends Thread {
    private final Object mLock;
    private Communication.SendCallback mCallback;
    private byte[] mCommands;

    private StarIOPort mPort;

    private String mPortName = null;
    private String mPortSettings;
    private int     mTimeout;
    private Context mContext;

    SendCommandThread(Object lock, byte[] commands, StarIOPort port, Communication.SendCallback callback) {
        mCommands = commands;
        mPort     = port;
        mCallback = callback;
        mLock     = lock;
    }

    SendCommandThread(Object lock, byte[] commands, String portName, String portSettings, int timeout, Context context, Communication.SendCallback callback) {
        mCommands     = commands;
        mPortName     = portName;
        mPortSettings = portSettings;
        mTimeout      = timeout;
        mContext      = context;
        mCallback     = callback;
        mLock         = lock;
    }

    @Override
    public void run() {
        Communication.Result communicateResult = Communication.Result.ErrorOpenPort;
        boolean result = false;

        synchronized (mLock) {
            try {
                if (mPort == null) {

                    if (mPortName == null) {
                        resultSendCallback(false, communicateResult, mCallback);
                        return;
                    } else {
                        mPort = StarIOPort.getPort(mPortName, mPortSettings, mTimeout, mContext);
                    }
                }
                if (mPort == null) {
                    communicateResult = Communication.Result.ErrorOpenPort;
                    resultSendCallback(false, communicateResult, mCallback);
                    return;
                }

                StarPrinterStatus status;

                communicateResult = Communication.Result.ErrorBeginCheckedBlock;

                status = mPort.beginCheckedBlock();

                if (status.offline) {
                    throw new StarIOPortException("A printer is offline.");
                }

                communicateResult = Communication.Result.ErrorWritePort;

                mPort.writePort(mCommands, 0, mCommands.length);

                communicateResult = Communication.Result.ErrorEndCheckedBlock;

                mPort.setEndCheckedBlockTimeoutMillis(30000);     // 30000mS!!!

                status = mPort.endCheckedBlock();

                if (status.coverOpen) {
                    throw new StarIOPortException("Printer cover is open");
                } else if (status.receiptPaperEmpty) {
                    throw new StarIOPortException("Receipt paper is empty");
                } else if (status.offline) {
                    throw new StarIOPortException("Printer is offline");
                }

                result = true;
                communicateResult = Communication.Result.Success;
            } catch (StarIOPortException e) {
                // Nothing
            }

            if (mPort != null && mPortName != null) {
                try {
                    StarIOPort.releasePort(mPort);
                } catch (StarIOPortException e) {
                    // Nothing
                }
                mPort = null;
            }

            resultSendCallback(result, communicateResult, mCallback);
        }
    }

    private static void resultSendCallback(final boolean result, final Communication.Result communicateResult, final Communication.SendCallback callback) {
        if (callback != null) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onStatus(result, communicateResult);
                }
            });
        }
    }
}

class SendCommandDoNotCheckConditionThread extends Thread {
    private final Object mLock;
    private Communication.SendCallback mCallback;
    private byte[] mCommands;

    private StarIOPort mPort;

    private String mPortName = null;
    private String mPortSettings;
    private int     mTimeout;
    private Context mContext;

    SendCommandDoNotCheckConditionThread(Object lock, byte[] commands, StarIOPort port, Communication.SendCallback callback) {
        mLock     = lock;
        mCommands = commands;
        mPort     = port;
        mCallback = callback;
    }

    SendCommandDoNotCheckConditionThread(Object lock, byte[] commands, String portName, String portSettings, int timeout, Context context, Communication.SendCallback callback) {
        mLock         = lock;
        mCommands     = commands;
        mPortName     = portName;
        mPortSettings = portSettings;
        mTimeout      = timeout;
        mContext      = context;
        mCallback     = callback;
    }

    @Override
    public void run() {
        Communication.Result communicateResult = Communication.Result.ErrorOpenPort;
        boolean result = false;

        synchronized (mLock) {
            try {
                if (mPort == null) {

                    if (mPortName == null) {
                        resultSendCallback(false, communicateResult, mCallback);
                        return;
                    } else {
                        mPort = StarIOPort.getPort(mPortName, mPortSettings, mTimeout, mContext);
                    }
                }
                if (mPort == null) {
                    communicateResult = Communication.Result.ErrorOpenPort;
                    resultSendCallback(false, communicateResult, mCallback);
                    return;
                }

                StarPrinterStatus status;

                communicateResult = Communication.Result.ErrorWritePort;

                status = mPort.retreiveStatus();

                if (status.rawLength == 0) {
                    throw new StarIOPortException("Unable to communicate with printer.");
                }

                communicateResult = Communication.Result.ErrorWritePort;

                mPort.writePort(mCommands, 0, mCommands.length);

//                if (status.coverOpen) {
//                    throw new StarIOPortException("Printer cover is open");
//                } else if (status.receiptPaperEmpty) {
//                    throw new StarIOPortException("Receipt paper is empty");
//                } else if (status.offline) {
//                    throw new StarIOPortException("Printer is offline");
//                }

                communicateResult = Communication.Result.Success;
                result = true;
            } catch (StarIOPortException e) {
                // Nothing
            }

            if (mPort != null && mPortName != null) {
                try {
                    StarIOPort.releasePort(mPort);
                } catch (StarIOPortException e) {
                    // Nothing
                }
                mPort = null;
            }

            resultSendCallback(result, communicateResult, mCallback);
        }
    }

    private static void resultSendCallback(final boolean result, final Communication.Result communicateResult, final Communication.SendCallback callback) {
        if (callback != null) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onStatus(result, communicateResult);
                }
            });
        }
    }
}

class SendCommandForRedirectionThread extends Thread {
    private final Object mLock;
    private byte[]                                 mCommands;
    private List<PrinterSettings> mPrinterSettingsList;
    private Context mContext;
    private Communication.PrintRedirectionCallback mCallback;
    private int                                    mTimeout;

    SendCommandForRedirectionThread(Object lock, byte[] commands, List<PrinterSettings> printerSettingsList, int timeout, Context context, Communication.PrintRedirectionCallback callback) {
        mLock                = lock;
        mCommands            = commands;
        mPrinterSettingsList = printerSettingsList;
        mContext             = context;
        mCallback            = callback;
        mTimeout             = timeout;
    }

    @Override
    public void run() {
        List<Pair<String, Communication.Result>> communicationResultList = new ArrayList<>();

        synchronized (mLock) {
            Communication.Result communicateResult = Communication.Result.ErrorOpenPort;

            for (PrinterSettings printerSettings : mPrinterSettingsList) {
                StarIOPort port = null;

                String portName     = printerSettings.getPortName();
                String portSettings = printerSettings.getPortSettings();

                try {
                    communicateResult = Communication.Result.ErrorOpenPort;

                    port = StarIOPort.getPort(portName, portSettings, mTimeout, mContext);

                    communicateResult = Communication.Result.ErrorBeginCheckedBlock;

                    StarPrinterStatus status = port.beginCheckedBlock();

                    if (status.offline) {
                        throw new StarIOPortException("A printer is offline.");
                    }

                    communicateResult = Communication.Result.ErrorWritePort;

                    port.writePort(mCommands, 0, mCommands.length);

                    communicateResult = Communication.Result.ErrorEndCheckedBlock;

                    port.setEndCheckedBlockTimeoutMillis(30000);     // 30000mS!!!

                    status = port.endCheckedBlock();

                    if (status.coverOpen) {
                        throw new StarIOPortException("Printer cover is open");
                    } else if (status.receiptPaperEmpty) {
                        throw new StarIOPortException("Receipt paper is empty");
                    } else if (status.offline) {
                        throw new StarIOPortException("Printer is offline");
                    }

                    communicateResult = Communication.Result.Success;
                } catch (StarIOPortException e) {
                    // Nothing
                }

                if (port != null) {
                    try {
                        StarIOPort.releasePort(port);
                    } catch (StarIOPortException e) {
                        // Nothing
                    }
                }

                communicationResultList.add(new Pair<>(portName, communicateResult));

                if (communicateResult == Communication.Result.Success) {
                    break;
                }
            }

            resultSendCallback(communicationResultList, mCallback);
        }
    }

    private static void resultSendCallback(final List<Pair<String, Communication.Result>> communicationResultList, final Communication.PrintRedirectionCallback callback) {
        if (callback != null) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onStatus(communicationResultList);
                }
            });
        }
    }
}

class ParseDoNotCheckConditionThread extends Thread {
    private final Object mLock;
    private IPeripheralCommandParser mParser;
    private StarIOPort mPort;
    private Communication.SendCallback mCallback;

    private String mPortName = null;
    private String mPortSettings;
    private int     mTimeout;
    private Context mContext;

    ParseDoNotCheckConditionThread(Object lock, IPeripheralCommandParser parser, StarIOPort port, Communication.SendCallback callback) {
        mLock     = lock;
        mParser   = parser;
        mPort     = port;
        mCallback = callback;
    }

    ParseDoNotCheckConditionThread(Object lock, IPeripheralCommandParser parser, String portName, String portSettings, int timeout, Context context, Communication.SendCallback callback) {
        mLock         = lock;
        mParser       = parser;
        mPortName     = portName;
        mPortSettings = portSettings;
        mTimeout      = timeout;
        mContext      = context;
        mCallback     = callback;
    }

    @Override
    public void run() {
        Communication.Result communicateResult = Communication.Result.ErrorOpenPort;
        boolean result = false;

        synchronized (mLock) {
            try {
                if (mPort == null) {

                    if (mPortName == null) {
                        resultSendCallback(false, communicateResult, mCallback);
                        return;
                    } else {
                        mPort = StarIOPort.getPort(mPortName, mPortSettings, mTimeout, mContext);
                    }
                }

                communicateResult = Communication.Result.ErrorWritePort;

                StarPrinterStatus status = mPort.retreiveStatus();

                if (status.rawLength == 0) {
                    throw new StarIOPortException("Unable to communicate with printer.");
                }

                communicateResult = Communication.Result.ErrorWritePort;
                byte[] sendData = mParser.createSendCommands();
                mPort.writePort(sendData, 0, sendData.length);

                List<Byte> receiveDataList = new ArrayList<>();
                byte[]     readBuffer      = new byte[1024];

                long start = System.currentTimeMillis();

                while (true) {
                    if (1000 < (System.currentTimeMillis() - start)) {
                        communicateResult = Communication.Result.ErrorReadPort;
                        break;
                    }

                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        // Do nothing
                    }

                    int receiveSize = mPort.readPort(readBuffer, 0, readBuffer.length);

                    if (0 < receiveSize) {
                        for (int i = 0; i < receiveSize; i++) {
                            receiveDataList.add(readBuffer[i]);
                        }
                    }
                    else {
                        continue;
                    }

                    byte[] receiveData = new byte[receiveDataList.size()];

                    int receiveDataLength = receiveDataList.size();

                    for (int i = 0; i < receiveDataLength; i++) {
                        receiveData[i] = receiveDataList.get(i);
                    }

                    if (mParser.parse(receiveData, receiveDataLength) == ParseResult.Success) {
                        result = true;
                        communicateResult = Communication.Result.Success;
                        break;
                    }
                }
            } catch (StarIOPortException e) {
                // Nothing
            }

            if (mPort != null && mPortName != null) {
                try {
                    StarIOPort.releasePort(mPort);
                } catch (StarIOPortException e) {
                    // Nothing
                }
                mPort = null;
            }

            resultSendCallback(result, communicateResult, mCallback);
        }
    }

    private static void resultSendCallback(final boolean result, final Communication.Result communicateResult, final Communication.SendCallback callback) {
        if (callback != null) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onStatus(result, communicateResult);
                }
            });
        }
    }
}

class RetrieveStatusThread extends Thread {
    private final Object mLock;
    private Communication.StatusCallback mCallback;

    private StarIOPort mPort;

    private String mPortName = null;
    private String mPortSettings;
    private int     mTimeout;
    private Context mContext;

    @SuppressWarnings("unused")
    RetrieveStatusThread(Object lock, StarIOPort port, Communication.StatusCallback callback) {
        mPort     = port;
        mCallback = callback;
        mLock     = lock;
    }

    RetrieveStatusThread(Object lock, String portName, String portSettings, int timeout, Context context, Communication.StatusCallback callback) {
        mPortName     = portName;
        mPortSettings = portSettings;
        mTimeout      = timeout;
        mContext      = context;
        mCallback     = callback;
        mLock         = lock;
    }

    @Override
    public void run() {

        synchronized (mLock) {
            StarPrinterStatus status = null;

            try {
                if (mPort == null) {

                    if (mPortName == null) {
                        resultSendCallback(null, mCallback);
                        return;
                    } else {
                        mPort = StarIOPort.getPort(mPortName, mPortSettings, mTimeout, mContext);
                    }
                }
                if (mPort == null) {
                    resultSendCallback(null, mCallback);
                    return;
                }

                status = mPort.retreiveStatus();

            } catch (StarIOPortException e) {
                // Nothing
            }

            if (mPort != null && mPortName != null) {
                try {
                    StarIOPort.releasePort(mPort);
                } catch (StarIOPortException e) {
                    // Nothing
                }
                mPort = null;
            }

            resultSendCallback(status, mCallback);
        }
    }

    private static void resultSendCallback(final StarPrinterStatus status, final Communication.StatusCallback callback) {
        if (callback != null) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onStatus(status);
                }
            });
        }
    }
}

class GetFirmwareInformationThread extends Thread {
    private final Object mLock;
    private Communication.FirmwareInformationCallback mCallback;

    private StarIOPort mPort;

    private String mPortName = null;
    private String mPortSettings;
    private int     mTimeout;
    private Context mContext;

    GetFirmwareInformationThread(Object lock, String portName, String portSettings, int timeout, Context context, Communication.FirmwareInformationCallback callback) {
        mPortName     = portName;
        mPortSettings = portSettings;
        mTimeout      = timeout;
        mContext      = context;
        mCallback     = callback;
        mLock         = lock;
    }

    @Override
    public void run() {

        synchronized (mLock) {
            Map<String, String> firmwareInformationMap = null;

            try {
                if (mPort == null) {
                    if (mPortName == null) {
                        resultSendCallback(null, mCallback);
                        return;
                    } else {
                        mPort = StarIOPort.getPort(mPortName, mPortSettings, mTimeout, mContext);
                    }
                }
                if (mPort == null) {
                    resultSendCallback(null, mCallback);
                    return;
                }

                firmwareInformationMap = mPort.getFirmwareInformation();

            } catch (StarIOPortException e) {
                // Nothing
            }

            if (mPort != null && mPortName != null) {
                try {
                    StarIOPort.releasePort(mPort);
                } catch (StarIOPortException e) {
                    // Nothing
                }
                mPort = null;
            }

            resultSendCallback(firmwareInformationMap, mCallback);
        }
    }

    private static void resultSendCallback(final Map<String, String> firmwareInformationMap, final Communication.FirmwareInformationCallback callback) {
        if (callback != null) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onFirmwareInformation(firmwareInformationMap);
                }
            });
        }
    }
}

class GetSerialNumberThread extends Thread {
    private final Object mLock;
    private Communication.SerialNumberCallback mCallback;

    private StarIOPort mPort;

    private String mPortName = null;
    private String mPortSettings;
    private int     mTimeout;
    private Context mContext;

    GetSerialNumberThread(Object lock, String portName, String portSettings, int timeout, Context context, Communication.SerialNumberCallback callback) {
        mPortName     = portName;
        mPortSettings = portSettings;
        mTimeout      = timeout;
        mContext      = context;
        mCallback     = callback;
        mLock         = lock;
    }

    @Override
    public void run() {
        Communication.Result communicateResult = Communication.Result.ErrorOpenPort;

        String serialNumber = "";

        synchronized (mLock) {
            try {
                if (mPort == null) {
                    if (mPortName == null) {
                        resultSendCallback(communicateResult, null, mCallback);
                        return;
                    } else {
                        mPort = StarIOPort.getPort(mPortName, mPortSettings, mTimeout, mContext);
                    }
                }
                if (mPort == null) {
                    resultSendCallback(communicateResult, null, mCallback);
                    return;
                }

                communicateResult = Communication.Result.ErrorWritePort;

                StarPrinterStatus status = mPort.retreiveStatus();

                if (status.rawLength == 0) {
                    throw new StarIOPortException("Unable to communicate with printer.");
                }

                byte[] getInformationCommand = new byte[]{ 0x1b, 0x1d, 0x29, 0x49, 0x01, 0x00, 49 };  // ESC GS ) I pL pH fn (Transmit printer information command)

                mPort.writePort(getInformationCommand, 0, getInformationCommand.length);

                List<Byte> receiveDataList = new ArrayList<>();
                byte[]     readBuffer      = new byte[1024];

                long start = System.currentTimeMillis();

                String information = "";

                while (true) {
                    if (3000 < (System.currentTimeMillis() - start)) {
                        communicateResult = Communication.Result.ErrorReadPort;

                        throw new StarIOPortException("Timeout");
                    }

                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        // Do nothing
                    }

                    int receiveSize = mPort.readPort(readBuffer, 0, readBuffer.length);

                    if (0 < receiveSize) {
                        for (int i = 0; i < receiveSize; i++) {
                            receiveDataList.add(readBuffer[i]);
                        }
                    }
                    else {
                        continue;
                    }

                    byte[] receiveData = new byte[receiveDataList.size()];

                    int receiveDataLength = receiveDataList.size();

                    for (int i = 0; i < receiveDataLength; i++) {
                        receiveData[i] = receiveDataList.get(i);
                    }

                    boolean receiveResponse = false;

                    if (2 <= receiveDataLength) {
                        for (int i = 0; i < receiveDataLength - 1; i++) {
                            if (receiveData[i]     == 0x0a &&       // Check the footer of the command.
                                receiveData[i + 1] == 0x00) {
                                for (int j = 0; j < receiveDataLength - 9; j++) {
                                    if (receiveData[j]     == 0x1b &&     // Check the header of the command.
                                        receiveData[j + 1] == 0x1d &&
                                        receiveData[j + 2] == 0x29 &&
                                        receiveData[j + 3] == 0x49 &&
                                        receiveData[j + 4] == 0x01 &&
                                        receiveData[j + 5] == 0x00 &&
                                        receiveData[j + 6] == 49) {
                                        for (int k = j + 7; k < receiveDataLength - 2; k++) {
                                            String text = String.format("%c", receiveData[k]);

                                            information += text;
                                        }

                                        receiveResponse = true;
                                        break;
                                    }
                                }

                                break;
                            }
                        }
                    }

                    if (receiveResponse) {
                        break;
                    }
                }

                int startIndex = information.indexOf("PrSrN=");

                if (startIndex != -1) {
                    String temp = information.substring(startIndex);

                    int endIndex = temp.indexOf(",");

                    if (endIndex == -1) {
                        endIndex = temp.length();
                    }

                    serialNumber = temp.substring("PrSrN=".length(), endIndex);
                    communicateResult = Communication.Result.Success;
                }
            } catch (StarIOPortException e) {
                // Nothing
            }

            if (mPort != null && mPortName != null) {
                try {
                    StarIOPort.releasePort(mPort);
                } catch (StarIOPortException e) {
                    // Nothing
                }
                mPort = null;
            }

            resultSendCallback(communicateResult, serialNumber, mCallback);
        }
    }

    private static void resultSendCallback(final Communication.Result communicateResult, final String serialNumber, final Communication.SerialNumberCallback callback) {
        if (callback != null) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onSerialNumber(communicateResult, serialNumber);
                }
            });
        }
    }
}

class SetUsbSerialNumberThread extends Thread {
    private final Object mLock;
    private Communication.SendCallback mCallback;

    private StarIOPort mPort;

    private byte[]  mSerialNumber;
    private boolean mIsEnabled;
    private String mPortName = null;
    private String mPortSettings;
    private int     mTimeout;
    private Context mContext;

    SetUsbSerialNumberThread(Object lock, byte[] serialNumber, boolean isEnabled, String portName, String portSettings, int timeout, Context context, Communication.SendCallback callback) {
        mSerialNumber = serialNumber;
        mIsEnabled    = isEnabled;
        mPortName     = portName;
        mPortSettings = portSettings;
        mTimeout      = timeout;
        mContext      = context;
        mCallback     = callback;
        mLock         = lock;
    }

    @Override
    public void run() {
        Communication.Result communicateResult = Communication.Result.ErrorOpenPort;
        boolean result = false;

        synchronized (mLock) {
            try {
                if (mPort == null) {
                    if (mPortName == null) {
                        resultSendCallback(false, communicateResult, mCallback);
                        return;
                    } else {
                        mPort = StarIOPort.getPort(mPortName, mPortSettings, mTimeout, mContext);
                    }
                }
                if (mPort == null) {
                    resultSendCallback(false, communicateResult, mCallback);
                    return;
                }

                communicateResult = Communication.Result.ErrorWritePort;

                StarPrinterStatus status = mPort.retreiveStatus();

                if (status.offline) {
                    throw new StarIOPortException("A printer is offline.");
                }

                if (mSerialNumber.length != 0) {
                    int usbSerialNumberDigit = 0;

                    if (mSerialNumber.length <= 8) {
                        usbSerialNumberDigit = 8;
                    } else if (mSerialNumber.length <= 16) {
                        usbSerialNumberDigit = 16;
                    }

                    // Create set USB serial number command.
                    byte[] setCommand = new byte[6 + usbSerialNumberDigit + 2];     // Command Header(6byte) + USB Serial Number(8 or 16byte) + Command Footer(2byte)

                    setCommand[0] = 0x1b;
                    setCommand[1] = 0x23;
                    setCommand[2] = 0x23;
                    setCommand[3] = 0x57;

                    int fillCounts = 0;

                    if (mSerialNumber.length <= 8) {
                        setCommand[4] = 0x38;
                        fillCounts = 8 - mSerialNumber.length;
                    } else if (mSerialNumber.length <= 16) {
                        setCommand[4] = 0x10;
                        fillCounts = 16 - mSerialNumber.length;
                    }

                    setCommand[5] = 0x2c;

                    int index = 6;

                    for (int i = 0; i < fillCounts; i++) {  // Fill in the top at '0' to be a total 8 or 16 digit.
                        setCommand[index + i] = 0x30;
                    }

                    index += fillCounts;

                    for (int i = 0; i < mSerialNumber.length; i++) {
                        setCommand[index + i] = mSerialNumber[i];
                    }

                    index += mSerialNumber.length;

                    setCommand[index]     = 0x0a;
                    setCommand[index + 1] = 0x00;

                    mPort.writePort(setCommand, 0, setCommand.length);

                    // Wait for 5 seconds until printer recover from software reset.
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        // Nothing
                    }
                }

                // Create set USB serial number is enabled command.
                if (mIsEnabled) {
                    byte[] enableCommand = new byte[] {
                            0x1b, 0x1d, 0x23, 0x2b, 0x43, 0x30, 0x30, 0x30, 0x32, 0x0a, 0x00,
                            0x1b, 0x1d, 0x23, 0x57, 0x30, 0x30, 0x30, 0x30, 0x30, 0x0a, 0x00 }; // Enable USB-ID by setting the bit1 of memory switch C to 1.

                    mPort.writePort(enableCommand, 0, enableCommand.length);
                } else {
                    byte[] disableCommand = new byte[] {
                            0x1b, 0x1d, 0x23, 0x2d, 0x43, 0x30, 0x30, 0x30, 0x32, 0x0a, 0x00,
                            0x1b, 0x1d, 0x23, 0x57, 0x30, 0x30, 0x30, 0x30, 0x30, 0x0a, 0x00 }; // Disable USB-ID by setting the bit1 of memory switch C to 0.

                    mPort.writePort(disableCommand, 0, disableCommand.length);
                }

                communicateResult = Communication.Result.Success;
                result = true;
            } catch (StarIOPortException e) {
                // Nothing
            }

            if (mPort != null && mPortName != null) {
                try {
                    StarIOPort.releasePort(mPort);
                } catch (StarIOPortException e) {
                    // Nothing
                }
                mPort = null;
            }

            resultSendCallback(result, communicateResult, mCallback);
        }
    }

    private static void resultSendCallback(final boolean result, final Communication.Result communicateResult, final Communication.SendCallback callback) {
        if (callback != null) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onStatus(result, communicateResult);
                }
            });
        }
    }
}

class InitializeUsbSerialNumberThread extends Thread {
    private final Object mLock;
    private Communication.SendCallback mCallback;

    private StarIOPort mPort;

    private boolean mIsEnabled;
    private String mPortName = null;
    private String mPortSettings;
    private int     mTimeout;
    private Context mContext;

    InitializeUsbSerialNumberThread(Object lock, boolean isEnabled, String portName, String portSettings, int timeout, Context context, Communication.SendCallback callback) {
        mIsEnabled    = isEnabled;
        mPortName     = portName;
        mPortSettings = portSettings;
        mTimeout      = timeout;
        mContext      = context;
        mCallback     = callback;
        mLock         = lock;
    }

    @Override
    public void run() {
        Communication.Result communicateResult = Communication.Result.ErrorOpenPort;
        boolean result = false;

        synchronized (mLock) {
            try {
                if (mPort == null) {
                    if (mPortName == null) {
                        resultSendCallback(false, communicateResult, mCallback);
                        return;
                    } else {
                        mPort = StarIOPort.getPort(mPortName, mPortSettings, mTimeout, mContext);
                    }
                }
                if (mPort == null) {
                    resultSendCallback(false, communicateResult, mCallback);
                    return;
                }

                communicateResult = Communication.Result.ErrorWritePort;

                StarPrinterStatus status = mPort.retreiveStatus();

                if (status.offline) {
                    throw new StarIOPortException("A printer is offline.");
                }

                // Create initialize USB serial number command.
                byte[] initializeCommand = new byte[]{ 0x1b, 0x23, 0x23, 0x57, 0x38, 0x2c, '?', '?', '?', '?', '?', '?', '?', '?', 0x0a, 0x00 };

                mPort.writePort(initializeCommand, 0, initializeCommand.length);

                // Wait for 5 seconds until printer recover from software reset.
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    // do nothing
                }

                // Create set USB serial number is enabled command.
                byte[] setEnableCommand;

                if (mIsEnabled) {
                    setEnableCommand = new byte[] {
                            0x1b, 0x1d, 0x23, 0x2b, 0x43, 0x30, 0x30, 0x30, 0x32, 0x0a, 0x00,
                            0x1b, 0x1d, 0x23, 0x57, 0x30, 0x30, 0x30, 0x30, 0x30, 0x0a, 0x00 }; // Enable
                } else {
                    setEnableCommand = new byte[] {
                            0x1b, 0x1d, 0x23, 0x2d, 0x43, 0x30, 0x30, 0x30, 0x32, 0x0a, 0x00,
                            0x1b, 0x1d, 0x23, 0x57, 0x30, 0x30, 0x30, 0x30, 0x30, 0x0a, 0x00 }; // Disable
                }

                mPort.writePort(setEnableCommand, 0, setEnableCommand.length);

                communicateResult = Communication.Result.Success;
                result = true;
            } catch (StarIOPortException e) {
                // Nothing
            }

            if (mPort != null && mPortName != null) {
                try {
                    StarIOPort.releasePort(mPort);
                } catch (StarIOPortException e) {
                    // Nothing
                }
                mPort = null;
            }

            resultSendCallback(result, communicateResult, mCallback);
        }
    }

    private static void resultSendCallback(final boolean result, final Communication.Result communicateResult, final Communication.SendCallback callback) {
        if (callback != null) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onStatus(result, communicateResult);
                }
            });
        }
    }
}
