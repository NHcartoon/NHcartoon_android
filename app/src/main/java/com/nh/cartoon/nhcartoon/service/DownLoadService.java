package com.nh.cartoon.nhcartoon.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;

import com.arialyy.annotations.Download;
import com.arialyy.aria.core.Aria;
import com.arialyy.aria.core.download.DownloadTask;
import com.nh.cartoon.nhcartoon.bean.DownloadImg;
import com.nh.cartoon.nhcartoon.bean.NHBook;
import com.nh.cartoon.nhcartoon.database.BookDBHelper;
import com.nh.cartoon.nhcartoon.logic.DownLoadHelper;
import com.nh.cartoon.nhcartoon.utils.Constant;
import com.nh.cartoon.nhcartoon.utils.Logger;
import com.nh.cartoon.nhcartoon.utils.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.nh.cartoon.nhcartoon.utils.Constant.DETAIL_LIST_IMG_URL;
import static com.nh.cartoon.nhcartoon.utils.Constant.DOWNLOADING;
import static com.nh.cartoon.nhcartoon.utils.Constant.DOWNLOAD_BOOK_PATH;
import static com.nh.cartoon.nhcartoon.utils.Constant.DOWNLOAD_COMPLETE;
import static com.nh.cartoon.nhcartoon.utils.Constant.DOWNLOAD_IMG_PATH;

public class DownLoadService extends Service {
    public static final String TAG = "DownLoadService";

    public Map<String, DownloadImg> downloadImgMap = new HashMap<>();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.d(TAG, "in onCreate");
        Aria.get(this).getDownloadConfig().setConvertSpeed(true);
        Aria.download(this).register();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.d(TAG, "in onStartCommand");
        Logger.d(TAG, "MyService:" + this);
        String id = intent.getStringExtra("id");
        Logger.d(TAG, "id:" + id);

        NHBook book = BookDBHelper.getInstance(this).queryNHBookById(id);

        String rootPath = StringUtils.replaceParam(DownLoadHelper.getAlbumStorageDir() + DOWNLOAD_BOOK_PATH, book.getId());
        File rootFile = new File(rootPath);
        if (!rootFile.exists()) {
            rootFile.mkdirs();
        }
        List<DownloadImg> imgs = new ArrayList<>();

        for (int i = 1; i <= book.getPageNumber(); i++) {
            DownloadImg img = new DownloadImg();
            img.setBookId(book.getId());
            img.setImgId(book.getId());
            img.setImgIndex(i);
            img.setImgType(book.getImgType());
            img.setImgState(DOWNLOADING);

            File imgFile = new File(img.getImgPath());
            img.setImgState(imgFile.exists() ? DOWNLOAD_COMPLETE : DOWNLOADING);
            BookDBHelper.getInstance(this).insertDownloadTag(book, i);
            imgs.add(img);
        }

        for (DownloadImg img : imgs) {
            if (img.getImgState() == DOWNLOAD_COMPLETE) {
                BookDBHelper.getInstance(this).updateDownLoadImgState(img.getBookId(), img.getImgIndex(), DOWNLOAD_COMPLETE);
                continue;
            }
            downloadImgMap.put(img.getImgUrl(), img);
            Aria.download(this)
                    .load(img.getImgUrl())     //读取下载地址
                    .setDownloadPath(img.getImgPath()) //设置文件保存的完整路径
                    .start();   //启动下载
        }

        if (downloadImgMap.size() == 0) {
            stopSelf();
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Logger.d(TAG, "in onDestroy");
    }

    //在这里处理任务执行中的状态，如进度进度条的刷新
    @Download.onTaskRunning
    protected void running(DownloadTask task) {

        int p = task.getPercent();    //任务进度百分比
        String speed = task.getConvertSpeed();    //转换单位后的下载速度，单位转换需要在配置文件中打开
        long longSpeed = task.getSpeed(); //原始byte长度速度
        Logger.d(TAG, "[DownLoadHelper]task.getKey():" + task.getKey() + ",p: " + p + ",speed:" + speed + ", longSpeed: " + longSpeed + " ");
    }

    @Download.onTaskComplete
    void taskComplete(DownloadTask task) {
        //在这里处理任务完成的状态
        DownloadImg img = downloadImgMap.remove(task.getKey());
        if (img != null) {
            BookDBHelper.getInstance(this).updateDownLoadImgState(img.getBookId(), img.getImgIndex(), DOWNLOAD_COMPLETE);
        }
        Logger.d(TAG, "[DownLoadHelper]taskComplete():" + task.getKey() + ",size -> " + downloadImgMap.size());

        if (downloadImgMap.size() == 0) {
            stopSelf();
        }
    }

    @Download.onTaskFail
    void taskFile(DownloadTask task) {
        //String url = task
        //DownloadImg img = downloadImgMap.get(task.getKey());

        //if (TextUtils.equals(img.getImgType(), "jpg")) {
        //    img.setImgType("png");
        //} else {
        //    img.setImgType("jpg");
        //}

        Logger.d(TAG, "[DownLoadHelper]taskFile():" + task.getKey() + ",size -> " + downloadImgMap.size());
    }

}
