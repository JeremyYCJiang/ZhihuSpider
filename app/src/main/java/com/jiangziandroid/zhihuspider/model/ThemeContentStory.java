package com.jiangziandroid.zhihuspider.model;

/**
 * Created by JeremyYCJiang on 2015/5/28.
 */
public class ThemeContentStory {
    private long mThemeContentStoryId;
    private String mThemeContentStoryTitle;
    private int mThemeContentStoryType;

    public String getImagesUrl() {
        return mImagesUrl;
    }

    public void setImagesUrl(String imagesUrl) {
        mImagesUrl = imagesUrl;
    }

    private String mImagesUrl;

    public long getThemeContentStoryId() {
        return mThemeContentStoryId;
    }

    public void setThemeContentStoryId(long themeContentStoryId) {
        mThemeContentStoryId = themeContentStoryId;
    }

    public String getThemeContentStoryTitle() {
        return mThemeContentStoryTitle;
    }

    public void setThemeContentStoryTitle(String themeContentStoryTitle) {
        mThemeContentStoryTitle = themeContentStoryTitle;
    }

    public int getThemeContentStoryType() {
        return mThemeContentStoryType;
    }

    public void setThemeContentStoryType(int themeContentStoryType) {
        mThemeContentStoryType = themeContentStoryType;
    }
}
