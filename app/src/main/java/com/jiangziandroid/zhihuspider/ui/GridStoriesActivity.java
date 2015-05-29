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
                                ifLatestIsLastdayNews();
                                if20130519DisableLastDayButton();
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


    private void ifLatestIsLastdayNews(){
        //today
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        String stringCurrentDate = formatter.format(new GregorianCalendar().getTime());
        //last day
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(new GregorianCalendar().getTime());
        calendar1.add(Calendar.DATE, -1);
        Date minusedDate = calendar1.getTime();
        String stringMinusedDate = formatter.format(minusedDate);
        //next day
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(new GregorianCalendar().getTime());
        calendar2.add(Calendar.DATE, 1);
        Date increasedDate = calendar2.getTime();
        String stringIncreasedDate = formatter.format(increasedDate);

            //case 1 : today's news has not been created
        if(mNewsAPI.contains("latest") && !mStories.getDate().equals(stringCurrentDate)){
            mDateTextView.setText(stringCurrentDate);
            mNextDayButton.setVisibility(View.INVISIBLE);
            mPlaceHolderTextView.setVisibility(View.VISIBLE);
        }
            //case 2 : today's news has been created
        else if(mNewsAPI.contains("latest") && mStories.getDate().equals(stringCurrentDate)){
            mDateTextView.setText(mStories.getDate());
            mNextDayButton.setVisibility(View.INVISIBLE);
            mPlaceHolderTextView.setVisibility(View.INVISIBLE);
        }
            //case 3 : after press nextDay button there has today's news
        else if(!mNewsAPI.contains("latest") && mNewsAPI.contains(stringIncreasedDate)
                && mStories.getDate().equals(stringCurrentDate)){
            mDateTextView.setText(mStories.getDate());
            mNextDayButton.setVisibility(View.INVISIBLE);
            mPlaceHolderTextView.setVisibility(View.INVISIBLE);
        }
            //case 4 : after press nextDay button there has not today's news
        else if(!mNewsAPI.contains("latest") && mNewsAPI.contains(stringIncreasedDate)
                && mStories.getDate().equals(stringMinusedDate)){
            mDateTextView.setText(stringCurrentDate);
            mNextDayButton.setVisibility(View.INVISIBLE);
            mPlaceHolderTextView.setVisibility(View.VISIBLE);
        }
            //normal case
        else{
            mDateTextView.setText(mStories.getDate());
            mNextDayButton.setVisibility(View.VISIBLE);
            mPlaceHolderTextView.setVisibility(View.INVISIBLE);
        }
    }

    private void if20130519DisableLastDayButton(){
        if(mStories.getDate().equals("20130519")){
            mLastDayButton.setVisibility(View.GONE);
            mDateTextView.setText("小报生日 " + mStories.getDate());
        }else {
            mLastDayButton.setVisibility(View.VISIBLE);
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
                //today
                SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
                String stringCurrentDate = formatter.format(new GregorianCalendar().getTime());
                //last day
                Calendar calendar1 = Calendar.getInstance();
                calendar1.setTime(new GregorianCalendar().getTime());
                calendar1.add(Calendar.DATE, -1);
                Date minusedDate = calendar1.getTime();
                String stringMinusedDate = formatter.format(minusedDate);
                //next day
                Calendar calendar2 = Calendar.getInstance();
                calendar2.setTime(new GregorianCalendar().getTime());
                calendar2.add(Calendar.DATE, 1);
                Date increasedDate = calendar2.getTime();
                String stringIncreasedDate = formatter.format(increasedDate);
                    //case 1 click:
                if(mNewsAPI.contains("latest") && !mStories.getDate().equals(stringCurrentDate)){
                    mNewsAPI = ZhihuAPI.API_HISTORY_NEWS + stringCurrentDate;
                    getStories(mNewsAPI);
                }
                    //case 4 click:
                else if(!mNewsAPI.contains("latest") && mNewsAPI.contains(stringIncreasedDate)
                        && mStories.getDate().equals(stringMinusedDate)){
                    mNewsAPI = ZhihuAPI.API_HISTORY_NEWS + stringCurrentDate;
                    getStories(mNewsAPI);
                }
                    //case 2 , 3 and normal case click:
                else {
                    mNewsAPI = ZhihuAPI.API_HISTORY_NEWS + mStories.getDate();
                    getStories(mNewsAPI);
                }
            }
        });
        mNextDayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Increase 2 day to current story day
                SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
                //Use formatter.parse() to convert a String date to Date
                try {
                    //next 2 days
                    Date storyDate = formatter.parse(mStories.getDate());
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(storyDate);
                    calendar.add(Calendar.DATE, 2);
                    Date increasedDate = calendar.getTime();
                    String stringIncreasedDate = formatter.format(increasedDate);
                    mNewsAPI = ZhihuAPI.API_HISTORY_NEWS + stringIncreasedDate;
                    getStories(mNewsAPI);
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
