package com.nh.cartoon.nhcartoon.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NHBook {

    private String id;
    private String imgId;
    private String title;
    private String imgUrl;
    private int languageType;
    private String imgType;
    private String parodies;//模仿
    private String characters;//人物
    private String tags;//标签
    private String artists;//作者
    private String groups;//组织
    private String language;//语言
    private String categories;//分类
    private Map<String, List<NHBookTag>> tagMap = new HashMap<>();
    private int pageNumber;
    private int favorite;
    private int downloadState;
    private int locatPath;
    private int dbState;
    private long updateTime;

    public String getImgType() {
        return imgType;
    }

    public void setImgType(String imgType) {
        this.imgType = imgType;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public int getDbState() {
        return dbState;
    }

    public void setDbState(int dbState) {
        this.dbState = dbState;
    }

    public int getLocatPath() {
        return locatPath;
    }

    public void setLocatPath(int locatPath) {
        this.locatPath = locatPath;
    }

    public int getDownloadState() {
        return downloadState;
    }

    public void setDownloadState(int downloadState) {
        this.downloadState = downloadState;
    }

    public int getFavorite() {
        return favorite;
    }

    public void setFavorite(int favorite) {
        this.favorite = favorite;
    }

    public int getLanguageType() {
        return languageType;
    }

    public void setLanguageType(int languageType) {
        this.languageType = languageType;
    }

    public void addTag(NHBookTag tag) {
        List<NHBookTag> tags = null;
        if (tagMap.containsKey(tag.getType())) {
            tags = tagMap.get(tag.getType());
        } else {
            tags = new ArrayList<>();
            tagMap.put(tag.getType(), tags);
        }
        tags.add(tag);
    }

    public Map<String, List<NHBookTag>> getTagMap() {
        return tagMap;
    }

    public String getImgId() {
        return imgId;
    }

    public void setImgId(String imgId) {
        this.imgId = imgId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getParodies() {
        return parodies;
    }

    public void setParodies(String parodies) {
        this.parodies = parodies;
    }

    public String getCharacters() {
        return characters;
    }

    public void setCharacters(String characters) {
        this.characters = characters;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getArtists() {
        return artists;
    }

    public void setArtists(String artists) {
        this.artists = artists;
    }

    public String getGroups() {
        return groups;
    }

    public void setGroups(String groups) {
        this.groups = groups;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    @Override
    public String toString() {
        return "NHBook{" +
                "id='" + id + '\'' +
                ", imgId='" + imgId + '\'' +
                ", title='" + title + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                ", languageType=" + languageType +
                ", parodies='" + parodies + '\'' +
                ", characters='" + characters + '\'' +
                ", tags='" + tags + '\'' +
                ", artists='" + artists + '\'' +
                ", groups='" + groups + '\'' +
                ", language='" + language + '\'' +
                ", categories='" + categories + '\'' +
                ", tagMap=" + tagMap +
                ", pageNumber=" + pageNumber +
                ", favorite=" + favorite +
                '}';
    }
}
