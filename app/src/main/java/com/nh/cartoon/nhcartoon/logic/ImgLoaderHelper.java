package com.nh.cartoon.nhcartoon.logic;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.nh.cartoon.nhcartoon.R;
import com.nh.cartoon.nhcartoon.ui.view.CircleProgressView;
import com.nh.cartoon.nhcartoon.utils.Logger;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;

public class ImgLoaderHelper {

    private static final String TAG = "ImgLoaderHelper";

    private static volatile ImgLoaderHelper singleton;

    public static ImgLoaderHelper getInstance(Context context) {
        if (singleton == null) {
            synchronized (ImgLoaderHelper.class) {
                if (singleton == null) {
                    singleton = new ImgLoaderHelper(context);
                }
            }
        }
        return singleton;
    }

    private Context mContext;
    private DisplayImageOptions mOptions;

    private ImgLoaderHelper(Context context) {

        mContext = context.getApplicationContext();

        // 配置/初始化
        File cacheDir = StorageUtils.getCacheDirectory(mContext);
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(mContext)
                .memoryCacheExtraOptions(1280, 1756) // default = device screen dimensions 缓存最大图片大小
                .diskCacheExtraOptions(1280, 1756, null) // 闪存最大图片大小
                .threadPoolSize(3) // default 最大线程数
                .threadPriority(Thread.NORM_PRIORITY - 2) // default 线程优先级
                .tasksProcessingOrder(QueueProcessingType.FIFO) // default 线程处理队列，先进先出
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024)) // LruMemory
                .memoryCacheSize(2 * 1024 * 1024) // 缓存
                .memoryCacheSizePercentage(13)    // default 缓存比例？
                .diskCache(new UnlimitedDiskCache(cacheDir)) // default 闪存缓存
                .diskCacheSize(50 * 1024 * 1024) // 闪存缓存大小
                .diskCacheFileCount(100) // 闪存缓存图片文件数量
                //.diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator()) // default 文件名
                .imageDownloader(new BaseImageDownloader(mContext)) // default
                .imageDecoder(new BaseImageDecoder(true)) // default
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple()) // default
                .writeDebugLogs() // LOG
                .build();
        ImageLoader.getInstance().init(config);
        // 加载图片
        mOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.loading) // resource or drawable
                .showImageForEmptyUri(R.mipmap.load_fail) // resource or drawable
                .showImageOnFail(R.mipmap.load_fail) // resource or drawable
                .resetViewBeforeLoading(false)  // default
                .delayBeforeLoading(10)
                .cacheInMemory(true) // default
                .cacheOnDisk(true) // default
                //.preProcessor(...)
                //.postProcessor(...)
                //.extraForDownloader(...)
                //.considerExifParams(false) // default
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default
                .bitmapConfig(Bitmap.Config.ARGB_8888) // default
                //.decodingOptions(...)
                .displayer(new SimpleBitmapDisplayer()) // default
                .handler(new Handler()) // default
                .build();
        //ImageLoader.getInstance().displayImage(imageUrl, imageView);
        //ImageLoader.getInstance().displayImage(imageUrl, imageView，options);
        //ImageLoader.getInstance().displayImage(imageUrl, imageView, options, listener);
        /*new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {
            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {
            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
            }

            @Override
            public void onLoadingCancelled(String s, View view) {
            }
        })
        */
    }

    public void loadImageWithProgress(String url, final ImageView imgView, final CircleProgressView progressView) {
        ImageLoader.getInstance().displayImage(url, imgView, mOptions, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                Logger.d(TAG, "加载开始 -> " + imageUri);
                progressView.setVisibility(View.VISIBLE);
                imgView.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                progressView.setVisibility(View.INVISIBLE);
                imgView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                Logger.d(TAG, "加载完成 -> " + imageUri);
                progressView.setVisibility(View.INVISIBLE);
                imgView.setVisibility(View.VISIBLE);
                imgView.setImageBitmap(loadedImage);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                Logger.d(TAG, "加载取消 -> " + imageUri);
                progressView.setVisibility(View.VISIBLE);
                imgView.setVisibility(View.INVISIBLE);
            }
        }, new ImageLoadingProgressListener() {
            @Override
            public void onProgressUpdate(String imageUri, View view, int current, int total) {
                int index = (current * 100 / total);
                Logger.d(TAG, "current = " + current + ", total = " + total + ", index = " + index);
                progressView.setmCurrent(index);
            }
        });
    }

    public void loadImage(String url, ImageView imgView) {
        Logger.d(TAG, "开始加载 -> " + url);
        ImageLoader.getInstance().displayImage(url, imgView, mOptions);
    }

    public void loadImage(String url) {
        ImageLoader.getInstance().loadImage(url, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                Logger.d(TAG, "预加载开始 -> " + imageUri);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                Logger.d(TAG, "预加载完成 -> " + imageUri);
                Toast.makeText(mContext, "预加载完成", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                Logger.d(TAG, "预加载取消 -> " + imageUri);
            }
        });
    }

    public void onDestroy(ImageView imageView) {
        ImageLoader.getInstance().clearMemoryCache();
        ImageLoader.getInstance().cancelDisplayTask(imageView);
    }
}
