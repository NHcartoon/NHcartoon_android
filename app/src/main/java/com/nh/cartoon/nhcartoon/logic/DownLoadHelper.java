package com.nh.cartoon.nhcartoon.logic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;

import com.nh.cartoon.nhcartoon.bean.NHBook;
import com.nh.cartoon.nhcartoon.database.BookDBHelper;
import com.nh.cartoon.nhcartoon.service.DownLoadService;
import com.nh.cartoon.nhcartoon.utils.Constant;
import com.nh.cartoon.nhcartoon.utils.Logger;

import java.io.File;


public class DownLoadHelper {

    private static final String TAG = "DownLoadHelper";

    private static File ALBUM_STORAGE_DIR;

    public static File getAlbumStorageDir() {
        if (ALBUM_STORAGE_DIR != null) {
            return ALBUM_STORAGE_DIR;
        }
        ALBUM_STORAGE_DIR = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        Logger.d(TAG, "getAlbumStorageDir -> " + ALBUM_STORAGE_DIR.getAbsolutePath());
        return ALBUM_STORAGE_DIR;
    }

    public static void doDownLoad(Activity context, NHBook nhBook) {
        //String path = DownLoadHelper.getAlbumStorageDir().getAbsolutePath();
        //TestUrl = https://i.nhentai.net/galleries/1356021/1.jpg
        //String imgPath = StringUtils.replaceParam(path + BASIC_PATH, "1356021", "1", "jpg");

        //DownLoadHelper.downLoadFile(this, "https://i.nhentai.net/galleries/1356021/1.jpg", imgPath);
        BookDBHelper.getInstance(context).updateNHBookDLState(nhBook.getId(), Constant.DOWNLOADING);

        Intent intent = new Intent(context, DownLoadService.class);
        intent.putExtra("id", nhBook.getId());
        context.startService(intent);
    }

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"};


    public static void verifyStoragePermissions(Activity activity) {

        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
