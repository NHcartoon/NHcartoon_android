package com.nh.cartoon.nhcartoon.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.nh.cartoon.nhcartoon.bean.NHBook;
import com.nh.cartoon.nhcartoon.bean.NHBookTag;
import com.nh.cartoon.nhcartoon.logic.FanyiHelper;
import com.nh.cartoon.nhcartoon.utils.Constant;
import com.nh.cartoon.nhcartoon.utils.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.nh.cartoon.nhcartoon.utils.Constant.DOWNLOADING;

public class BookDBHelper {
    private static final String TAG = "BookDBHelper";

    private static volatile BookDBHelper singleton;

    public static BookDBHelper getInstance(Context context) {
        if (singleton == null) {
            synchronized (BookDBHelper.class) {
                if (singleton == null) {
                    singleton = new BookDBHelper(context);
                }
            }
        }
        return singleton;
    }

    private BookDBHelper(Context context) {
        mContext = context.getApplicationContext();
        mHelper = new DBHelper(mContext);
        mDatabase = mHelper.getWritableDatabase();
    }

    private Context mContext;
    private DBHelper mHelper;
    private SQLiteDatabase mDatabase;

    public void insertSimpleBook(NHBook nhBook) {
        Logger.d(TAG, "insertBookSimple -> " + nhBook);
        if (judgeNHBookExist(nhBook) != Constant.DB_STATE_NULL) {
            Logger.d(TAG, "insertBookSimple ->not DB_STATE_NULL return");
            return;
        }

        ContentValues values = new ContentValues();
        values.put(DBHelper.BOOK_ID, nhBook.getId());
        values.put(DBHelper.BOOK_IMG_URL, nhBook.getImgUrl());
        values.put(DBHelper.BOOK_IMG_ID, nhBook.getImgId());
        values.put(DBHelper.BOOK_TITLE, nhBook.getTitle());
        values.put(DBHelper.BOOK_LANG_TYPE, nhBook.getLanguageType());
        values.put(DBHelper.BOOK_DB_STATE, Constant.DB_STATE_SIMPLE);
        values.put(DBHelper.BOOK_IMG_TYPE, nhBook.getImgType());
        mDatabase.insert(DBHelper.TABLE_NAME_BOOK, null, values);
    }

    public void insertBookDetail(NHBook nhBook) {
        Logger.d(TAG, "insertBookDetail -> " + nhBook);
        if (judgeNHBookExist(nhBook) == Constant.DB_STATE_DETAIL) {
            Logger.d(TAG, "insertBookDetail -> DB_STATE_DETAIL return");
            return;
        }

        ContentValues values = new ContentValues();
        values.put(DBHelper.BOOK_IMG_ID, nhBook.getImgId());
        values.put(DBHelper.BOOK_PAGE_NUM, nhBook.getPageNumber());
        values.put(DBHelper.BOOK_FAVORITE, nhBook.getFavorite());
        values.put(DBHelper.BOOK_DB_STATE, Constant.DB_STATE_DETAIL);
        values.put(DBHelper.BOOK_UPDATE_TIME, System.currentTimeMillis());
        mDatabase.update(DBHelper.TABLE_NAME_BOOK, values, DBHelper.BOOK_ID + " = ? ", new String[]{nhBook.getId()});

        for (Map.Entry<String, List<NHBookTag>> entry : nhBook.getTagMap().entrySet()) {
            for (NHBookTag tmpTag : entry.getValue()) {
                if (!judgeTagExist(tmpTag)) {
                    insertTag(tmpTag);
                }
                insertBookTag(nhBook.getId(), tmpTag.getId() + "");
            }
        }
        FanyiHelper.getInstance(mContext).updateTagCNName(nhBook);
        Logger.d(TAG, "insertBookDetail setInDBDetail");
    }

    public void insertDownloadTag(NHBook nhBook, int imgIndex) {
        Logger.d(TAG, "insertDownloadTag -> " + nhBook.getId());
        if (judgeDownloadImgExist(nhBook.getId(), imgIndex)) {
            Logger.d(TAG, "insertDownloadTag -> return ");
            return;
        }
        ContentValues values = new ContentValues();
        values.put(DBHelper.DOWNLOAD_BOOK_ID, nhBook.getId());
        values.put(DBHelper.DOWNLOAD_IMG_ID, nhBook.getImgId());
        values.put(DBHelper.DOWNLOAD_IMG_INDEX, imgIndex);
        values.put(DBHelper.DOWNLOAD_IMG_TYPE, nhBook.getImgType());
        values.put(DBHelper.DOWNLOAD_IMG_STATE, DOWNLOADING);
        mDatabase.insert(DBHelper.TABLE_NAME_DOWNLOAD, null, values);
    }

    public void insertBookTag(String bookId, String tagId) {
        Logger.d(TAG, "insertBookTag -> " + bookId);
        if (judgeBookTagExist(bookId, tagId)) {
            Logger.d(TAG, "insertBookTag -> return ");
            return;
        }
        ContentValues values = new ContentValues();
        values.put(DBHelper.BOOKTAG_TAG_ID, tagId);
        values.put(DBHelper.BOOKTAG_BOOK_ID, bookId);
        mDatabase.insert(DBHelper.TABLE_NAME_BOOKTAG, null, values);
    }

    public void insertTag(NHBookTag nhBookTag) {
        Logger.d(TAG, "insertTag -> " + nhBookTag);
        if (judgeTagExist(nhBookTag)) {
            Logger.d(TAG, "insertTag -> return ");
            return;
        }
        ContentValues values = new ContentValues();
        values.put(DBHelper.TAG_ID, nhBookTag.getId());
        values.put(DBHelper.TAG_NAME, nhBookTag.getName());
        values.put(DBHelper.TAG_COUNT, nhBookTag.getCount());
        values.put(DBHelper.TAG_TYPE, nhBookTag.getType());
        values.put(DBHelper.TAG_URL, nhBookTag.getUrl());
        mDatabase.insert(DBHelper.TABLE_NAME_TAG, null, values);
    }

    public boolean judgeDownloadImgExist(String bookId, int imgIndex) {
        Cursor cursor = mDatabase.query(DBHelper.TABLE_NAME_DOWNLOAD,
                new String[]{
                        DBHelper.DOWNLOAD_ID
                },
                DBHelper.DOWNLOAD_BOOK_ID + " =  ? AND " + DBHelper.DOWNLOAD_IMG_INDEX + " = ? ",
                new String[]{bookId, imgIndex + ""},
                null,
                null,
                null);
        boolean ret = cursor.moveToNext();
        cursor.close();
        return ret;
    }

    public boolean judgeBookTagExist(String bookId, String tagId) {
        Cursor cursor = mDatabase.query(DBHelper.TABLE_NAME_BOOKTAG,
                new String[]{DBHelper.BOOK_ID},
                DBHelper.BOOKTAG_BOOK_ID + " =  ? AND " + DBHelper.BOOKTAG_TAG_ID + " = ? ",
                new String[]{bookId, tagId},
                null,
                null,
                null);
        boolean ret = cursor.moveToNext();
        cursor.close();
        return ret;
    }

    public boolean judgeTagExist(NHBookTag nhBookTag) {
        Cursor cursor = mDatabase.query(DBHelper.TABLE_NAME_TAG,
                new String[]{DBHelper.TAG_ID},
                DBHelper.TAG_ID + " =  ? ",
                new String[]{nhBookTag.getId() + ""},
                null,
                null,
                null);
        boolean ret = cursor.moveToNext();
        cursor.close();
        return ret;
    }

    public int judgeNHBookExist(NHBook nhBook) {
        Cursor cursor = mDatabase.query(DBHelper.TABLE_NAME_BOOK,
                new String[]{
                        DBHelper.BOOK_ID,
                        DBHelper.BOOK_DB_STATE
                },
                DBHelper.BOOK_ID + " =  ? ",
                new String[]{nhBook.getId() + ""},
                null,
                null,
                null);
        if (cursor.moveToNext()) {
            return cursor.getInt(cursor.getColumnIndex(DBHelper.BOOK_DB_STATE));
        }
        return Constant.DB_STATE_NULL;
    }

    public List<NHBook> queryHistoryBooks() {
        Cursor cursor = mDatabase.query(DBHelper.TABLE_NAME_BOOK,
                DBHelper.TABLE_BOOK_COL,
                DBHelper.BOOK_UPDATE_TIME + " > ? ",
                new String[]{"0"},
                null,
                null,
                DBHelper.BOOK_UPDATE_TIME + " DESC ",
                " 50 ");
        List<NHBook> nhBooks = new ArrayList<>();
        NHBook nhBook = null;
        while (cursor.moveToNext()) {
            nhBook = new NHBook();
            updateNHBookWithCursor(cursor, nhBook);
            doFullNHBookTagsFromDB(nhBook);

            nhBooks.add(nhBook);
        }

        return nhBooks;
    }

    public List<NHBook> queryLoveBooks() {
        Cursor cursor = mDatabase.query(DBHelper.TABLE_NAME_BOOK,
                DBHelper.TABLE_BOOK_COL,
                DBHelper.BOOK_FAVORITE + " =  ? ",
                new String[]{Constant.IS_FAVORITE + ""},
                null,
                null,
                null);
        List<NHBook> nhBooks = new ArrayList<>();
        NHBook nhBook = null;
        while (cursor.moveToNext()) {
            nhBook = new NHBook();
            updateNHBookWithCursor(cursor, nhBook);
            doFullNHBookTagsFromDB(nhBook);

            nhBooks.add(nhBook);
        }

        return nhBooks;
    }

    public List<NHBook> queryDownLoadingBooks() {
        Cursor cursor = mDatabase.query(DBHelper.TABLE_NAME_BOOK,
                DBHelper.TABLE_BOOK_COL,
                DBHelper.BOOK_DL_STATE + " =  ? ",
                new String[]{Constant.DOWNLOADING + ""},
                null,
                null,
                null);
        List<NHBook> nhBooks = new ArrayList<>();
        NHBook nhBook = null;
        while (cursor.moveToNext()) {
            nhBook = new NHBook();
            updateNHBookWithCursor(cursor, nhBook);
            doFullNHBookTagsFromDB(nhBook);

            nhBooks.add(nhBook);
        }

        return nhBooks;
    }

    public NHBook queryNHBookById(String nhBookId) {
        Logger.d(TAG, "queryNHBookById -> " + nhBookId);
        Cursor bookCursor = mDatabase.query(DBHelper.TABLE_NAME_BOOK,
                DBHelper.TABLE_BOOK_COL,
                DBHelper.BOOK_ID + " =  ? ",
                new String[]{nhBookId + ""},
                null,
                null,
                null);
        NHBook nhBook = null;
        if (bookCursor.moveToNext()) {
            nhBook = new NHBook();
            updateNHBookWithCursor(bookCursor, nhBook);
        } else {
            Logger.d(TAG, "return null -> ");
            return null;
        }
        bookCursor.close();

        doFullNHBookTagsFromDB(nhBook);
        return nhBook;
    }

    public void updateBookFavorite(String id, int favorite) {
        Logger.d(TAG, "updateBookFavorite -> " + id);
        ContentValues values = new ContentValues();
        values.put(DBHelper.BOOK_FAVORITE, favorite);
        mDatabase.update(DBHelper.TABLE_NAME_BOOK, values, DBHelper.BOOK_ID + " = ? ", new String[]{id});
    }

    public void updateTagCNName(String id, String cnName) {
        Logger.d(TAG, "updateTagCNName -> " + id + "," + cnName);
        if (TextUtils.isEmpty(cnName)) {
            Logger.d(TAG, "updateTagCNName -> return");
            return;
        }
        ContentValues values = new ContentValues();
        values.put(DBHelper.TAG_CN_NAME, cnName);
        mDatabase.update(DBHelper.TABLE_NAME_TAG, values, DBHelper.TAG_ID + " = ? ", new String[]{id});
    }

    public void updateBookUpdateTime(String id) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.BOOK_UPDATE_TIME, System.currentTimeMillis());
        mDatabase.update(DBHelper.TABLE_NAME_BOOK, values, DBHelper.BOOK_ID + " = ? ", new String[]{id});
    }

    private void doFullNHBookTagsFromDB(NHBook nhBook) {
        Cursor tagCursor = mDatabase.query(DBHelper.TABLE_NAME_TAG,
                new String[]{
                        DBHelper.TAG_ID,
                        DBHelper.TAG_COUNT,
                        DBHelper.TAG_NAME,
                        DBHelper.TAG_CN_NAME,
                        DBHelper.TAG_TYPE,
                        DBHelper.TAG_URL
                }, DBHelper.TAG_ID + " in (select _TAG_ID from BOOK_TAG WHERE _BOOK_ID = ?)",
                new String[]{nhBook.getId()}, null, null, null);

        //Cursor tagCursor = mDatabase.rawQuery("SELECT * FROM TAG WHERE _ID IN (select _TAG_ID from _BOOK_TAG WHERE _BOOK_ID = ? )",new String[]{nhBookId});

        while (tagCursor.moveToNext()) {
            NHBookTag tmpTag = new NHBookTag();
            tmpTag.setId(tagCursor.getInt(tagCursor.getColumnIndex(DBHelper.TAG_ID)));
            tmpTag.setCount(tagCursor.getInt(tagCursor.getColumnIndex(DBHelper.TAG_COUNT)));
            tmpTag.setName(tagCursor.getString(tagCursor.getColumnIndex(DBHelper.TAG_NAME)));
            tmpTag.setType(tagCursor.getString(tagCursor.getColumnIndex(DBHelper.TAG_TYPE)));
            tmpTag.setUrl(tagCursor.getString(tagCursor.getColumnIndex(DBHelper.TAG_URL)));
            tmpTag.setCnName(tagCursor.getString(tagCursor.getColumnIndex(DBHelper.TAG_CN_NAME)));
            nhBook.addTag(tmpTag);
        }
        Logger.d(TAG, "nhBook detail -> " + nhBook);
        tagCursor.close();
        Logger.d(TAG, "doFullNHBookTagsFromDB setInDBDetail");
    }

    private void updateNHBookWithCursor(Cursor bookCursor, NHBook nhBook) {
        nhBook.setId(bookCursor.getString(bookCursor.getColumnIndex(DBHelper.BOOK_ID)));
        nhBook.setImgId(bookCursor.getString(bookCursor.getColumnIndex(DBHelper.BOOK_IMG_ID)));
        nhBook.setTitle(bookCursor.getString(bookCursor.getColumnIndex(DBHelper.BOOK_TITLE)));
        nhBook.setImgUrl(bookCursor.getString(bookCursor.getColumnIndex(DBHelper.BOOK_IMG_URL)));
        nhBook.setLanguageType(bookCursor.getInt(bookCursor.getColumnIndex(DBHelper.BOOK_LANG_TYPE)));
        nhBook.setPageNumber(bookCursor.getInt(bookCursor.getColumnIndex(DBHelper.BOOK_PAGE_NUM)));
        nhBook.setFavorite(bookCursor.getInt(bookCursor.getColumnIndex(DBHelper.BOOK_FAVORITE)));
        nhBook.setImgType(bookCursor.getString(bookCursor.getColumnIndex(DBHelper.BOOK_IMG_TYPE)));
        nhBook.setDownloadState(bookCursor.getInt(bookCursor.getColumnIndex(DBHelper.BOOK_DL_STATE)));
        Logger.d(TAG, "nhBook -> " + nhBook);
    }

    public void updateNHBookDLState(String id, int state) {
        Logger.d(TAG, "updateNHBookDLState -> " + state);
        ContentValues values = new ContentValues();
        values.put(DBHelper.BOOK_DL_STATE, state);
        mDatabase.update(DBHelper.TABLE_NAME_BOOK, values, DBHelper.BOOK_ID + " = ? ", new String[]{id});
    }

    public void updateDownLoadImgState(String bookId, int index, int state) {
        Logger.d(TAG, "updateDownLoadImgState -> " + bookId);
        ContentValues values = new ContentValues();
        values.put(DBHelper.DOWNLOAD_IMG_STATE, state);
        mDatabase.update(DBHelper.TABLE_NAME_DOWNLOAD,
                values,
                DBHelper.DOWNLOAD_BOOK_ID + " =  ? AND " + DBHelper.DOWNLOAD_IMG_INDEX + " = ? ",
                new String[]{bookId, index + ""});
    }

}
