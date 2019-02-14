package com.nh.cartoon.nhcartoon.utils;

import com.nh.cartoon.nhcartoon.bean.NHBook;
import com.nh.cartoon.nhcartoon.bean.NHBookTag;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DetaliJsonUtils {

    public static void updateNHBookWithJson(String json, NHBook nhBook) {
        try {
            JSONObject jo = new JSONObject(json);
            nhBook.setImgId(jo.getString("media_id"));

            JSONArray tagJA = jo.getJSONArray("tags");
            for (int i = 0; i < tagJA.length(); i++) {
                JSONObject tagJO = tagJA.getJSONObject(i);
                NHBookTag tag = new NHBookTag();
                tag.setId(tagJO.getInt("id"));
                tag.setType(tagJO.getString("type"));
                tag.setName(tagJO.getString("name"));
                tag.setUrl(tagJO.getString("url"));
                tag.setCount(tagJO.getInt("count"));
                nhBook.addTag(tag);
            }
            nhBook.setPageNumber(jo.getInt("num_pages"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
