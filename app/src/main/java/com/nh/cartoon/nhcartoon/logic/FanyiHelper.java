package com.nh.cartoon.nhcartoon.logic;

import android.content.Context;

import com.nh.cartoon.nhcartoon.bean.NHBook;
import com.nh.cartoon.nhcartoon.bean.NHBookTag;
import com.nh.cartoon.nhcartoon.database.BookDBHelper;
import com.nh.cartoon.nhcartoon.utils.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FanyiHelper {

    private static final String TAG = "FanyiHelper";

    private static volatile FanyiHelper singleton;

    private Context mContext;

    public static FanyiHelper getInstance(Context context) {
        if (singleton == null) {
            synchronized (FanyiHelper.class) {
                if (singleton == null) {
                    singleton = new FanyiHelper(context);
                }
            }
        }
        return singleton;
    }

    private OkHttpClient mClient;

    private FanyiHelper(Context context) {
        mContext = context.getApplicationContext();
        mClient = new OkHttpClient.Builder().readTimeout(5, TimeUnit.SECONDS).build();
    }


    //
    private String fanyi(String str) {
        Logger.d(TAG, "start fanyi");
        Request request = new Request.Builder().url("https://fanyi.youdao.com/translate?&doctype=json&type=AUTO&i=" + str)
                .get().build();
        Call call = mClient.newCall(request);
        try {
            Response response = call.execute();
            String resStr = response.body().string();
            Logger.d(TAG, "resStr -> " + resStr);
            String retString = getRetString(resStr);
            Logger.d(TAG, "fanyi -> " + retString);
            return retString;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private String getRetString(String string) {
        Logger.d(TAG, "start getRetString");
        //{"type":"EN2ZH_CN","errorCode":0,"elapsedTime":1,"translateResult":[[{"src":"Compelte","tgt":"完成"}]]}
        try {
            JSONObject JO = new JSONObject(string);
            JSONArray JA1 = JO.getJSONArray("translateResult");

            if (JA1.length() > 0) {
                JSONArray JA2 = JA1.getJSONArray(0);
                if (JA2.length() > 0) {
                    JSONObject tmpJO = JA2.getJSONObject(0);
                    return tmpJO.getString("tgt");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    public void updateTagCNName(final NHBook nhBook) {
        Logger.d(TAG, "start updateTagCNName");
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (Map.Entry<String, List<NHBookTag>> entry : nhBook.getTagMap().entrySet()) {
                    for (NHBookTag tmpTag : entry.getValue()) {
                        String cnName = fanyi(tmpTag.getName());
                        tmpTag.setCnName(cnName);
                        BookDBHelper.getInstance(mContext).updateTagCNName(tmpTag.getId() + "", tmpTag.getCnName());
                    }
                }
            }
        }).start();
    }
}
