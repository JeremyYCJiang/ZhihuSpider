package com.jiangziandroid.zhihuspider.model;

/**
 * Created by JeremyYCJiang on 2015/5/20.
 */
public class NewsExtras {
    private int mComments;
    private int mLongComments;
    private int mShortComments;
    private int mPopularity;

    public int getComments() {
        return mComments;
    }

    public void setComments(int comments) {
        mComments = comments;
    }

    public int getLongComments() {
        return mLongComments;
    }

    public void setLongComments(int longComments) {
        mLongComments = longComments;
    }

    public int getShortComments() {
        return mShortComments;
    }

    public void setShortComments(int shortComments) {
        mShortComments = shortComments;
    }

    public int getPopularity() {
        return mPopularity;
    }

    public void setPopularity(int popularity) {
        mPopularity = popularity;
    }
}
