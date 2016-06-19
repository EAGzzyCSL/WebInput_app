package me.eagzzycsl.webinput;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public abstract class MySocketServer {
    private ServerSocket server;
    private Socket client;
    private InputStream is;
    private OutputStream os;
    private byte buffer[] = new byte[8192];
    private Runnable listenConnectRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                client = server.accept();
                onConnected();
                startListenMsg();
                MyLog.i(MyLog.msg_socketServer, "客户端已连接");

            } catch (IOException e) {
                e.printStackTrace();
            }
            MyLog.i(MyLog.msg_socketServer, "连接线程已结束");
        }
    };
    private Runnable listenMsgRunnable = new Runnable() {
        @Override
        public void run() {
            while (true) {
                int readBytes;
                try {
                    readBytes = is.read(buffer);
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
                if (readBytes != -1) {
                    MyLog.i(MyLog.msg_socketServer, "读到的长度：" + readBytes);
                    onReceive(buffer, readBytes);
                } else {
                    nowConnectionClose();
                    break;
                }
            }
        }
    };


    public MySocketServer(int port) {
        try {
            this.server = new ServerSocket(port);
            new Thread(listenConnectRunnable).start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void startListenMsg() {
        try {
            is = client.getInputStream();
            os = client.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        new Thread(listenMsgRunnable).start();
    }

    public void send(byte[] bytes) {
        try {
            os.write(bytes);
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void nowConnectionClose() {
        onDisConnected();
        try {
            if (client != null) {
                client.close();
            }
            new Thread(listenConnectRunnable).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public abstract void onConnected();

    public abstract void onDisConnected();

    public abstract void onReceive(byte[] buffer, int readBytes);
}
