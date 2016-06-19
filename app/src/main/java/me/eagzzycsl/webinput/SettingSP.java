package me.eagzzycsl.webinput;

import android.content.Context;
import android.content.SharedPreferences;

public class SettingSP {
    private final static String P_NAME = "me.eagzzycsl.webinput_preferences";

    private SharedPreferences sp;

    public SettingSP(Context context) {
        this.sp = context.getSharedPreferences(P_NAME, Context.MODE_PRIVATE);
    }

    public int getDefaultPort() {
        return Integer.valueOf(sp.getString("defaultPort", "6666"));
    }

}
