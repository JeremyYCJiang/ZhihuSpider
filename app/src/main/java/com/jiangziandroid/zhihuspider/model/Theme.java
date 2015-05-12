package com.jiangziandroid.zhihuspider.model;

/**
 * Created by JeremyYCJiang on 2015/5/12.
 */
public class Theme {
    private int mThemeId;
    private String mThumbnailUri;
    private String mThemeName;
    private String mDescription;

    public int getThemeId() {
        return mThemeId;
    }

    public void setThemeId(int themeId) {
        mThemeId = themeId;
    }

    public String getThumbnailUri() {
        return mThumbnailUri;
    }

    public void setThumbnailUri(String thumbnail) {
        mThumbnailUri = thumbnail;
    }

    public String getThemeName() {
        return mThemeName;
    }

    public void setThemeName(String themeName) {
        mThemeName = themeName;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }
}
