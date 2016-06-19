package me.eagzzycsl.webinput;

public interface OnSocketListener {
    void onConnected();

    void onDisConnected();

    void onReceive(String s);
}
