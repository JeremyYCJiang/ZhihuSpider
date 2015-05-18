package com.jiangziandroid.zhihuspider.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.jiangziandroid.zhihuspider.model.TopStory;
import com.jiangziandroid.zhihuspider.ui.SlideImageFragment;

import java.util.ArrayList;

/**
 * Created by JeremyYCJiang on 2015/5/14.
 */
public class SlideImageAdapter extends FragmentStatePagerAdapter {

    protected Context mContext;
    protected ArrayList<TopStory> mTopStories;
    public SlideImageAdapter(Context context, android.support.v4.app.FragmentManager fragmentManager,
                             ArrayList<TopStory> topStories){
        super(fragmentManager);
        mContext = context;
        mTopStories = topStories;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        String imageStringUri = mTopStories.get(position).getImageStringUri();
        String imageTitle = mTopStories.get(position).getTitle();
        long storyId = mTopStories.get(position).getStoryId();
        SlideImageFragment slideImageFragment = new SlideImageFragment();
        //use bundle to pass data to fragment
        Bundle bundle = new Bundle();
        bundle.putLong("StoryId",storyId);
        bundle.putString("ImageStringUri", imageStringUri);
        bundle.putString("ImageTitle", imageTitle);
        slideImageFragment.setArguments(bundle);
        return slideImageFragment;
    }

    @Override
    // Show 5 total pages.
    public int getCount() {
        return 5;
    }

}
