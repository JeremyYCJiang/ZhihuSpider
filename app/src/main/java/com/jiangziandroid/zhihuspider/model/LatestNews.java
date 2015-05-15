package com.jiangziandroid.zhihuspider.model;

import java.util.ArrayList;

/**
 * Created by JeremyYCJiang on 2015/5/14.
 */
public class LatestNews {
    private String mDate;
    private ArrayList<TopStory> mTopStories;
    private ArrayList<Story> mStories;

    public ArrayList<Story> getStories() {
        return mStories;
    }

    public void setStories(ArrayList<Story> stories) {
        mStories = stories;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }

    public ArrayList<TopStory> getTopStories() {
        return mTopStories;
    }

    public void setTopStories(ArrayList<TopStory> topStories) {
        mTopStories = topStories;
    }
}
