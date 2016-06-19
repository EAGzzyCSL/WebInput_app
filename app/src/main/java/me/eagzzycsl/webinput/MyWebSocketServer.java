package me.eagzzycsl.webinput;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Base64;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public class MyWebSocketServer {
    private static final String magicString = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";

    private String calSecWebSocketAccept(String secWebSocketKey) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
            messageDigest.reset();
            String src = secWebSocketKey + magicString;
            /*编码问题，不过只有ascii码字符似乎不需要考虑*/
            messageDigest.update(src.getBytes());
            byte[] bytes = messageDigest.digest();
            return Base64.encodeToString(bytes, Base64.NO_WRAP);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }


    private MySocketServer mySocketServer;
    private boolean shakeHand = true;

    public MyWebSocketServer(int port, final OnSocketListener onSocketListener, final Handler handler) {
        mySocketServer = new MySocketServer(port) {


            @Override
            public void onConnected() {
                onSocketListener.onConnected();
                handler.sendEmptyMessage(HandleWhat._connected);
            }

            @Override
            public void onDisConnected() {
                onSocketListener.onDisConnected();
                handler.sendEmptyMessage(HandleWhat._disconnected);
            }


            @Override
            public void onReceive(byte[] buffer, int readBytes) {
                if (shakeHand) {
                    send(
                            generateResponse(
                                    myHttpHeadParse(buffer, readBytes)
                            ).getBytes()
                    );
                    shakeHand = false;
                } else {
                    String s = myWebSocketFrameParse(buffer, readBytes);
                    if (s != null) {
                        onSocketListener.onReceive(s);
                        System.out.println(s);
                    } else {
                        nowConnectionClose();

                    }
                }
            }
        };
    }

    private HashMap<String, String> myHttpHeadParse(byte[] buffer, int readBytes) {
        HashMap<String, String> r = new HashMap<>();
        String head = new String(buffer, 0, readBytes);
        String[] one = head.split("\r\n");
        for (String o : one) {
            String[] kv = o.split(": ");
            if (kv.length > 1) {
                r.put(kv[0], kv[1]);
            }
        }
        return r;
    }

    private String generateResponse(HashMap<String, String> httpHead) {
        String sec = httpHead.get("Sec-WebSocket-Key");
        return TextUtils.join("\r\n", new String[]{
                "HTTP/1.1 101 Switching Protocols",
                "Upgrade: websocket",
                "Connection: Upgrade",
                "Sec-WebSocket-Accept: " + calSecWebSocketAccept(sec),
                "\r\n"
        });
    }

    private String myWebSocketFrameParse(byte[] buffer, int readBytes) {
        /*fin:finish*/
        byte fin = (byte) ((buffer[0] & 0x80) >>> 7);
        /*opCode*/
        byte opCode = (byte) (buffer[0] & 0xf);
        if (opCode == 0x8) {
            return null;
        } else if (opCode == 0x1) {
            byte hasMask = (byte) ((buffer[1] & 0x80) >>> 7);
            byte length = (byte) (buffer[1] & 0x7F);
            int dataStart = 2;

            /*about length*/
            if (length == 126) {
                dataStart += 2;
            } else if (length == 127) {
                dataStart += 4;
            }
            /*mask and data pack*/
            if (hasMask == 1) {
                byte[] mask = new byte[]{
                        buffer[dataStart++],
                        buffer[dataStart++],
                        buffer[dataStart++],
                        buffer[dataStart++],
                };
                for (int i = 0; i < length; i++) {
                    buffer[dataStart + i] ^= mask[i % 4];
                }
            }
            return new String(buffer, dataStart, length, Charset.forName("utf-8"));
        } else {
            /*未处理情况*/
            return null;
        }

    }

    public void close() {
        mySocketServer.nowConnectionClose();
    }
}