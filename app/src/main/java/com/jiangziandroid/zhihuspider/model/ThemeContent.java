package com.jiangziandroid.zhihuspider.model;

import java.util.ArrayList;

/**
 * Created by JeremyYCJiang on 2015/5/28.
 */
public class ThemeContent {
    private String mThemeName;
    private String mThemeDescription;
    private String mThemeBackgroundUrl;
    private ArrayList<ThemeContentEditor> mEditorsArrayList;
    private ArrayList<ThemeContentStory> mThemeStoriesArrayList;

    public String getThemeName() {
        return mThemeName;
    }

    public void setThemeName(String themeName) {
        mThemeName = themeName;
    }

    public String getThemeDescription() {
        return mThemeDescription;
    }

    public void setThemeDescription(String themeDescription) {
        mThemeDescription = themeDescription;
    }

    public String getThemeBackgroundUrl() {
        return mThemeBackgroundUrl;
    }

    public void setThemeBackgroundUrl(String themeBackgroundUrl) {
        mThemeBackgroundUrl = themeBackgroundUrl;
    }

    public ArrayList<ThemeContentEditor> getEditorsArrayList() {
        return mEditorsArrayList;
    }

    public void setEditorsArrayList(ArrayList<ThemeContentEditor> editorsArrayList) {
        mEditorsArrayList = editorsArrayList;
    }

    public ArrayList<ThemeContentStory> getThemeStoriesArrayList() {
        return mThemeStoriesArrayList;
    }

    public void setThemeStoriesArrayList(ArrayList<ThemeContentStory> themeStoriesArrayList) {
        mThemeStoriesArrayList = themeStoriesArrayList;
    }
}
