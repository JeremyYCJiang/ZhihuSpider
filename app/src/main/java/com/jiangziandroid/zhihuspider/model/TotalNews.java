package com.jiangziandroid.zhihuspider.model;

import java.util.ArrayList;

/**
 * Created by JeremyYCJiang on 2015/5/22.
 */
public class TotalNews {
    private ArrayList<LatestNews> mTotalNewsArrayList;

    public ArrayList<LatestNews> getTotalNewsArrayList() {
        return mTotalNewsArrayList;
    }

    public void setTotalNewsArrayList(ArrayList<LatestNews> totalNewsArrayList) {
        mTotalNewsArrayList = totalNewsArrayList;
    }
}
