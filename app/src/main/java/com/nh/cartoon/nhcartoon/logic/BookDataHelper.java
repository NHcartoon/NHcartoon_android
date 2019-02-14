package com.nh.cartoon.nhcartoon.logic;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.nh.cartoon.nhcartoon.bean.NHBook;
import com.nh.cartoon.nhcartoon.database.BookDBHelper;
import com.nh.cartoon.nhcartoon.utils.Constant;
import com.nh.cartoon.nhcartoon.utils.HtmlUtils;
import com.nh.cartoon.nhcartoon.utils.Logger;
import com.nh.cartoon.nhcartoon.utils.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class BookDataHelper {

    private static final String TAG = "BookDataHelper";

    private static volatile BookDataHelper singleton;
    private static final int DISPOSE_LIST_HTML = 0x000FFF;
    private static final int DISPOSE_DETAIL_HTML = 0x001FFF;
    private static final int GET_DETAIL_HTML = 0x002FFF;

    private Context mContext;
    private DataThread mDataThread;
    private OkHttpClient client = new OkHttpClient();
    private BookDBHelper mBookDBHelper;
    private BookListLoadListener mBookListLoadListener;
    private DetailLoadListener mDetailLoadListener;
    private List<NHBook> mNhBookList = new ArrayList<>();

    private int mLoadIndex = 1;
    private String mLoadUrl = "";

    @SuppressLint("HandlerLeak")
    private Handler mHandler;

    public static BookDataHelper getInstance(Context context) {
        if (singleton == null) {
            synchronized (BookDataHelper.class) {
                if (singleton == null) {
                    singleton = new BookDataHelper(context);
                }
            }
        }
        return singleton;
    }

    private BookDataHelper(Context context) {
        mContext = context.getApplicationContext();
        mBookDBHelper = BookDBHelper.getInstance(mContext);
        mDataThread = new DataThread();
        mDataThread.start();
        //TODO 由于多线程的原因 mDataThread 在此时可能为null
        mHandler = new Handler(mDataThread.childLooper) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case DISPOSE_LIST_HTML:
                        Logger.d(TAG, "start DISPOSE_HTML");
                        Bundle bundle = msg.getData();
                        String string = bundle.getString("HTML");
                        String regex = "<div class=\"gallery\" data-tags.*</div></a></div>";
                        List<String> booksList = StringUtils.getMatcherList(regex, string);
                        List<NHBook> nhBookList = new ArrayList<>();
                        for (String str : booksList) {
                            NHBook nhBook = HtmlUtils.buildNHBook(str);
                            Logger.d(TAG, "nhBook -> " + nhBook.toString());
                            mBookDBHelper.insertSimpleBook(nhBook);
                            nhBookList.add(nhBook);
                        }
                        mNhBookList.addAll(nhBookList);
                        if (mBookListLoadListener != null) {
                            mBookListLoadListener.doBookLoadComplete(mNhBookList);
                        }

                        break;
                    case DISPOSE_DETAIL_HTML:
                        Logger.d(TAG, "start DISPOSE_DETAIL_HTML");
                        Bundle b = msg.getData();
                        String s = b.getString("HTML");
                        NHBook nhBook = HtmlUtils.updateNHBookDetails(s, mContext);
                        if (mDetailLoadListener != null) {
                            if (nhBook != null) {
                                mDetailLoadListener.doDetailLoadComplete(nhBook);
                            } else {
                                mDetailLoadListener.onFailure();
                            }
                        }
                        break;
                    case GET_DETAIL_HTML:
                        break;
                    default:
                        Logger.e(TAG, "handler msg err");
                }
            }
        };
    }

    public void getQuickNext() {
        mNhBookList.clear();
        getNextPage();
    }

    public void getBookDataFromHome() {
        getBookWithUrl(Constant.HOME_URL);
    }

    public void getNextPage() {
        mLoadIndex++;
        getBookWithUrl(mLoadUrl);
    }

    public void getBookWithUrl(String url) {
        if (!TextUtils.equals(url, mLoadUrl)) {
            mNhBookList.clear();
            mLoadUrl = url;
            mLoadIndex = 1;
        }
        //获取基本信息
        String endUrl = mLoadUrl + "?page=" + mLoadIndex;
        Request.Builder builder = new Request.Builder().url(endUrl);
        Call call = client.newCall(builder.build());
        call.enqueue(mListBookCallback);//加入调度队列
    }

    public void getBookDataFromLove() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mLoadUrl = null;
                List<NHBook> nhBooks = mBookDBHelper.queryLoveBooks();
                Logger.d(TAG, "nhBooks size ->" + nhBooks.size());
                if (mBookListLoadListener != null) {
                    mBookListLoadListener.doBookLoadComplete(nhBooks);
                }
            }
        }).start();
    }

    public void getBookDataFromHistory() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mLoadUrl = null;
                List<NHBook> nhBooks = mBookDBHelper.queryHistoryBooks();
                Logger.d(TAG, "nhBooks size ->" + nhBooks.size());
                if (mBookListLoadListener != null) {
                    mBookListLoadListener.doBookLoadComplete(nhBooks);
                }
            }
        }).start();
    }

    public void updateBookDetailFromWeb(String id) {
        Logger.e(TAG, "getBookDetail -> " + id);
        NHBook nhbook = BookDBHelper.getInstance(mContext).queryNHBookById(id);
        if (nhbook != null && nhbook.getPageNumber() != 0) {
            BookDBHelper.getInstance(mContext).updateBookUpdateTime(id);
            mDetailLoadListener.doDetailLoadComplete(nhbook);
            return;
        }

        //获取详细信息
        String url = StringUtils.replaceParam(Constant.DETAIL_URL, id);
        Logger.e(TAG, "detail url -> " + url);
        Request.Builder builder = new Request.Builder().url(url);
        Call call = client.newCall(builder.build());
        call.enqueue(mBookDetailCallback);//加入调度队列
    }

    public NHBook getBookDetailFromDB(String id) {
        BookDBHelper.getInstance(mContext).updateBookUpdateTime(id);
        return BookDBHelper.getInstance(mContext).queryNHBookById(id);
    }

    //详情请求回调
    private Callback mBookDetailCallback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            Log.i(TAG, "mBookDetailCallback onFailure");
            e.printStackTrace();
            getBookDataFromHome();
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String str = new String(response.body().bytes(), "utf-8");

            Message message = Message.obtain();
            message.what = DISPOSE_DETAIL_HTML;
            Bundle bundle = new Bundle();
            bundle.putString("HTML", str);

            message.setData(bundle);

            mHandler.sendMessage(message);
        }
    };

    //列表请求回调
    private Callback mListBookCallback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            Log.i(TAG, "mListBookCallback onFailure");
            e.printStackTrace();
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            //从response从获取服务器返回的数据，转成字符串处理
            String str = new String(response.body().bytes(), "utf-8");

            Message message = Message.obtain();
            message.what = DISPOSE_LIST_HTML;
            Bundle bundle = new Bundle();
            bundle.putString("HTML", str);

            message.setData(bundle);

            mHandler.sendMessage(message);
        }
    };


    //监听
    public void setBookListLoadListener(BookListLoadListener bookListLoadListener) {
        this.mBookListLoadListener = bookListLoadListener;
    }

    public void setDetailLoadListener(DetailLoadListener detailLoadListener) {
        this.mDetailLoadListener = detailLoadListener;
    }

    public interface BookListLoadListener {
        void doBookLoadComplete(List<NHBook> NHBookList);
    }

    public interface DetailLoadListener {
        void doDetailLoadComplete(NHBook nhBook);

        void onFailure();
    }

    private class DataThread extends Thread {
        public Looper childLooper;

        @Override
        public void run() {
            Looper.prepare();//创建与当前线程相关的Looper
            childLooper = Looper.myLooper();//获取当前线程的Looper对象
            Looper.loop();//调用此方法，消息才会循环处理
        }
    }

}
