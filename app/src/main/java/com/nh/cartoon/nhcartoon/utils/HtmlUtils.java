package com.nh.cartoon.nhcartoon.utils;

import android.content.Context;
import android.text.TextUtils;

import com.nh.cartoon.nhcartoon.bean.NHBook;
import com.nh.cartoon.nhcartoon.database.BookDBHelper;

import java.util.Map;

import static com.nh.cartoon.nhcartoon.utils.Constant.BOOK_DETAILS_URL;
import static com.nh.cartoon.nhcartoon.utils.Constant.CN_TAG;
import static com.nh.cartoon.nhcartoon.utils.Constant.GB_TAG;
import static com.nh.cartoon.nhcartoon.utils.Constant.JP_TAG;
import static com.nh.cartoon.nhcartoon.utils.Constant.LANGUAGE_TYPE_CN;
import static com.nh.cartoon.nhcartoon.utils.Constant.LANGUAGE_TYPE_GB;
import static com.nh.cartoon.nhcartoon.utils.Constant.LANGUAGE_TYPE_JP;

public class HtmlUtils {

    private static final String TAG = "HtmlUtils";

    public static NHBook buildNHBook(String str) {
        String title = StringUtils.getMatcherStr("<div class=\"caption\">.*</div>", str);
        title = title.substring(21, title.length() - 16);

        String imgUrl = StringUtils.getMatcherStr("//t.nhentai.net/galleries/[0-9]*/thumb.(jpg|png)", str);
        imgUrl = "https:" + imgUrl;

        String imgType = "jpg";
        if (imgUrl.contains("png")) {
            imgType = "png";
        }
//
        String tmpLanguage = StringUtils.getMatcherStr("data-tags=\"[0-9 ]*\"", str);
        Logger.e(TAG, "tmpLanguage -> " + tmpLanguage);
        int languageType = LANGUAGE_TYPE_GB;
        if (tmpLanguage.contains(CN_TAG)) {
            Logger.e(TAG, "tmpLanguage -> CN_TAG");
            languageType = LANGUAGE_TYPE_CN;
        } else if (tmpLanguage.contains(GB_TAG)) {
            Logger.e(TAG, "tmpLanguage -> GB_TAG");
            languageType = LANGUAGE_TYPE_GB;
        } else if (tmpLanguage.contains(JP_TAG)) {
            Logger.e(TAG, "tmpLanguage -> JP_TAG");
            languageType = LANGUAGE_TYPE_JP;
        }

        String imgId = StringUtils.getMatcherStr("es/[0-9]*/th", imgUrl);
        imgId = imgId.substring(3, imgId.length() - 3);
        //String bookDetails = StringUtils.getMatcherStr("a href=\"/g/[0-9]*/\" class", str);
        //bookDetails = bookDetails.substring(8, bookDetails.length() - 7);

        String id = StringUtils.getMatcherStr("a href=\"/g/[0-9]*/\" class", str);
        id = id.substring(11, id.length() - 8);

        //https://t.nhentai.net/galleries/1353842/cover.jpg
        //imgUrl = StringUtils.replaceParam(BOOK_IMG_URL, new String[]{imgId});
        NHBook nhBook = new NHBook();
        nhBook.setImgUrl(imgUrl);
        nhBook.setImgId(imgId);
        nhBook.setTitle(title);
        nhBook.setId(id);
        nhBook.setLanguageType(languageType);
        nhBook.setImgType(imgType);
        return nhBook;
    }

    public static NHBook updateNHBookDetails(String str, Context context) {
        StringBuffer sb = new StringBuffer();
        sb.append(str.replaceAll("\r", "").replaceAll("\n", "").replaceAll("\t", ""));
        String id = StringUtils.getMatcherStr("<a href=\"/g/[0-9]*/download\"", sb);
        if (TextUtils.isEmpty(id)) {
            Logger.e(TAG, "updateNHBookDetails error, id ->" + id);
            return null;
        }
        id = id.substring(12, id.length() - 10);

        NHBook nhBook = BookDBHelper.getInstance(context).queryNHBookById(id);
        if (nhBook == null) {
            Logger.e(TAG, "nhBook is null, id ->" + id);
            return null;
        }
        String detailJson = StringUtils.getMatcherStr("N.gallery\\(.*\\}\\);", str);
        if (!TextUtils.isEmpty(detailJson)) {
            Logger.d(TAG, "dispose json for detail!!!");

            detailJson = detailJson.substring(10, detailJson.length() - 2);
            DetaliJsonUtils.updateNHBookWithJson(detailJson, nhBook);
            BookDBHelper.getInstance(context).insertBookDetail(nhBook);
            return nhBook;
        }
        Logger.d(TAG, "dispose html for detail!!!");
        String tagStr = StringUtils.getMatcherStr("field-name.*</span></a></span>", sb);
        String[] strList = tagStr.split("</span></a></span>");
        for (String tmpStr : strList) {
            Logger.e(TAG, "tmpStr->" + tmpStr);
            TagUtils.disposeTagString(tmpStr, nhBook);
        }
        BookDBHelper.getInstance(context).insertBookDetail(nhBook);
        return nhBook;
    }


}
