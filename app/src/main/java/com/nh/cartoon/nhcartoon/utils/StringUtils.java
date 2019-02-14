package com.nh.cartoon.nhcartoon.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

    private static final String TAG = "StringUtils";

    public static String replaceParam(String str, String... param) {
        if (param == null || param.length == 0) {
            return str;
        }

        for (int i = 0; i < param.length; i++) {
            str = str.replace("{" + i + "}", param[i]);
        }
        return str;
    }

    public static List<String> getMatcherList(String regex, CharSequence source) {
        List<String> ret = new ArrayList<>();

        String result = "";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(source);
        while (matcher.find()) {
            result = matcher.group(0);
            Logger.d(TAG, result);
            ret.add(result);
        }
        return ret;
    }

    public static String getMatcherStr(String regex, CharSequence source) {
        Logger.d(TAG, "source:" + source);
        String result = "";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(source);
        if (matcher.find()) {
            result = matcher.group(0);
            Logger.d(TAG, result);
        } else {
            Logger.e(TAG, "no match");
        }
        return result;
    }
}
