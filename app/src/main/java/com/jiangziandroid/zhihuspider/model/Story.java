package com.jiangziandroid.zhihuspider.model;

/**
 * Created by JeremyYCJiang on 2015/5/14.
 */
public class Story {
    private String mImageStringUri;
    private String mTitle;
    private boolean mMultipic;

    public boolean isMultipic() {
        return mMultipic;
    }

    public void setMultipic(boolean multipic) {
        mMultipic = multipic;
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
