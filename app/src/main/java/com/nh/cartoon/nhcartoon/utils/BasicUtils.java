package com.nh.cartoon.nhcartoon.utils;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class BasicUtils {

    public static void hideInput(Context context, View view) {
        InputMethodManager manager = ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE));
        if (manager != null)
            manager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

}
