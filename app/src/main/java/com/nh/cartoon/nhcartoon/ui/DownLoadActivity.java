package com.nh.cartoon.nhcartoon.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.nh.cartoon.nhcartoon.R;
import com.nh.cartoon.nhcartoon.ui.fragment.DownloadingFragment;
import com.nh.cartoon.nhcartoon.utils.Logger;

public class DownLoadActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "DownLoadActivity";
    private static final int PANEL_TYPE_DOWNLOADED = 1;
    private static final int PANEL_TYPE_DOWNLOADING = 2;

    private TextView mDownedBtn;
    private TextView mDownIngBtn;

    private View mDownedPanel;
    private TextView mDowningPanel;

    private int mShowPanelType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);//remove title bar  即隐藏标题栏
        getSupportActionBar().hide();// 隐藏ActionBar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//remove notification bar  即全屏

        setContentView(R.layout.down_load_activity);

        initView();

    }

    private void switchPanel(int panelType) {
        if (mShowPanelType == panelType) {
            Logger.d(TAG,"switchPanel return panelType:" + panelType);
            return;
        }

        Logger.d(TAG,"switchPanel panelType:" + panelType);

        mShowPanelType = panelType;
        switch (panelType) {
            case PANEL_TYPE_DOWNLOADED:
                mDownedBtn.setTextColor(this.getResources().getColor(R.color.red));
                mDownIngBtn.setTextColor(this.getResources().getColor(R.color.white));

                mDownedPanel.setVisibility(View.VISIBLE);
                mDowningPanel.setVisibility(View.GONE);
                break;
            case PANEL_TYPE_DOWNLOADING:
                mDownedBtn.setTextColor(this.getResources().getColor(R.color.white));
                mDownIngBtn.setTextColor(this.getResources().getColor(R.color.red));

                mDownedPanel.setVisibility(View.GONE);
                mDowningPanel.setVisibility(View.VISIBLE);
                break;
        }

    }

    private void initView() {
        mDownedBtn = findViewById(R.id.downloaded_btn);
        mDownIngBtn = findViewById(R.id.downloading_btn);

        mDownedPanel = findViewById(R.id.downloaded_panel);
        mDowningPanel = findViewById(R.id.downloading_panel);

        mDownedBtn.setOnClickListener(this);
        mDownIngBtn.setOnClickListener(this);

        switchPanel(PANEL_TYPE_DOWNLOADED);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.downloaded_btn:
                Logger.d(TAG,"onClick downloaded_btn");
                switchPanel(PANEL_TYPE_DOWNLOADED);
                break;
            case R.id.downloading_btn:
                Logger.d(TAG,"onClick downloaded_btn");
                switchPanel(PANEL_TYPE_DOWNLOADING);
                break;
        }
    }


}
