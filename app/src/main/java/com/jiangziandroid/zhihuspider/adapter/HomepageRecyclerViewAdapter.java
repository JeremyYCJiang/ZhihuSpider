package com.jiangziandroid.zhihuspider.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jiangziandroid.zhihuspider.R;
import com.jiangziandroid.zhihuspider.model.TotalNews;
import com.jiangziandroid.zhihuspider.ui.StoryActivity;
import com.squareup.picasso.Picasso;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by JeremyYCJiang on 2015/5/11.
 */

public class HomepageRecyclerViewAdapter extends
        RecyclerView.Adapter<HomepageRecyclerViewAdapter.HomepageRecyclerViewViewHolder> {

    // Declaring Variable to Understand which View is being worked on
    // IF the view under inflation and population is header or Item
    private static final int TYPE_VIEWPAGER = 0;
    private static final int TYPE_TIMETITLE = 1;
    private static final int TYPE_CONTENT = 2;

    private TotalNews mTotalNews;
    private Context mContext;
    private FragmentManager mFragmentManager;
    private int mTotalNewSize;
    private HashMap<Integer, Integer> mDateMap = new HashMap<>();
    private HashMap<Integer, Integer> mTimeMap = new HashMap<>();
    private HashMap<Integer, Integer> mItemMap = new HashMap<>();

    public HomepageRecyclerViewAdapter(Context context, TotalNews totalNews, FragmentManager fragmentManager){

        mTotalNews = totalNews;
        mContext = context;
        mFragmentManager = fragmentManager;
    }

    @Override
    public HomepageRecyclerViewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == 0 ){
            View view0 = LayoutInflater.from(parent.getContext()).inflate(R.layout.homepage_viewpager_row,
                    parent, false);
            return new HomepageRecyclerViewViewHolder(view0, viewType);
        }else if(viewType == 1){
            View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.homepage_time_title_item_row,
                    parent, false);
            return new HomepageRecyclerViewViewHolder(view1, viewType);
        }else {
            View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.homepage_content_item_row,
                    parent, false);
            return new HomepageRecyclerViewViewHolder(view2, viewType);
        }
    }


    //views(View)
    public class HomepageRecyclerViewViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        int HolderId;
        public TextView mTimeTitleTextView;
        public ImageView mItemIconImageView;
        public TextView mItemTextTextView;
        public LinearLayout mMultipicLL;
        // The pager adapter, which provides the pages to the view pager widget.
        public cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager mAutoScrollViewPager;
        // The pager widget, which handles animation and allows swiping horizontally to access,
        // previous and next wizard steps.
        public com.viewpagerindicator.CirclePageIndicator mCirclePageIndicator;
        public SlideImageAdapter mSlideImageAdapter;
        public long mStoryId;

        public HomepageRecyclerViewViewHolder(View itemView, int viewType) {
            super(itemView);
            if(viewType == 0){
                mAutoScrollViewPager = (cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager)
                        itemView.findViewById(R.id.imageViewPager);
                mCirclePageIndicator = (CirclePageIndicator) itemView.findViewById(R.id.viewpagerIndicator);
                HolderId = 0;
            }else if(viewType == 1){
                mTimeTitleTextView= (TextView) itemView.findViewById(R.id.rowTimeTitleText);
                HolderId = 1;
            }else {
                itemView.setOnClickListener(this);
                mItemIconImageView = (ImageView) itemView.findViewById(R.id.rowCollectionIcon);
                mItemTextTextView = (TextView) itemView.findViewById(R.id.rowHomepageText);
                mMultipicLL = (LinearLayout) itemView.findViewById(R.id.multipicLL);
                HolderId = 2;
            }
        }

        @Override
        public void onClick(View v) {
            if(getPosition()>1 && getPosition()<mTotalNews.getTotalNewsArrayList().get(0).getStories().size()+2)
                mStoryId = mTotalNews.getTotalNewsArrayList().get(0).getStories().get(getPosition()-2).getStoryId();
            else {
                mStoryId = mTotalNews.getTotalNewsArrayList().get(mDateMap.get(mTimeMap.get(getPosition())))
                        .getStories().get(mItemMap.get(getPosition())).getStoryId();
            }
            Intent intent = new Intent(v.getContext(), StoryActivity.class);
            intent.putExtra("StoryId", mStoryId);
            v.getContext().startActivity(intent);
        }
    }


    //Bridge(Controller) and data(Model) mapping code
    @Override
    public void onBindViewHolder(HomepageRecyclerViewAdapter.HomepageRecyclerViewViewHolder holder, int position) {
        if(holder.HolderId == 0){
            holder.mSlideImageAdapter = new SlideImageAdapter(mContext, mFragmentManager,
                    mTotalNews.getTotalNewsArrayList().get(0).getTopStories());
            holder.mAutoScrollViewPager.setAdapter(holder.mSlideImageAdapter);
            //Bind the viewPager indicator to the adapter
            holder.mCirclePageIndicator.setViewPager(holder.mAutoScrollViewPager);
            holder.mAutoScrollViewPager.setInterval(5000);
            holder.mAutoScrollViewPager.startAutoScroll();
        }
        else if(holder.HolderId == 1){
            int dateIndex = position;
            if(position == 1){
                holder.mTimeTitleTextView.setText(mTotalNews.getTotalNewsArrayList().get(position-1).getDate());
            }
            if(position > 1){
                if(mDateMap.containsKey(position)){
                    holder.mTimeTitleTextView.setText(mTotalNews.getTotalNewsArrayList()
                            .get(mDateMap.get(position)).getDate());
                }
                else {
                    for(int i = 1; i<mTotalNewSize; i++){
                        dateIndex -= mTotalNews.getTotalNewsArrayList().get(i-1).getStories().size();
                    }
                    dateIndex = dateIndex-1;
                    //use mDateMap to store datePosition-dateIndex key-value pairs
                    mDateMap.put(position, dateIndex);
                    holder.mTimeTitleTextView.setText(mTotalNews.getTotalNewsArrayList()
                            .get(mDateMap.get(position)).getDate());
                }
            }
        }
        else {
            int itemIndex = position;
            if(position>1 && position<mTotalNews.getTotalNewsArrayList().get(0).getStories().size()+2){
                holder.mItemTextTextView.setText(mTotalNews.getTotalNewsArrayList().get(0).getStories()
                        .get(position - 2).getTitle());

                Picasso.with(mContext)
                        .load(mTotalNews.getTotalNewsArrayList().get(0).getStories().get(position-2)
                                .getImageStringUri())
                        .into(holder.mItemIconImageView);

                if(mTotalNews.getTotalNewsArrayList().get(0).getStories().get(position-2).isMultipic()){
                    holder.mMultipicLL.setVisibility(View.VISIBLE);
                }else if(mTotalNews.getTotalNewsArrayList().get(0).getStories().get(position-2)
                        .getTitle().contains("多图")){
                    holder.mMultipicLL.setVisibility(View.VISIBLE);
                }else {
                    holder.mMultipicLL.setVisibility(View.INVISIBLE);
                }
            }
            else {
                if(mTimeMap.containsKey(position) && mItemMap.containsKey(position)) {
                    holder.mItemTextTextView.setText(mTotalNews.getTotalNewsArrayList()
                            .get(mDateMap.get(mTimeMap.get(position)))
                            .getStories().get(mItemMap.get(position)).getTitle());

                    Picasso.with(mContext)
                            .load(mTotalNews.getTotalNewsArrayList()
                                    .get(mDateMap.get(mTimeMap.get(position))).getStories()
                                    .get(mItemMap.get(position)).getImageStringUri())
                            .into(holder.mItemIconImageView);

                    if(mTotalNews.getTotalNewsArrayList().get(mDateMap.get(mTimeMap.get(position))).getStories()
                            .get(mItemMap.get(position)).isMultipic()){
                        holder.mMultipicLL.setVisibility(View.VISIBLE);
                    }else if(mTotalNews.getTotalNewsArrayList().get(mDateMap.get(mTimeMap.get(position))).getStories()
                            .get(mItemMap.get(position)).getTitle().contains("多图")){
                        holder.mMultipicLL.setVisibility(View.VISIBLE);
                    }else {
                        holder.mMultipicLL.setVisibility(View.INVISIBLE);
                    }
                }
                else {
                    int timePosition = 1;
                    for(int i = 1; i<mTotalNewSize; i++){
                        timePosition += mTotalNews.getTotalNewsArrayList().get(i-1).getStories().size()+1;
                    }
                    //use mTimeMap to store position-datePosition key-value pairs
                    mTimeMap.put(position, timePosition);

                    for(int j = 1; j<mTotalNewSize; j++){
                            itemIndex -= mTotalNews.getTotalNewsArrayList().get(j-1).getStories().size();
                        }
                    itemIndex = itemIndex-mTotalNewSize-1;
                    //use mItemMap to store position-itemIndex key-value pairs
                    mItemMap.put(position, itemIndex);

                    holder.mItemTextTextView.setText(mTotalNews.getTotalNewsArrayList()
                            .get(mDateMap.get(mTimeMap.get(position)))
                            .getStories().get(mItemMap.get(position)).getTitle());

                    Picasso.with(mContext)
                            .load(mTotalNews.getTotalNewsArrayList()
                                    .get(mDateMap.get(mTimeMap.get(position))).getStories()
                                    .get(mItemMap.get(position)).getImageStringUri())
                            .into(holder.mItemIconImageView);

                    if(mTotalNews.getTotalNewsArrayList().get(mDateMap.get(mTimeMap.get(position))).getStories()
                            .get(mItemMap.get(position)).isMultipic()){
                        holder.mMultipicLL.setVisibility(View.VISIBLE);
                    }else if(mTotalNews.getTotalNewsArrayList().get(mDateMap.get(mTimeMap.get(position))).getStories()
                            .get(mItemMap.get(position)).getTitle().contains("多图")){
                        holder.mMultipicLL.setVisibility(View.VISIBLE);
                    }else {
                        holder.mMultipicLL.setVisibility(View.INVISIBLE);
                    }
                }
            }
        }
    }


    //  use position to check which ViewType should ViewHolder create
    //  position = 0 ,          ViewType = TYPE_VIEWPAGER;      HolderId = 0
    //  position = 1 ,          ViewType = TYPE_TIMETITLE;      HolderId = 1
    //  position = 2 ,          ViewType = TYPE_CONTENT;        HolderId = 2
    //  and so on ...
    //  use HolderId as condition to bind corresponding data
    @Override
    public int getItemViewType(int position) {
        if(isPositionViewPager(position))
        {
            return TYPE_VIEWPAGER; //0
        }else if(isPositionTimeTitle(position)){
            return TYPE_TIMETITLE;//1
        }else {
            return TYPE_CONTENT;//2
        }
    }
    private boolean isPositionViewPager(int position) {
        return position == 0;
    }
    private boolean isPositionTimeTitle(int position) {
        int timePosition = 1;
        ArrayList<Integer> timePositionArrayList = new ArrayList<>();
        timePositionArrayList.add(timePosition);
        if(mTotalNewSize > 1){
            for(int i = 1; i<mTotalNewSize; i++){
                timePosition += mTotalNews.getTotalNewsArrayList().get(i-1).getStories().size()+1;
                timePositionArrayList.add(timePosition);
            }
        }
        return timePositionArrayList.contains(position);
    }

    @Override
    public int getItemCount() {
        int mItemCount;
        if(mTotalNewSize == 1){
            mItemCount = mTotalNews.getTotalNewsArrayList().get(0).getStories().size()+2;
        }
        else {
            mItemCount = mTotalNews.getTotalNewsArrayList().get(0).getStories().size()+2;
            for(int i = 1; i< mTotalNewSize; i++){
                mItemCount += mTotalNews.getTotalNewsArrayList().get(i).getStories().size()+1;
            }
        }
        return mItemCount;
    }


//    public void refill(ArrayList<Story> receivedStories){
//        mLatestNews.getStories().clear();
//        mLatestNews.getStories().addAll(receivedStories);
//        notifyDataSetChanged();
//    }

    public void add(TotalNews totalNews){
        mTotalNews = totalNews;
        mTotalNewSize = mTotalNews.getTotalNewsArrayList().size();
        notifyDataSetChanged();
    }

}