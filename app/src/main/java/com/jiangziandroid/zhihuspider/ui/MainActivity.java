package com.jiangziandroid.zhihuspider.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.jiangziandroid.zhihuspider.R;
import com.jiangziandroid.zhihuspider.adapter.SlideDrawerAdapter;
import com.jiangziandroid.zhihuspider.model.Theme;
import com.jiangziandroid.zhihuspider.model.Themes;
import com.parse.ParseUser;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends ActionBarActivity {

    @InjectView(R.id.tool_bar)  android.support.v7.widget.Toolbar mToolbar;
    @InjectView(R.id.profile_image) de.hdodenhof.circleimageview.CircleImageView mCircleImageView;
    @InjectView(R.id.profile_username) TextView mProfileTextView;
    @InjectView(R.id.RecyclerView) RecyclerView mRecyclerView; // Declaring RecyclerView
    @InjectView(R.id.DrawerLayout) DrawerLayout mDrawerLayout; // Declaring DrawerLayout
    protected Themes mThemes;
    protected SlideDrawerAdapter mSlideDrawerAdapter;       // Declaring Adapter For Recycler View
    protected RecyclerView.LayoutManager mLayoutManager;    // Declaring Layout Manager as a linear layout manager
    protected ActionBarDrawerToggle mDrawerToggle;          // Declaring Action Bar Drawer Toggle

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        setSupportActionBar(mToolbar);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
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

        //update RecyclerView
        getZhihuThemes();
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
        if (ParseUser.getCurrentUser() != null) {
            mProfileTextView.setText(ParseUser.getCurrentUser().getUsername());
            Toast.makeText(MainActivity.this, "Welcome! " + mProfileTextView.getText(), Toast.LENGTH_SHORT).show();
            mCircleImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //go to user info page
                }
            });
            mProfileTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //go to user info page
                }
            });
        } else {
            mProfileTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            });
        }
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
                    ParseUser.logOut();
                    mProfileTextView.setText(R.string.remind_user_login_text);
                    mProfileTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                    });
                    Toast.makeText(MainActivity.this, "Logout successfully!", Toast.LENGTH_SHORT).show();
                }
        }

        return super.onOptionsItemSelected(item);
    }


    public void getZhihuThemes() {
        String themesUrl = "http://news-at.zhihu.com/api/4/themes";
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
                            if (mRecyclerView.getAdapter() == null) {
                                //initial the adapter
                                mRecyclerView.setAdapter(mSlideDrawerAdapter);
                                mRecyclerView.setHasFixedSize(true);
                            }
                            else {
                                //refill the adapter
                                ((SlideDrawerAdapter) (mRecyclerView.getAdapter())).refill(mThemes.getThemes());
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
}

