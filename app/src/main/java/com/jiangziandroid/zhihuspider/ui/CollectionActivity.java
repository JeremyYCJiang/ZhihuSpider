package com.jiangziandroid.zhihuspider.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.jiangziandroid.zhihuspider.R;
import com.jiangziandroid.zhihuspider.adapter.CollectionRecyclerViewAdapter;
import com.jiangziandroid.zhihuspider.model.Story;
import com.jiangziandroid.zhihuspider.utils.ParseConstants;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class CollectionActivity extends Activity implements SwipeRefreshLayout.OnRefreshListener{

    @InjectView(R.id.BackImageView) ImageView mBackImageView;
    @InjectView(R.id.CollectionRecyclerView) RecyclerView mCollectionRecyclerView;
    @InjectView(R.id.swipeRefreshLayout) SwipeRefreshLayout mSwipeRefreshLayout;
    protected LinearLayoutManager mCollectionLayoutManager;
    protected CollectionRecyclerViewAdapter mCollectionRecyclerViewAdapter;
    protected ParseUser mCurrentUser;
    protected ParseRelation<ParseObject> mFavouriteRelation;
    protected Story mStory;
    protected ArrayList<Story> mStoryArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);
        ButterKnife.inject(this);
        setAppBarOnClickListener();
        //set layout manager
        mCollectionLayoutManager = new LinearLayoutManager(this);
        mCollectionRecyclerView.setLayoutManager(mCollectionLayoutManager);
        //set swipe refresh
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.swipeRefresh1, R.color.swipeRefresh2, R.color.swipeRefresh3, R.color.swipeRefresh4);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCurrentUser = ParseUser.getCurrentUser();
        if(mCurrentUser != null){
            getCollections();
        }
    }

    private void setAppBarOnClickListener() {
        mBackImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private void getCollections() {
        mFavouriteRelation = mCurrentUser.getRelation(ParseConstants.KEY_FAVOURITE_STORY_RELATION);
        mFavouriteRelation.getQuery().findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if(e != null){
                    //set swipe refresh stop
                    if(mSwipeRefreshLayout.isRefreshing()){
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                    Log.e("FindAllRelation", "Error: " + e.getMessage());
                }
                else {
                    mStoryArrayList = new ArrayList<>();
                    for(int i = 0; i<list.size(); i++){
                        mStory = new Story();
                        mStory.setStoryId(Long.parseLong(list.get(i).getString("storyId")));
                        mStory.setTitle(list.get(i).getString("storyTitle"));
                        mStory.setImageStringUri(list.get(i).getString("storyImageUrl"));
                        mStoryArrayList.add(mStory);
                    }
                    //set swipe refresh stop
                    if(mSwipeRefreshLayout.isRefreshing()){
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                    //Set an adapter
                    if(mCollectionRecyclerView.getAdapter() == null){
                        mCollectionRecyclerViewAdapter = new CollectionRecyclerViewAdapter(
                                CollectionActivity.this, mStoryArrayList);
                        mCollectionRecyclerView.setAdapter(mCollectionRecyclerViewAdapter);
                        mCollectionRecyclerView.setHasFixedSize(true);
                    }else {
                        ((CollectionRecyclerViewAdapter)mCollectionRecyclerView.getAdapter())
                                .refill(mStoryArrayList);
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_collection, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onRefresh() {
        getCollections();
    }
}
