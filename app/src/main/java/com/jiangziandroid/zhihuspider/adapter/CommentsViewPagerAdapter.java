package com.jiangziandroid.zhihuspider.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.jiangziandroid.zhihuspider.ui.CommentsFragment;


/**
 * Created by JeremyYCJiang on 2015/5/21.
 */
public class CommentsViewPagerAdapter extends FragmentStatePagerAdapter{

    private Context mContext;
    private String[] mViewPagerTabTitles;
    private long mStoryId;
    public CommentsViewPagerAdapter(Context context, long storyId, String[] viewPagerTabTitles, FragmentManager fm) {
        super(fm);
        mContext = context;
        mViewPagerTabTitles = viewPagerTabTitles;
        mStoryId = storyId;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        String[] LongOrShort = {"long","short"};
        CommentsFragment commentsFragment = new CommentsFragment();
        //use bundle to pass data to fragment
        Bundle bundle = new Bundle();
        bundle.putLong("StoryId", mStoryId);
        bundle.putString("LongOrShort", LongOrShort[position]);
        commentsFragment.setArguments(bundle);
        return commentsFragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
            return mViewPagerTabTitles[position];
        }


}





