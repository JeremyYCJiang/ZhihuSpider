package com.jiangziandroid.zhihuspider.model;

import java.util.ArrayList;

/**
 * Created by JeremyYCJiang on 2015/5/19.
 */
public class NewsDetails {
    private long mStoryId;
    private String mTitle;
    private String mImageSource;
    private String mImageStringUri;
    private ArrayList<Recommender> mRecommenders;
    private String mBody;

    public String getBody() {
        return mBody;
    }

    public void setBody(String body) {
        mBody = body;
    }

    public long getStoryId() {
        return mStoryId;
    }

    public void setStoryId(long storyId) {
        mStoryId = storyId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getImageSource() {
        return mImageSource;
    }

    public void setImageSource(String imageSource) {
        mImageSource = imageSource;
    }

    public String getImageStringUri() {
        return mImageStringUri;
    }

    public void setImageStringUri(String imageStringUri) {
        mImageStringUri = imageStringUri;
    }

    public ArrayList<Recommender> getRecommenders() {
        return mRecommenders;
    }

    public void setRecommenders(ArrayList<Recommender> recommenders) {
        mRecommenders = recommenders;
    }
}
