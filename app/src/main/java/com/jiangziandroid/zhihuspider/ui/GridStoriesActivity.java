package com.jiangziandroid.zhihuspider.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.jiangziandroid.zhihuspider.R;
import com.jiangziandroid.zhihuspider.adapter.CardViewStoriesAdapter;
import com.jiangziandroid.zhihuspider.model.LatestNews;
import com.jiangziandroid.zhihuspider.model.Story;
import com.jiangziandroid.zhihuspider.utils.ZhihuAPI;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class GridStoriesActivity extends Activity implements SwipeRefreshLayout.OnRefreshListener{
    @InjectView(R.id.BackImageView) ImageView mBackImageView;
    @InjectView(R.id.swipeRefreshLayout) SwipeRefreshLayout mSwipeRefreshLayout;
    @InjectView(R.id.GridStoriesRecyclerView) RecyclerView mStoriesGridRecyclerView;
    @InjectView(R.id.placeHolderTextView) TextView mPlaceHolderTextView;
    @InjectView(R.id.dateTextView) TextView mDateTextView;
    @InjectView(R.id.LastDayButton) Button mLastDayButton;
    @InjectView(R.id.NextDayButton) Button mNextDayButton;

    protected String mNewsAPI;          // for initialize and refresh stories
    protected String mLastDayNewsAPI;   // for previous day's stories
    protected String mNextDayNewsAPI;   // for next day's stories
    protected LatestNews mStories;
    protected GridLayoutManager mGridLayoutManager;
    protected LinearLayoutManager mLinearLayoutManager;
    protected CardViewStoriesAdapter mCardViewStoriesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_stories);
        ButterKnife.inject(this);
        //Set AppBar OnClickListener
        setAppBarOnClickListener();
        //SetOnRefreshListener
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.swipeRefresh1, R.color.swipeRefresh2, R.color.swipeRefresh3, R.color.swipeRefresh4);
        //Set GridLayoutManager
        mGridLayoutManager = new GridLayoutManager(this, 1);
        mStoriesGridRecyclerView.setLayoutManager(mGridLayoutManager);
        //Set LinearLayoutManager
//        mLinearLayoutManager = new LinearLayoutManager(this);
//        mStoriesGridRecyclerView.setLayoutManager(mLinearLayoutManager);
        //Get NewsAPI from Intent
        mNewsAPI = getIntent().getStringExtra("NewsAPI");
        getStories(mNewsAPI);

    }


    private void getStories(String newsAPI) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(newsAPI).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                if(mSwipeRefreshLayout.isRefreshing()){
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onResponse(Response response) throws IOException {
                String jsonData = response.body().string();
                if(jsonData.equals("")){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(mSwipeRefreshLayout.isRefreshing()){
                                mSwipeRefreshLayout.setRefreshing(false);
                            }
                            mDateTextView.setText("No today's stories now!");
                            mPlaceHolderTextView.setVisibility(View.VISIBLE);
                        }
                    });
                }
                else {
                    try {
                        mStories = getNewsDetails(jsonData);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(mSwipeRefreshLayout.isRefreshing()){
                                    mSwipeRefreshLayout.setRefreshing(false);
                                    Log.e(getApplication().getPackageName(), "Get data successfully! ^.^");
                                }
                                //Check if need to disable Button display
                                if20130519DisableLastDayButton();
                                ifTodayDisableNextDayButton();
                                mDateTextView.setText(mStories.getDate());
                                mPlaceHolderTextView.setVisibility(View.INVISIBLE);
                                //Set CardViewStoriesAdapter
                                mCardViewStoriesAdapter = new CardViewStoriesAdapter
                                                                (GridStoriesActivity.this, mStories);
                                mStoriesGridRecyclerView.setAdapter(mCardViewStoriesAdapter);
                                mStoriesGridRecyclerView.setHasFixedSize(true);
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    private void if20130519DisableLastDayButton(){
        if(mStories.getDate().equals("20130519")){
            mLastDayButton.setVisibility(View.INVISIBLE);
        }else {
            mLastDayButton.setVisibility(View.VISIBLE);
        }
    }

    private void ifTodayDisableNextDayButton() {
        //Minus 1 day to current day
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new GregorianCalendar().getTime());
        calendar.add(Calendar.DATE, -1);
        Date minusDate = calendar.getTime();
        String minusStringDate = formatter.format(minusDate);
        if(mStories.getDate().equals(minusStringDate)){
            mNextDayButton.setVisibility(View.INVISIBLE);
        }else {
            mNextDayButton.setVisibility(View.VISIBLE);
        }
    }

    private LatestNews getNewsDetails(String jsonData) throws JSONException {
        LatestNews latestNews = new LatestNews();
        latestNews.setDate(getDate(jsonData));
        //latestNews.setTopStories(getTopStoriesDetails(jsonData));
        latestNews.setStories(getStoriesDetails(jsonData));
        return latestNews;
    }

    private String getDate(String jsonData) throws JSONException {
        JSONObject latestNews = new JSONObject(jsonData);
        return latestNews.getString("date");
    }

    private ArrayList<Story> getStoriesDetails(String jsonData) throws JSONException {
        JSONObject latestNews = new JSONObject(jsonData);
        JSONArray stories = latestNews.getJSONArray("stories");
        ArrayList<Story> storiesArray = new ArrayList<>();
        for (int i =0 ; i<stories.length(); i++){
            JSONObject jsonStory = stories.getJSONObject(i);
            Story story = new Story();
            story.setStoryId(jsonStory.getLong("id"));
            story.setTitle(jsonStory.getString("title"));
            story.setImageStringUri(jsonStory.getJSONArray("images").getString(0));
            if(jsonStory.has("multipic")){
                story.setMultipic(true);
            }else {
                story.setMultipic(false);
            }
            storiesArray.add(story);
        }
        return storiesArray;
    }


    private void setAppBarOnClickListener() {
        mBackImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mLastDayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLastDayNewsAPI = ZhihuAPI.API_HISTORY_NEWS + mStories.getDate();
                getStories(mLastDayNewsAPI);
            }
        });
        mNextDayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Increase 2 day to current story day
                SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
                //Use formatter.parse() to convert a String date to Date
                try {
                    Date storyDate = formatter.parse(mStories.getDate());
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(storyDate);
                    calendar.add(Calendar.DATE, 2);
                    Date increasedDate = calendar.getTime();
                    String stringIncreasedDate = formatter.format(increasedDate);
                    mNextDayNewsAPI = ZhihuAPI.API_HISTORY_NEWS + stringIncreasedDate;
                    getStories(mNextDayNewsAPI);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_grid_stories, menu);
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
        //Increase 1 day to current story day
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        //Use formatter.parse() to convert a String date to Date
        try {
            Date storyDate = formatter.parse(mStories.getDate());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(storyDate);
            calendar.add(Calendar.DATE, 1);
            Date increasedDate = calendar.getTime();
            String stringIncreasedDate = formatter.format(increasedDate);
            mNewsAPI = ZhihuAPI.API_HISTORY_NEWS + stringIncreasedDate;
            getStories(mNewsAPI);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
