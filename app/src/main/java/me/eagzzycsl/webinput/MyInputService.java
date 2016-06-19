package me.eagzzycsl.webinput;

import android.content.Context;
import android.content.Intent;
import android.inputmethodservice.InputMethodService;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteOrder;

public class MyInputService extends InputMethodService {
    private TextView textView_info;
    private MyWebSocketServer myWebSocketServer;
    private String wsLink = null;
    private String state = null;
    private boolean currentShowIsWsLink = true;
    private SettingSP settingSP;
    private int port;

    private String getIp() {
        WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
        int ipAddress = wm.getConnectionInfo().getIpAddress();
        if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
            ipAddress = Integer.reverseBytes(ipAddress);
        }
        byte[] ipByteArray = BigInteger.valueOf(ipAddress).toByteArray();

        String ipAddressString;
        try {
            ipAddressString = InetAddress.getByAddress(ipByteArray).getHostAddress();
        } catch (UnknownHostException ex) {
            ipAddressString = null;
        }
        return ipAddressString;
    }

    @Override
    public void onInitializeInterface() {
        super.onInitializeInterface();
        MyLog.i(MyLog.msg_inputService, "初始化");
        settingSP = new SettingSP(this);
        state = getString(R.string.waitForConnect);
        port = settingSP.getDefaultPort();
        myWebSocketServer = new MyWebSocketServer(port
                , new OnSocketListener() {
            @Override
            public void onConnected() {
            }

            @Override
            public void onDisConnected() {
            }

            @Override
            public void onReceive(String s) {
                InputConnection inputConnection = getCurrentInputConnection();
                if (inputConnection != null) {
                    getCurrentInputConnection().commitText(s, 0);

                }
            }
        }, new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case HandleWhat._connected: {
                        state = getString(R.string.haveConnected);
                        textView_info.setText(state);
                        currentShowIsWsLink = false;
                        break;
                    }
                    case HandleWhat._disconnected: {
                        state = getString(R.string.connectionInterrupt);
                        textView_info.setText(state);
                        currentShowIsWsLink = false;
                        break;
                    }
                }
            }
        });
    }

    @Override
    public View onCreateInputView() {

        View v = LayoutInflater.from(getApplicationContext()).inflate(
                R.layout.keyboard, null
        );
        textView_info = (TextView) v.findViewById(R.id.textView_info);
        v.findViewById(R.id.imageView_setting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyInputService.this, SettingsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        v.findViewById(R.id.imageView_switch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imeManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imeManager != null) {
                    imeManager.showInputMethodPicker();
                }
            }
        });
        wsLink = getIp() + ":" + port;
        textView_info.setText(wsLink);
        textView_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentShowIsWsLink) {
                    textView_info.setText(state);
                } else {
                    textView_info.setText(wsLink);
                }
                currentShowIsWsLink = !currentShowIsWsLink;
            }
        });
        return v;
    }

    @Override
    public View onCreateCandidatesView() {
        return null;
    }


    @Override
    public boolean onShowInputRequested(int flags, boolean configChange) {
        return super.onShowInputRequested(flags, configChange);
    }

    @Override
    public void onDestroy() {
        MyLog.i(MyLog.msg_inputService, "销毁");
        myWebSocketServer.close();

        super.onDestroy();
    }
}

