package com.nh.cartoon.nhcartoon.bean;

import com.nh.cartoon.nhcartoon.logic.DownLoadHelper;
import com.nh.cartoon.nhcartoon.utils.StringUtils;

import static com.nh.cartoon.nhcartoon.utils.Constant.DETAIL_LIST_IMG_URL;
import static com.nh.cartoon.nhcartoon.utils.Constant.DOWNLOAD_IMG_PATH;

public class DownloadImg {

    private int id;
    private String bookId;
    private String imgId;
    private int imgState;
    private int imgIndex;
    private String imgType;

    public String getImgUrl() {
        return StringUtils.replaceParam(DETAIL_LIST_IMG_URL, this.imgId, this.imgIndex + "", this.imgType);
    }

    public String getImgPath() {
        return StringUtils.replaceParam(DownLoadHelper.getAlbumStorageDir() + DOWNLOAD_IMG_PATH, this.imgId, this.imgIndex + "", this.imgType);
    }

    public String getImgId() {
        return imgId;
    }

    public void setImgId(String imgId) {
        this.imgId = imgId;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getImgState() {
        return imgState;
    }

    public void setImgState(int imgState) {
        this.imgState = imgState;
    }

    public int getImgIndex() {
        return imgIndex;
    }

    public void setImgIndex(int imgIndex) {
        this.imgIndex = imgIndex;
    }

    public String getImgType() {
        return imgType;
    }

    public void setImgType(String imgType) {
        this.imgType = imgType;
    }
}


