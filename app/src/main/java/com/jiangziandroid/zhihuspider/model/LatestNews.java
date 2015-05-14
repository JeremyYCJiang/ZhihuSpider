package com.jiangziandroid.zhihuspider.model;

import java.util.ArrayList;

/**
 * Created by JeremyYCJiang on 2015/5/14.
 */
public class LatestNews {
    private String date;
    private ArrayList<TopStory> mTopStories;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ArrayList<TopStory> getTopStories() {
        return mTopStories;
    }

    public void setTopStories(ArrayList<TopStory> topStories) {
        mTopStories = topStories;
    }
}
