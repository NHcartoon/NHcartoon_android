package com.nh.cartoon.nhcartoon.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nh.cartoon.nhcartoon.R;
import com.nh.cartoon.nhcartoon.bean.NHBook;
import com.nh.cartoon.nhcartoon.logic.ActivityJumpHelper;
import com.nh.cartoon.nhcartoon.logic.BookDataHelper;
import com.nh.cartoon.nhcartoon.logic.DownLoadHelper;
import com.nh.cartoon.nhcartoon.ui.adapter.BooksAdapter;
import com.nh.cartoon.nhcartoon.utils.BasicUtils;
import com.nh.cartoon.nhcartoon.utils.Logger;
import com.nh.cartoon.nhcartoon.utils.StringUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import java.util.List;

import static com.nh.cartoon.nhcartoon.utils.Constant.TAG_URL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";

    private ListView mListView;

    private BooksAdapter mBooksAdapter;

    private BookDataHelper mBookDataHelper;

    private DrawerLayout mDrawerLayout;

    private TextView mBackView;
    private TextView mNextView;
    private TextView mSearchView;
    private TextView mStartSearch;
    private TextView mMainTitle;
    private EditText mSearchEdit;

    private View mListFootView;
    private View mSearchPanel;

    private boolean mHaveMore = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);//remove title bar  即隐藏标题栏
        getSupportActionBar().hide();// 隐藏ActionBar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//remove notification bar  即全屏

        setContentView(R.layout.activity_main);
        mBookDataHelper = BookDataHelper.getInstance(this);
        mBookDataHelper.setBookListLoadListener(new BookDataHelper.BookListLoadListener() {
            @Override
            public void doBookLoadComplete(final List<NHBook> NHBookList) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mBooksAdapter.addBooks(NHBookList);
                        mListView.removeFooterView(mListFootView);
                        if (mHaveMore) {
                            mListView.addFooterView(mListFootView);
                        }
                    }
                });

            }
        });

        mBookDataHelper.getBookDataFromHome();

        initView();

        initMenuView();

        DownLoadHelper.verifyStoragePermissions(this);

    }

    private void initView() {
        mListView = findViewById(R.id.books_list_view);
        mBackView = findViewById(R.id.back_view);
        mDrawerLayout = findViewById(R.id.id_drawerlayout);
        mNextView = findViewById(R.id.next_view);
        mSearchView = findViewById(R.id.search_btn);
        mSearchPanel = findViewById(R.id.search_panel);
        mSearchEdit = findViewById(R.id.search_edit);
        mStartSearch = findViewById(R.id.start_search);
        mMainTitle = findViewById(R.id.main_title);
        showMainTile("主页");


        mBooksAdapter = new BooksAdapter(this, null);
        mListView.setAdapter(mBooksAdapter);
        mListView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), false, true));
        mBackView.setBackgroundResource(R.mipmap.menu_w);
        mBackView.setOnClickListener(this);
        mNextView.setOnClickListener(this);
        mSearchView.setOnClickListener(this);
        mStartSearch.setOnClickListener(this);

        mDrawerLayout.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                mBackView.setBackgroundResource(R.mipmap.menu_w);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                mBackView.setBackgroundResource(R.mipmap.left_w);
            }
        });

        mListFootView = LayoutInflater.from(this).inflate(R.layout.main_list_foot_view, null);
        Button loadMoreBtn = mListFootView.findViewById(R.id.load_more);
        loadMoreBtn.setOnClickListener(this);


        mSearchEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    BasicUtils.hideInput(MainActivity.this, view);
                }
            }
        });
    }

    private void initMenuView() {
        //home
        View homeView = findViewById(R.id.menu_home_btn);
        View loveView = findViewById(R.id.menu_love_btn);
        View downloadView = findViewById(R.id.menu_download_btn);
        View historyView = findViewById(R.id.menu_history_btn);
        View SettingView = findViewById(R.id.menu_setting_btn);
        homeView.setOnClickListener(this);
        loveView.setOnClickListener(this);
        downloadView.setOnClickListener(this);
        historyView.setOnClickListener(this);
        SettingView.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // RESULT_OK，判断另外一个activity已经结束数据输入功能，Standard activity result:
        // operation succeeded. 默认值是-1
        if (resultCode == 2) {
            if (requestCode == ActivityJumpHelper.REQUEST_CODE_JUMP_URL) {
                String url = data.getStringExtra("show_url");
                String showTitle = data.getStringExtra("show_title");
                Logger.d(TAG, "onActivityResult -> URL :" + url);
                mBookDataHelper.getBookWithUrl(url);
                showMainTile(showTitle);
                mBooksAdapter.clearBooks();
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_btn:
                Logger.d(TAG, " search btn ");
                if (mSearchPanel.getVisibility() == View.GONE) {
                    mSearchPanel.setVisibility(View.VISIBLE);
                } else {
                    mSearchPanel.setVisibility(View.GONE);
                }
                break;
            case R.id.start_search:
                Logger.d(TAG, " start search ");
                String searchStr = mSearchEdit.getText().toString();
                Toast.makeText(this, "search:" + searchStr, Toast.LENGTH_SHORT).show();
                mBookDataHelper.getBooksForSearch(searchStr);
                showMainTile("search:" + searchStr);
                mSearchPanel.setVisibility(View.GONE);
                mSearchEdit.setText("");
                BasicUtils.hideInput(MainActivity.this, mSearchEdit);
                break;
            case R.id.next_view:
                mBookDataHelper.getQuickNext();
                mListView.setSelection(0);
                break;
            case R.id.back_view:
                if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                    mBackView.setBackgroundResource(R.mipmap.menu_w);
                } else {
                    mDrawerLayout.openDrawer(GravityCompat.START);
                    mBackView.setBackgroundResource(R.mipmap.left_w);
                }
                break;
            case R.id.load_more:
                mBookDataHelper.getNextPage();
                break;
            case R.id.menu_home_btn:
                mHaveMore = true;
                mBookDataHelper.getBookDataFromHome();
                mDrawerLayout.closeDrawer(GravityCompat.START);
                showMainTile(this.getResources().getString(R.string.menu_home));
                break;
            case R.id.menu_love_btn:
                mHaveMore = false;
                mBookDataHelper.getBookDataFromLove();
                mDrawerLayout.closeDrawer(GravityCompat.START);
                showMainTile(this.getResources().getString(R.string.menu_love));
                break;
            case R.id.menu_download_btn:
                ActivityJumpHelper.goDownLoadActivity(this);
                Toast.makeText(this, "download", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu_history_btn:
                mHaveMore = false;
                mBookDataHelper.getBookDataFromHistory();
                mDrawerLayout.closeDrawer(GravityCompat.START);
                showMainTile(this.getResources().getString(R.string.menu_history));
                break;
            case R.id.menu_setting_btn:
                Toast.makeText(this, "setting", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void showMainTile(String title) {
        mMainTitle.setText(title);
    }
}

