package com.nh.cartoon.nhcartoon.utils;

public class Constant {

    public static final int LANGUAGE_TYPE_CN = 0;
    public static final int LANGUAGE_TYPE_GB = 1;
    public static final int LANGUAGE_TYPE_JP = 2;

    public static final String CN_TAG = "29963";
    public static final String GB_TAG = "12227";
    public static final String JP_TAG = "6346";

    public static final int IS_FAVORITE = 1;
    public static final int NOT_FAVORITE = 0;

    public static final int NO_DOWNLOAD = 0;
    public static final int DOWNLOADING = 1;
    public static final int DOWNLOAD_COMPLETE = 2;

    public static final int DB_STATE_NULL = 0;
    public static final int DB_STATE_SIMPLE = 1;
    public static final int DB_STATE_DETAIL = 2;

    public static final String DETAIL_URL = "https://nhentai.net/g/{0}/";

    public static final String BOOK_IMG_URL = "https://i.nhentai.net/galleries/{0}/1.{1}";
    //public static final String BOOK_IMG_URL = "https://t.nhentai.net/galleries/{0}/thumb.{1}";

    public static final String BOOK_DETAILS_URL = "https://nhentai.net/g/{0}/";

    //https://t.nhentai.net/galleries/1354758/20t.jpg
    //https://i.nhentai.net/galleries/1354748/13.jpg
    public static final String DETAIL_LIST_SIMPLE_IMG_URL = "https://t.nhentai.net/galleries/{0}/{1}t.{2}";
    public static final String DETAIL_LIST_IMG_URL = "https://i.nhentai.net/galleries/{0}/{1}.{2}";

    //public static final String HOME_URL = "https://nhentai.net/";
    public static final String HOME_URL = "https://nhentai.net/language/chinese/";
    public static final String TAG_URL = "https://nhentai.net{0}";


    public static final String DOWNLOAD_BOOK_PATH = "/nhcartoon/books/{0}";
    public static final String DOWNLOAD_IMG_PATH = DOWNLOAD_BOOK_PATH + "/{1}.{2}";

    public static final String SEARCH_URL = "https://nhentai.net/search/?q={0}";

}
