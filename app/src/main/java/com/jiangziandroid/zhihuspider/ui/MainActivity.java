package com.jiangziandroid.zhihuspider.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jiangziandroid.zhihuspider.R;
import com.jiangziandroid.zhihuspider.adapter.HomepageRecyclerViewAdapter;
import com.jiangziandroid.zhihuspider.adapter.HomepageThemesRecyclerViewAdapter;
import com.jiangziandroid.zhihuspider.adapter.SlideDrawerAdapter;
import com.jiangziandroid.zhihuspider.model.LatestNews;
import com.jiangziandroid.zhihuspider.model.Story;
import com.jiangziandroid.zhihuspider.model.Theme;
import com.jiangziandroid.zhihuspider.model.ThemeContent;
import com.jiangziandroid.zhihuspider.model.ThemeContentEditor;
import com.jiangziandroid.zhihuspider.model.ThemeContentStory;
import com.jiangziandroid.zhihuspider.model.Themes;
import com.jiangziandroid.zhihuspider.model.TopStory;
import com.jiangziandroid.zhihuspider.model.TotalNews;
import com.jiangziandroid.zhihuspider.utils.ParseConstants;
import com.jiangziandroid.zhihuspider.utils.RecyclerItemClickListener;
import com.jiangziandroid.zhihuspider.utils.ZhihuAPI;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends ActionBarActivity implements SwipeRefreshLayout.OnRefreshListener{

    @InjectView(R.id.tool_bar)  android.support.v7.widget.Toolbar mToolbar;
    @InjectView(R.id.UserProfileRL) RelativeLayout mUserProfileRL;
    @InjectView(R.id.profile_image) de.hdodenhof.circleimageview.CircleImageView mCircleImageView;
    @InjectView(R.id.profile_username) TextView mProfileTextView;
    @InjectView(R.id.SlideDrawerRecyclerView) RecyclerView mSlideDrawerRecyclerView; // Declaring RecyclerView
    @InjectView(R.id.HomepageRecyclerView) RecyclerView mHomepageRecyclerView;
    @InjectView(R.id.DrawerLayout) DrawerLayout mDrawerLayout; // Declaring DrawerLayout
    @InjectView(R.id.SlidingDrawerRL) RelativeLayout mSlidingDrawerRL;
    @InjectView(R.id.slideDrawerHomeHeader)  RelativeLayout mHomePageRL;
    @InjectView(R.id.slideDrawerToolsHeader) LinearLayout mSlideDrawerToolsHeader;
    @InjectView(R.id.collectionLL) LinearLayout mCollectionLL;
    @InjectView(R.id.messageLL) LinearLayout mMessageLL;
    @InjectView(R.id.settingsLL) LinearLayout mSettingsLL;
    @InjectView(R.id.swipeRefreshLayout) SwipeRefreshLayout mSwipeRefreshLayout;
    protected Themes mThemes;
    protected LatestNews mLatestNews;
    protected SlideDrawerAdapter mSlideDrawerAdapter;       // Declaring Adapter For Recycler View
    protected HomepageRecyclerViewAdapter mHomepageRecyclerViewAdapter;
    protected HomepageThemesRecyclerViewAdapter mHomepageThemesRecyclerViewAdapter;
    protected RecyclerView.LayoutManager mSlideDrawerLayoutManager;    // Declaring Layout Manager as a linear layout manager
    protected LinearLayoutManager mHomepageLayoutManager;
    protected ActionBarDrawerToggle mDrawerToggle;          // Declaring Action Bar Drawer Toggle
    protected FragmentManager mFragmentManager;
    protected RecyclerItemClickListener mRecyclerItemClickListener;

    protected ParseUser mCurrentUser;
    protected TotalNews mTotalNews;
    protected ArrayList<LatestNews> mNewsArrayList;
    protected LatestNews mYesterdayNews;
    private long mLongDate;
    private ThemeContent mThemeContent;
    private int mThemeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        setSupportActionBar(mToolbar);
        mSlideDrawerLayoutManager = new LinearLayoutManager(this);
        mSlideDrawerRecyclerView.setLayoutManager(mSlideDrawerLayoutManager);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.swipeRefresh1, R.color.swipeRefresh2, R.color.swipeRefresh3, R.color.swipeRefresh4);
        mHomepageLayoutManager = new LinearLayoutManager(this);
        mHomepageRecyclerView.setLayoutManager(mHomepageLayoutManager);
        // This drawable shows a Hamburger icon when drawer is closed and an arrow when drawer is open.
        // It animates between these two states as the drawer opens.
        mDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, mDrawerLayout, mToolbar,
                R.string.openDrawer, R.string.closeDrawer) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // code here will execute once the drawer is opened( As I don't want anything happened when drawer is
                // open I am not going to put anything here)
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                // Code here will execute once drawer is closed
            }
        };
        // Drawer Toggle Object Made
        mDrawerLayout.setDrawerListener(mDrawerToggle);  // Drawer Listener set to the Drawer toggle
        mDrawerToggle.syncState(); // Finally we set the drawer toggle sync State

        mHomePageRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Close the slidingDrawer and show the main page
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(R.string.title_activity_main);
                    getLatestNews();
                }
                mDrawerLayout.closeDrawer(mSlidingDrawerRL);
            }
        });
//        mSlideDrawerToolsHeader.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(MainActivity.this, "Reserved function!", Toast.LENGTH_SHORT).show();
//            }
//        });

        //Set slide drawer tools header onClickListener
        setSlideDrawerToolsHeaderOnClickListener();
        //update main page content
        getLatestNews();
        //update slide drawer themes
        getZhihuThemes();


        mHomepageRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if(getSupportActionBar().getTitle().equals("知乎小报 by 江子") &&
                        mHomepageLayoutManager.findLastCompletelyVisibleItemPosition() ==
                                mHomepageLayoutManager.getItemCount() - 1){
                    Toast.makeText(MainActivity.this, "Show last day's story!", Toast.LENGTH_LONG).show();
                    getHistoryNews();
                }
            }
        });


    }

    private void setSlideDrawerToolsHeaderOnClickListener() {
        mCollectionLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCurrentUser != null){
                    Toast.makeText(MainActivity.this, "Collection function!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, CollectionActivity.class);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(MainActivity.this, "Please login first!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }

        });
        mMessageLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCurrentUser != null){
                    Toast.makeText(MainActivity.this, "Message function!", Toast.LENGTH_SHORT).show();

                }
                else {
                    Toast.makeText(MainActivity.this, "Please login first!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        });
        mSettingsLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Settings function!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        //Check if user is logged in
        /** Whenever you use any signUp or login methods, the user is cached on disk.
         You can treat this cache as a session, and automatically assume the user is logged in
         It would be bothersome if the user had to log in every time they open your app.
         You can avoid this by using the cached currentUser object.
         **/
        mCurrentUser = ParseUser.getCurrentUser();
        if (mCurrentUser != null) {
            mProfileTextView.setText(mCurrentUser.getUsername());
            Picasso.with(this).load(mCurrentUser.getString(ParseConstants.KEY_USER_PHOTO_STRING_URI))
                    .resize(96, 96).centerCrop().into(mCircleImageView);
        }
        mCircleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCurrentUser == null){
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                }else {
                    Intent intent = new Intent(MainActivity.this, UserInfoActivity.class);
                    startActivity(intent);
                }
            }
        });
        mProfileTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mCurrentUser == null){
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }else {
                        Intent intent = new Intent(MainActivity.this, UserInfoActivity.class);
                        startActivity(intent);
                    }
                }
            });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
            int id = item.getItemId();
        switch (id) {
            case R.id.action_logout:
                if (ParseUser.getCurrentUser() != null) {
                    ParseUser.logOutInBackground(new LogOutCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                mProfileTextView.setText(R.string.remind_user_login_text);
                                mCircleImageView.setImageResource(R.drawable.ic_action_carema);
                                mProfileTextView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                    }
                                });
                                mCircleImageView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                    }
                                });
                                mCurrentUser = ParseUser.getCurrentUser();
                                if(mCurrentUser == null)
                                Toast.makeText(MainActivity.this, "Logout successfully!", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.e("Logout", "Error: " + e.getMessage());
                            }
                        }
                    });
                }else {
                    Toast.makeText(MainActivity.this, "Please login first!", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.action_calendar:
                Intent intent = new Intent(MainActivity.this, CalendarActivity.class);
                startActivity(intent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    public void getZhihuThemes() {
        String themesUrl = ZhihuAPI.API_THEMES;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(themesUrl).build();
        Call call = client.newCall(request);
        //Transfer synchronous to asynchronous
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
            }

            @Override
            public void onResponse(Response response) throws IOException {
                // execute() is synchronous method, so delete it
                // Response response = call.execute();
                String jsonData = response.body().string();
                try {
                    mThemes = getThemesDetails(jsonData);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mSlideDrawerAdapter = new SlideDrawerAdapter(MainActivity.this, mThemes);
                            if (mSlideDrawerRecyclerView.getAdapter() == null) {
                                //initial the adapter
                                mSlideDrawerRecyclerView.setAdapter(mSlideDrawerAdapter);
                                mSlideDrawerRecyclerView.setHasFixedSize(true);
                                //Set the theme's item click listener
                                mRecyclerItemClickListener = new RecyclerItemClickListener(MainActivity.this,
                                        new RecyclerItemClickListener.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(View view, int position) {
                                                if(getSupportActionBar()!=null){
                                                    getSupportActionBar().setTitle(mThemes.getThemes().get(position).getThemeName());
                                                }
                                                mThemeId = mThemes.getThemes().get(position).getThemeId();
                                                mDrawerLayout.closeDrawer(mSlidingDrawerRL);
                                                //Refresh main content
                                                getThemeContent(mThemes.getThemes().get(position).getThemeId());
                                            }
                                        });
                                mSlideDrawerRecyclerView.addOnItemTouchListener(mRecyclerItemClickListener);
                            } else {
                                //refill the adapter
                                ((SlideDrawerAdapter) (mSlideDrawerRecyclerView.getAdapter())).refill(mThemes.getThemes());
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            private Themes getThemesDetails(String jsonData) throws JSONException {
                Themes themes = new Themes();
                themes.setThemes(getThemeDetails(jsonData));
                return themes;
            }

            private ArrayList<Theme> getThemeDetails(String jsonData) throws JSONException {
                JSONObject themes = new JSONObject(jsonData);
                JSONArray others = themes.getJSONArray("others");
                ArrayList<Theme> themeArray = new ArrayList<>();
                for (int i = 0; i < others.length(); i++) {
                    JSONObject jsonTheme = others.getJSONObject(i);
                    Theme theme = new Theme();
                    theme.setThemeId(jsonTheme.getInt("id"));
                    theme.setThemeName(jsonTheme.getString("name"));
                    theme.setThumbnailUri(jsonTheme.getString("thumbnail"));
                    theme.setDescription(jsonTheme.getString("description"));
                    themeArray.add(theme);
                }
                return themeArray;
            }
        });
    }

    private void getThemeContent(int themeId) {
        String themeContentUrl = ZhihuAPI.API_THEME_CONTENT + String.valueOf(themeId);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(themeContentUrl).build();
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
                try {
                    mThemeContent = getThemeContentDetails(jsonData);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(mSwipeRefreshLayout.isRefreshing()){
                                mSwipeRefreshLayout.setRefreshing(false);
                            }
                            mHomepageThemesRecyclerViewAdapter = new HomepageThemesRecyclerViewAdapter(MainActivity.this,
                                    mThemeContent);
                            mHomepageRecyclerView.setAdapter(mHomepageThemesRecyclerViewAdapter);
                            mHomepageRecyclerView.setHasFixedSize(true);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private ThemeContent getThemeContentDetails(String jsonData) throws JSONException {
        ThemeContent themeContent = new ThemeContent();
        JSONObject jsonThemeContent = new JSONObject(jsonData);
        themeContent.setThemeName(jsonThemeContent.getString("name"));
        themeContent.setThemeDescription(jsonThemeContent.getString("description"));
        themeContent.setThemeBackgroundUrl(jsonThemeContent.getString("background"));
        themeContent.setEditorsArrayList(getEditorsDetails(jsonThemeContent));
        themeContent.setThemeStoriesArrayList(getThemeStoriesDetails(jsonThemeContent));
        return themeContent;
    }

    private ArrayList<ThemeContentEditor> getEditorsDetails(JSONObject jsonThemeContent) throws JSONException {
        ArrayList<ThemeContentEditor> themeContentEditorArrayList = new ArrayList<>();
        JSONArray jsonArrayEditors = jsonThemeContent.getJSONArray("editors");
        for(int i = 0; i<jsonArrayEditors.length(); i++){
            ThemeContentEditor themeContentEditor = new ThemeContentEditor();
            JSONObject jsonEditor = jsonArrayEditors.getJSONObject(i);
            themeContentEditor.setEditorId(jsonEditor.getInt("id"));
            themeContentEditor.setEditorName(jsonEditor.getString("name"));
            themeContentEditor.setEditorAvatarUrl(jsonEditor.getString("avatar"));
            if(jsonEditor.has("bio")){
                themeContentEditor.setEditorBio(jsonEditor.getString("bio"));
            }
            if(jsonEditor.has("url")){
                themeContentEditor.setEditorUrl(jsonEditor.getString("url"));
            }
            themeContentEditorArrayList.add(themeContentEditor);
        }
        return themeContentEditorArrayList;
    }

    private ArrayList<ThemeContentStory> getThemeStoriesDetails(JSONObject jsonThemeContent) throws JSONException {
        ArrayList<ThemeContentStory> themeContentStoryArrayList = new ArrayList<>();
        JSONArray jsonArrayStories = jsonThemeContent.getJSONArray("stories");
        for(int i = 0; i<jsonArrayStories.length(); i++){
            ThemeContentStory themeContentStory = new ThemeContentStory();
            JSONObject jsonStory = jsonArrayStories.getJSONObject(i);
            themeContentStory.setThemeContentStoryId(jsonStory.getLong("id"));
            themeContentStory.setThemeContentStoryTitle(jsonStory.getString("title"));
            themeContentStory.setThemeContentStoryType(jsonStory.getInt("type"));
            if(jsonStory.has("images")) {
                themeContentStory.setImagesUrl(jsonStory.getJSONArray("images").getString(0));
            }
            themeContentStoryArrayList.add(themeContentStory);
        }
        return themeContentStoryArrayList;
    }


    public void getHistoryNews(){
        String oneDayNewsUri = ZhihuAPI.API_HISTORY_NEWS + mLongDate;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(oneDayNewsUri).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }
            @Override
            public void onResponse(Response response) throws IOException {
                String jsonData = response.body().string();
                Log.e(getApplication().getPackageName(), "Get history data successfully! ^.^");
                try {
                    mYesterdayNews = getNewsDetails(jsonData);
                    mLongDate = Long.parseLong(mYesterdayNews.getDate());
                    //instead of mLongDate = mLongDate-1;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mNewsArrayList.add(mYesterdayNews);
                            mTotalNews.setTotalNewsArrayList(mNewsArrayList);
                            ((HomepageRecyclerViewAdapter) (mHomepageRecyclerView.getAdapter())).
                                    add(mTotalNews);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void getLatestNews(){
        String latestNewsUri = ZhihuAPI.API_LATEST_NEWS;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(latestNewsUri).build();
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
                try {
                    mLatestNews = getNewsDetails(jsonData);
                    mLongDate = Long.parseLong(mLatestNews.getDate());
                    mTotalNews = new TotalNews();
                    mNewsArrayList = new ArrayList<>();
                    mNewsArrayList.add(mLatestNews);
                    mTotalNews.setTotalNewsArrayList(mNewsArrayList);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mSwipeRefreshLayout.isRefreshing()) {
                                mSwipeRefreshLayout.setRefreshing(false);
                                Log.e(getApplication().getPackageName(), "Get data successfully! ^.^");
                            }
                            mFragmentManager = getSupportFragmentManager();
                            mHomepageRecyclerViewAdapter = new HomepageRecyclerViewAdapter(MainActivity.this,
                                    mTotalNews, mFragmentManager);
//                            if (mHomepageRecyclerView.getAdapter() == null) {
                            //initial the adapter
                            mHomepageRecyclerView.setAdapter(mHomepageRecyclerViewAdapter);
                            mHomepageRecyclerView.setHasFixedSize(true);
//                            }
//                            else {
//                                //refill the adapter
//                                ((HomepageRecyclerViewAdapter) (mHomepageRecyclerView.getAdapter())).
//                                        refill(mLatestNews.getStories());
//                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        });
    }


    private LatestNews getNewsDetails(String jsonData) throws JSONException {
        LatestNews latestNews = new LatestNews();
        latestNews.setDate(getDate(jsonData));
        latestNews.setTopStories(getTopStoriesDetails(jsonData));
        latestNews.setStories(getStoriesDetails(jsonData));
        return latestNews;
    }

    private String getDate(String jsonData) throws JSONException {
        JSONObject latestNews = new JSONObject(jsonData);
        return latestNews.getString("date");
    }

    private ArrayList<TopStory> getTopStoriesDetails(String jsonData) throws JSONException {
        JSONObject latestNews = new JSONObject(jsonData);
        if(latestNews.has("top_stories")){
            JSONArray top_stories = latestNews.getJSONArray("top_stories");
            ArrayList<TopStory> topStoriesArray = new ArrayList<>();
            for (int i =0 ; i<top_stories.length(); i++){
                JSONObject jsonTopStory = top_stories.getJSONObject(i);
                TopStory topStory = new TopStory();
                topStory.setStoryId(jsonTopStory.getLong("id"));
                topStory.setImageStringUri(jsonTopStory.getString("image"));
                topStory.setTitle(jsonTopStory.getString("title"));
                topStoriesArray.add(topStory);
            }
            return topStoriesArray;
        }
        else return null;
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


    @Override
    public void onRefresh() {
        if(getSupportActionBar()!=null){
            if(getSupportActionBar().getTitle().equals("知乎小报 by 江子")) {
                getLatestNews();
            }
            else {
                getThemeContent(mThemeId);
            }
        }

    }

}

