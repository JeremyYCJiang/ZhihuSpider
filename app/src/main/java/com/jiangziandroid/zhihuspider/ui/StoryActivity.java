package com.jiangziandroid.zhihuspider.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.ImageView;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.jiangziandroid.zhihuspider.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class StoryActivity extends AppCompatActivity implements ObservableScrollViewCallbacks{
    @InjectView(R.id.scroll)  ObservableScrollView mObservableScrollView;
    @InjectView(R.id.tool_bar)  android.support.v7.widget.Toolbar mToolbar;
    @InjectView(R.id.image) ImageView mImageView;
    @InjectView(R.id.webView)   WebView mWebView;
    protected long mStoryId;
    protected int mParallaxImageHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);
        ButterKnife.inject(this);
        mObservableScrollView.setScrollViewCallbacks(this);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        // When we add index transparency, every color in the color table is given a transparency designation
        // in addition to its color data  (i.e., RGB values):
        //zero (o = False in Boolean algebra) means do not display this color, or
        //one (1 = True in Boolean Algebra) means display this color.
        mToolbar.setAlpha(1);
        mParallaxImageHeight = getResources().getDimensionPixelSize(R.dimen.parallax_image_height);
        mWebView.loadUrl("http://news-at.zhihu.com/api/4/news/4740231");
        mStoryId = getIntent().getLongExtra("StoryId", 404);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_story, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        //  If the ObservableScrollView is not scrolled, alpha of the Toolbar is 1.
        //      (If scrollY equals to 0, alpha of the Toolbar is 1.)
        //  If the ObservableScrollView is scrolled, it becomes opaque gradually, and when it's scrolled to a
        //  certain point, Toolbar is completely transparent.
        //      (If scrollY equals to mParallaxImageHeight, Toolbar is transparent.)
        // NOTE: scrollY is an absolute value
        float alpha = Math.max(0, 1 - (float) scrollY / mParallaxImageHeight);
        mToolbar.setAlpha(alpha);
        mImageView.setTranslationY(scrollY / 2);
        if(scrollY >= mParallaxImageHeight){
            getSupportActionBar().hide();
        }else {
            getSupportActionBar().show();
        }
    }

    //  We need to handle one more thing: restoring scroll state when the Activity is restored.
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        onScrollChanged(mObservableScrollView.getCurrentScrollY(), false, false);
    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        //  What we want to do is:
        //  1.to hide the ActionBar when we swipe up the view, because we want to see the contents.
        //  2.to show the ActionBar when we swipe down the view, because we want to tap a button on the ActionBar
        //  (it could be sharing the contents or going back to the former screen, for example).
    }



}
