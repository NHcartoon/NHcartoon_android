package com.nh.cartoon.nhcartoon.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;

import com.nh.cartoon.nhcartoon.R;
import com.nh.cartoon.nhcartoon.bean.NHBook;
import com.nh.cartoon.nhcartoon.logic.BookDataHelper;
import com.nh.cartoon.nhcartoon.ui.adapter.ImagePagerAdapter;
import com.nh.cartoon.nhcartoon.utils.Logger;

public class ImageShowActivity extends AppCompatActivity {

    private static final String TAG = "ImageShowActivity";

    private ViewPager mViewPager;

    private NHBook mNHBook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);//remove title bar  即隐藏标题栏
        getSupportActionBar().hide();// 隐藏ActionBar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//remove notification bar  即全屏

        setContentView(R.layout.image_show_activity);

        Intent intent = getIntent();

        String id = intent.getStringExtra("id");
        int index = intent.getIntExtra("index", 1);

        if (TextUtils.isEmpty(id)) {
            Logger.e(TAG, "id is empty!!");
            finish();
        }

        mNHBook = BookDataHelper.getInstance(this).getBookDetailFromDB(id);
        Logger.d(TAG, mNHBook);

        mViewPager = findViewById(R.id.image_show_view_pager);
        mViewPager.setAdapter(new ImagePagerAdapter(this, mNHBook, mViewPager));

        mViewPager.setCurrentItem(index - 1);
        mViewPager.setOffscreenPageLimit(3);
    }

}
