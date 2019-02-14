package com.nh.cartoon.nhcartoon.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "database.db";
    public static final int DB_VERSION = 1;

    public static final String TABLE_NAME_BOOK = "BOOK";
    public static final String TABLE_NAME_TAG = "TAG";
    public static final String TABLE_NAME_BOOKTAG = "BOOK_TAG";
    public static final String TABLE_NAME_DOWNLOAD = "DOWNLOAD";

    public static final String[] TABLE_BOOK_COL = {
            DBHelper.BOOK_ID,
            DBHelper.BOOK_IMG_ID,
            DBHelper.BOOK_TITLE,
            DBHelper.BOOK_IMG_URL,
            DBHelper.BOOK_LANG_TYPE,
            DBHelper.BOOK_PAGE_NUM,
            DBHelper.BOOK_FAVORITE,
            DBHelper.BOOK_DB_STATE,
            DBHelper.BOOK_UPDATE_TIME,
            DBHelper.BOOK_IMG_TYPE,
            DBHelper.BOOK_DL_STATE
    };

    public static final String BOOK_ID = "_ID";
    public static final String BOOK_IMG_ID = "_IMG_ID";
    public static final String BOOK_TITLE = "_TITLE";
    public static final String BOOK_IMG_URL = "_IMG_URL";
    public static final String BOOK_LANG_TYPE = "_LANG_TYPE";
    public static final String BOOK_PAGE_NUM = "_PAGE_NUM";
    public static final String BOOK_FAVORITE = "_FAVORITE";
    public static final String BOOK_DB_STATE = "_DB_STATE";
    public static final String BOOK_UPDATE_TIME = "_UPDATE_TIME";
    public static final String BOOK_IMG_TYPE = "_IMG_TYPE";
    public static final String BOOK_DL_STATE = "_DL_STATE";

    public static final String TAG_ID = "_ID";
    public static final String TAG_TYPE = "_TYPE";
    public static final String TAG_NAME = "_NAME";
    public static final String TAG_CN_NAME = "_CN_NAME";
    public static final String TAG_URL = "_URL";
    public static final String TAG_COUNT = "_COUNT";

    public static final String BOOKTAG_ID = "_ID";
    public static final String BOOKTAG_BOOK_ID = "_BOOK_ID";
    public static final String BOOKTAG_TAG_ID = "_TAG_ID";

    public static final String DOWNLOAD_ID = "_ID";
    public static final String DOWNLOAD_BOOK_ID = "_BOOK_ID";
    public static final String DOWNLOAD_IMG_ID = "_IMG_ID";
    public static final String DOWNLOAD_IMG_STATE = "_IMG_STATE";
    public static final String DOWNLOAD_IMG_INDEX = "_IMG_INDEX";
    public static final String DOWNLOAD_IMG_TYPE = "_IMG_TYPE";


    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String DOWNLOAD_SQL = "create table " +
                TABLE_NAME_DOWNLOAD +
                "(" +
                DOWNLOAD_ID + " integer primary key autoincrement, " +
                DOWNLOAD_BOOK_ID + " varcher, " +
                DOWNLOAD_IMG_ID + " varcher, " +
                DOWNLOAD_IMG_INDEX + " integer, " +
                DOWNLOAD_IMG_TYPE + " varcher, " +
                DOWNLOAD_IMG_STATE + " integer " +
                ")";
        db.execSQL(DOWNLOAD_SQL);


        String BOOKTAG_SQL = "create table " +
                TABLE_NAME_BOOKTAG +
                "(" +
                BOOKTAG_ID + " integer primary key autoincrement, " +
                BOOKTAG_BOOK_ID + " varcher, " +
                BOOKTAG_TAG_ID + " integer " +
                ")";
        db.execSQL(BOOKTAG_SQL);

        String TAG_SQL = "create table " +
                TABLE_NAME_TAG +
                "(" +
                "id" + " integer primary key autoincrement, " +
                TAG_ID + " integer, " +
                TAG_TYPE + " varcher, " +
                TAG_NAME + " varcher, " +
                TAG_CN_NAME + " varcher, " +
                TAG_URL + " varcher, " +
                TAG_COUNT + " integer" +
                ")";
        db.execSQL(TAG_SQL);

        String BOOK_SQL = "create table " +
                TABLE_NAME_BOOK +
                "(" +
                "id" + " integer primary key autoincrement, " +
                BOOK_ID + " varcher, " +
                BOOK_IMG_ID + " varcher, " +
                BOOK_TITLE + " varcher, " +
                BOOK_IMG_URL + " varcher, " +
                BOOK_LANG_TYPE + " integer, " +
                BOOK_PAGE_NUM + " integer, " +
                BOOK_DB_STATE + " integer, " +
                BOOK_FAVORITE + " integer, " +
                BOOK_UPDATE_TIME + " integer, " +
                BOOK_DL_STATE + " integer, " +
                BOOK_IMG_TYPE + " varcher " +
                ")";
        db.execSQL(BOOK_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
