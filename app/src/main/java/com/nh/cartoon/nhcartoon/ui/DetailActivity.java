package com.nh.cartoon.nhcartoon.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.Toast;

import com.nh.cartoon.nhcartoon.R;
import com.nh.cartoon.nhcartoon.bean.NHBook;
import com.nh.cartoon.nhcartoon.logic.BookDataHelper;
import com.nh.cartoon.nhcartoon.ui.adapter.DetailListAdapter;
import com.nh.cartoon.nhcartoon.utils.Logger;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

public class DetailActivity extends AppCompatActivity implements BookDataHelper.DetailLoadListener {

    private static final String TAG = "DetailActivity";

    private BookDataHelper mBookDataHelper;

    private ListView mDetailListView;

    private DetailListAdapter mAdapter;

    private View mBackView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);//remove title bar  即隐藏标题栏
        getSupportActionBar().hide();// 隐藏ActionBar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//remove notification bar  即全屏


        setContentView(R.layout.detail_activity);

        Intent intent = getIntent();
        if (intent == null) {
            Logger.e(TAG, "intent is empty");
            finish();
            return;
        }

        String id = intent.getStringExtra("id");
        if (TextUtils.isEmpty(id)) {
            Logger.e(TAG, "id is empty");
            finish();
            return;
        }
        Logger.e(TAG, "id ->" + id);
        Toast.makeText(this, "id -> " + id, Toast.LENGTH_SHORT).show();

        mDetailListView = findViewById(R.id.detail_list);
        mDetailListView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), false, true));

        mBookDataHelper = BookDataHelper.getInstance(this);
        mBookDataHelper.setDetailLoadListener(this);
        mBookDataHelper.updateBookDetailFromWeb(id);

        mBackView = findViewById(R.id.back_view);
        mBackView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void updateDetailUI(final NHBook nhBook) {
        Logger.e(TAG, "updateDetailUI");
        mAdapter = new DetailListAdapter(this, nhBook);
        mDetailListView.setAdapter(mAdapter);
    }

    @Override
    public void doDetailLoadComplete(final NHBook nhBook) {
        Logger.d(TAG, "doDetailLoadComplete");
        Logger.d(TAG, nhBook);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateDetailUI(nhBook);
            }
        });
    }

    @Override
    public void onFailure() {
        Logger.d(TAG, "onFailure");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBookDataHelper.setDetailLoadListener(null);
        if (mAdapter != null) {
            mAdapter.onDestory();
        }
    }

    public void onBackMainActivity(String url) {
        Intent intent = new Intent();
        intent.putExtra("show_url", url);
        setResult(2, intent);
        finish();
    }
}
