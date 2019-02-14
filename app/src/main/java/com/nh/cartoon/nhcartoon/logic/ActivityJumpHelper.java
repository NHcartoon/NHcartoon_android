package com.nh.cartoon.nhcartoon.logic;

import android.app.Activity;
import android.content.Intent;

import com.nh.cartoon.nhcartoon.ui.DetailActivity;
import com.nh.cartoon.nhcartoon.ui.DownLoadActivity;
import com.nh.cartoon.nhcartoon.ui.ImageShowActivity;

public class ActivityJumpHelper {

    public static final int REQUEST_CODE_JUMP_URL = 0x001;

    public static final int REQUEST_CODE_SHOW_INDEX = 0x002;

    public static void goDetailActivity(Activity context, String id) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra("id", id);
        context.startActivityForResult(intent, REQUEST_CODE_JUMP_URL);
    }


    public static void goImageShowActivity(Activity context, String id, int index) {
        Intent intent = new Intent(context, ImageShowActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("index", index);
        context.startActivityForResult(intent, REQUEST_CODE_SHOW_INDEX);
    }

    public static void goDownLoadActivity(Activity context) {
        Intent intent = new Intent(context, DownLoadActivity.class);
        context.startActivity(intent);
    }

}
