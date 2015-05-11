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
import com.parse.ParseUser;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends ActionBarActivity{

    @InjectView(R.id.tool_bar) android.support.v7.widget.Toolbar mToolbar;
    @InjectView(R.id.profile_image) de.hdodenhof.circleimageview.CircleImageView mCircleImageView;
    @InjectView(R.id.profile_username) TextView mProfileTextView;

    //First We Declare Titles And Icons For Our Navigation Drawer List View
    //This Icons And Titles Are holded in an Array as you can see

    String TITLES[] = {"Home","Events","Mail","Shop","Travel"};
    int ICONS[] = {R.drawable.ic_action_search, R.drawable.ic_action_search, R.drawable.ic_action_search,
            R.drawable.ic_action_search, R.drawable.ic_action_search,};

    //Similarly we Create a String Resource for the name and email in the header view
    //And we also create a int resource for profile picture in the header view

    String NAME = "Akash Bangad";
    String EMAIL = "akash.bangad@android4devs.com";
    int PROFILE = R.drawable.abc_ic_menu_copy_mtrl_am_alpha;

    RecyclerView mRecyclerView;                           // Declaring RecyclerView
    RecyclerView.Adapter mAdapter;                        // Declaring Adapter For Recycler View
    RecyclerView.LayoutManager mLayoutManager;            // Declaring Layout Manager as a linear layout manager
    DrawerLayout Drawer;                                  // Declaring DrawerLayout


    ActionBarDrawerToggle mDrawerToggle;                  // Declaring Action Bar Drawer Toggle




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        setSupportActionBar(mToolbar);

        mRecyclerView = (RecyclerView) findViewById(R.id.RecyclerView); // Assigning the RecyclerView Object to the xml View

        mRecyclerView.setHasFixedSize(true);                            // Letting the system know that the list objects are of fixed size

        mAdapter = new SlideDrawerAdapter(TITLES,ICONS,NAME,EMAIL,PROFILE);       // Creating the Adapter of MyAdapter class(which we are going to see in a bit)
        // And passing the titles,icons,header view name, header view email,
        // and header view profile picture

        mRecyclerView.setAdapter(mAdapter);                              // Setting the adapter to RecyclerView

        mLayoutManager = new LinearLayoutManager(this);                 // Creating a layout Manager

        mRecyclerView.setLayoutManager(mLayoutManager);                 // Setting the layout Manager


        Drawer = (DrawerLayout) findViewById(R.id.DrawerLayout);        // Drawer object Assigned to the view
        mDrawerToggle = new ActionBarDrawerToggle(this,Drawer,mToolbar,R.string.openDrawer,R.string.closeDrawer){

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // code here will execute once the drawer is opened( As I dont want anything happened whe drawer is
                // open I am not going to put anything here)
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                // Code here will execute once drawer is closed
            }



        }; // Drawer Toggle Object Made
        Drawer.setDrawerListener(mDrawerToggle); // Drawer Listener set to the Drawer toggle
        mDrawerToggle.syncState();               // Finally we set the drawer toggle sync State


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
            Toast.makeText(MainActivity.this, "Welcome! "+ mProfileTextView.getText(), Toast.LENGTH_SHORT).show();
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
        }
        else{
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
        switch (id){
            case R.id.action_logout:
                if(ParseUser.getCurrentUser() != null){
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


}
