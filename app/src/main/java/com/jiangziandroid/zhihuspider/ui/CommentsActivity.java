package com.jiangziandroid.zhihuspider.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jiangziandroid.zhihuspider.R;
import com.jiangziandroid.zhihuspider.adapter.CommentsViewPagerAdapter;
import com.jiangziandroid.zhihuspider.model.NewsExtras;
import com.jiangziandroid.zhihuspider.utils.ZhihuAPI;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class CommentsActivity extends FragmentActivity {
    @InjectView(R.id.BackImageView) ImageView mBackImageView;
    @InjectView(R.id.dateTextView) TextView mTotalCommentsTextView;
    @InjectView(R.id.ViewPager) ViewPager mViewPager;
    @InjectView(R.id.SlidingTabs) SlidingTabLayout mSlidingTabs;
    protected CommentsViewPagerAdapter mCommentsViewPagerAdapter;
    protected long mStoryId;
    protected NewsExtras mNewsExtras;
    protected String[] mViewPagerTabTitles;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        ButterKnife.inject(this);
        setAppBarOnClickListener();
        getStoryId();
        getNewsExtras();
    }


    private void getStoryId() {
        Intent intent = getIntent();
        mStoryId = intent.getLongExtra("StoryId", 0);
    }

    private void setAppBarOnClickListener() {
        mBackImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getNewsExtras() {
        String newsExtrasUrl = ZhihuAPI.API_NEWS_EXTRAS + mStoryId;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(newsExtrasUrl).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }
            @Override
            public void onResponse(Response response) throws IOException {
                String jsonData = response.body().string();
                try {
                    mNewsExtras = getNewsExtrasDetails(jsonData);
                    mViewPagerTabTitles = new String[2];
                    mViewPagerTabTitles[0] = mNewsExtras.getLongComments()+"条长评";
                    mViewPagerTabTitles[1] = mNewsExtras.getShortComments()+"条短评";
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mTotalCommentsTextView.setText(String.valueOf(mNewsExtras.getComments()));
                            // Creating The ViewPagerAdapter and Passing Fragment Manager
                            mCommentsViewPagerAdapter = new CommentsViewPagerAdapter(CommentsActivity.this,
                                    mStoryId, mViewPagerTabTitles, getSupportFragmentManager());
                            //setting the adapter
                            mViewPager.setAdapter(mCommentsViewPagerAdapter);
                            // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width
                            mSlidingTabs.setDistributeEvenly(true);
                            // Setting Custom TabView layout
                            mSlidingTabs.setCustomTabView(R.layout.custom_tab_title, R.id.tabText);
                            // Setting Custom Color for the Scroll bar indicator of the Tab View
                            mSlidingTabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
                                @Override
                                public int getIndicatorColor(int position) {
                                    return getResources().getColor(R.color.ColorPrimaryDark);
                                }
                            });
                            // Setting the ViewPager For the SlidingTabsLayout
                            mSlidingTabs.setViewPager(mViewPager);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private NewsExtras getNewsExtrasDetails(String jsonData) throws JSONException {
        NewsExtras newsExtras = new NewsExtras();
        JSONObject jsonExtras = new JSONObject(jsonData);
        newsExtras.setComments(jsonExtras.getInt("comments"));
        newsExtras.setLongComments(jsonExtras.getInt("long_comments"));
        newsExtras.setShortComments(jsonExtras.getInt("short_comments"));
        newsExtras.setPopularity(jsonExtras.getInt("popularity"));
        return newsExtras;
    }




}
