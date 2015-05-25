package com.jiangziandroid.zhihuspider.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jiangziandroid.zhihuspider.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class GridStoriesActivity extends Activity {
    @InjectView(R.id.BackImageView) ImageView mBackImageView;
    @InjectView(R.id.textView)  TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_stories);
        ButterKnife.inject(this);
        //Get NewsAPI from Intent
        String NewsAPI = getIntent().getStringExtra("NewsAPI");
        //Set AppBar OnClickListener
        setAppBarOnClickListener();
        //test
        mTextView.setText(NewsAPI);
    }


    private void setAppBarOnClickListener() {
        mBackImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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
}
