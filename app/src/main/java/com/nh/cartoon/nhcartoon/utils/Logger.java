package com.nh.cartoon.nhcartoon.utils;

import android.util.Log;

public class Logger {

    private static final String TARGET = "NH_CARTOON";

    private static final boolean SHOW_LOG = true;

    public static void d(String tag, Object msg) {
        if (!SHOW_LOG) {
            return;
        }
        Log.d(TARGET, "[" + tag + "]   " + msg);
    }

    public static void e(String tag, Object msg) {
        if (!SHOW_LOG) {
            return;
        }
        Log.e(TARGET, "[" + tag + "]   " + msg);
    }


}
