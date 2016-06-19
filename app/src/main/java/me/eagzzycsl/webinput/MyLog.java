package me.eagzzycsl.webinput;


import android.util.Log;


public class MyLog {
    private static boolean _DEBUG = true;
    public static final String msg_inputService = "输入法服务";
    public static final String msg_socketServer = "socket服务器";
    public static final String msg_webSocketServer = "webSocket服务器";

    public static void i(String tag, String msg) {
        if (_DEBUG) {
            Log.i(tag, msg);
        }
    }
}
