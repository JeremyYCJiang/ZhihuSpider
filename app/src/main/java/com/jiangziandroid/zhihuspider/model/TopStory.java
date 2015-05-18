package com.jiangziandroid.zhihuspider.model;

/**
 * Created by JeremyYCJiang on 2015/5/14.
 */
public class TopStory {
    private long mStoryId;
    private String mImageStringUri;
    private String mTitle;


    public long getStoryId() {
        return mStoryId;
    }

    public void setStoryId(long storyId) {
        mStoryId = storyId;
    }

    public String getImageStringUri() {
        return mImageStringUri;
    }

    public void setImageStringUri(String imageStringUri) {
        mImageStringUri = imageStringUri;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }
}
