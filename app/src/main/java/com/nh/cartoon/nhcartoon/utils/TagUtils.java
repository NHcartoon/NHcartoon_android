package com.nh.cartoon.nhcartoon.utils;

import android.text.TextUtils;

import com.nh.cartoon.nhcartoon.bean.NHBook;

import java.util.List;

public class TagUtils {

    private static final String TAG = "TagUtils";

    private static final int TAG_TYPE_PARODIES = 0;
    private static final int TAG_TYPE_CHARACTERS = 1;
    private static final int TAG_TYPE_TAGS = 2;
    private static final int TAG_TYPE_ARTISTS = 3;
    private static final int TAG_TYPE_GROUPS = 4;
    private static final int TAG_TYPE_LANGUAGE = 5;
    private static final int TAG_TYPE_CATEGORIES = 6;
    private static final int TAG_TYPE_UNKNOW = 9999;

    public static void disposeTagString(String tagString, NHBook nhBook) {
        if (TextUtils.isEmpty(tagString)) {
            return;
        }
        List<String> tmpList = StringUtils.getMatcherList("a href=\"\\S*\"", tagString);
        int startIndex = 0;
        int endIndex = 2;
        int tagType = 0;
        if (tagString.contains("field-name \">Parodies:")) {
            Logger.d(TAG, "Parodies");
            startIndex = 8 + 8;
            tagType = TAG_TYPE_PARODIES;
        } else if (tagString.contains("field-name \">Characters:")) {
            Logger.d(TAG, "Characters");
            //TODO
            startIndex = 8 + 8;
            tagType = TAG_TYPE_CHARACTERS;
        } else if (tagString.contains("field-name \">Tags:")) {
            Logger.d(TAG, "Tags");
            startIndex = 8 + 5;
            tagType = TAG_TYPE_TAGS;
        } else if (tagString.contains("field-name \">Artists:")) {
            Logger.d(TAG, "Artists");
            startIndex = 8 + 8;
            tagType = TAG_TYPE_ARTISTS;
        } else if (tagString.contains("field-name \">Groups:")) {
            Logger.d(TAG, "Groups");
            //TODO
            startIndex = 8 + 8;
            tagType = TAG_TYPE_GROUPS;
        } else if (tagString.contains("field-name \">Languages:")) {
            Logger.d(TAG, "Language");
            startIndex = 8 + 10;
            tagType = TAG_TYPE_LANGUAGE;
        } else if (tagString.contains("field-name \">Categories:")) {
            Logger.d(TAG, "Categories");
            startIndex = 8 + 10;
            tagType = TAG_TYPE_CATEGORIES;
        } else {
            tagType = TAG_TYPE_UNKNOW;
        }

        StringBuffer sb = new StringBuffer();
        for (String tmpStr : tmpList) {
            Logger.d(TAG, "tmpStr -> " + tmpStr);
            sb.append(tmpStr.substring(startIndex, tmpStr.length() - endIndex));
            sb.append(",");
        }
        if (TextUtils.isEmpty(sb)) {
            return;
        }
        String retStr = sb.toString().substring(0, sb.length() - 1);
        switch (tagType) {
            case TAG_TYPE_PARODIES:
                nhBook.setParodies(retStr);
                break;
            case TAG_TYPE_CHARACTERS:
                nhBook.setCharacters(retStr);
                break;
            case TAG_TYPE_TAGS:
                nhBook.setTags(retStr);
                break;
            case TAG_TYPE_ARTISTS:
                nhBook.setArtists(retStr);
                break;
            case TAG_TYPE_GROUPS:
                nhBook.setGroups(retStr);
                break;
            case TAG_TYPE_LANGUAGE:
                nhBook.setLanguage(retStr);
                break;
            case TAG_TYPE_CATEGORIES:
                nhBook.setCategories(retStr);
                break;
            case TAG_TYPE_UNKNOW:
            default:
                Logger.e(TAG, "tag type err, str -> " + tagString);
        }
    }
}
