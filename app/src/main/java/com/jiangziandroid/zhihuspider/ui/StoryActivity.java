package com.jiangziandroid.zhihuspider.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.jiangziandroid.zhihuspider.R;
import com.jiangziandroid.zhihuspider.model.NewsDetails;
import com.jiangziandroid.zhihuspider.model.NewsExtras;
import com.jiangziandroid.zhihuspider.model.Recommender;
import com.jiangziandroid.zhihuspider.utils.ZhihuAPI;
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
import de.hdodenhof.circleimageview.CircleImageView;

public class StoryActivity extends AppCompatActivity implements ObservableScrollViewCallbacks{
    @InjectView(R.id.scroll)  ObservableScrollView mObservableScrollView;
//    @InjectView(R.id.tool_bar)  android.support.v7.widget.Toolbar mToolbar;
    @InjectView(R.id.AppBarRL) RelativeLayout mAppBarRL;
    @InjectView(R.id.BackHomeImageView) ImageView mBackHomeImageView;
    @InjectView(R.id.ShareImageView) ImageView mShareImageView;
    @InjectView(R.id.CollectImageView) ImageView mCollectImageView;
    @InjectView(R.id.CommentsImageView) ImageView mCommentsImageView;
    @InjectView(R.id.CommentsTextView) TextView mCommentsTextView;
    @InjectView(R.id.ZanImageView) ImageView mZanImageView;
    @InjectView(R.id.ZanTextView) TextView mZanTextView;
    @InjectView(R.id.image) ImageView mImageView;
    @InjectView(R.id.titleText) TextView mTitleTextView;
    @InjectView(R.id.imageSourceText) TextView mImageSourceTextView;
    @InjectView(R.id.recommendersLL) LinearLayout mRecommendersLL;
    @InjectView(R.id.webView)   WebView mWebView;
    protected long mStoryId;
    protected int mParallaxImageHeight;
    protected NewsDetails mNewsDetails;
    protected NewsExtras mNewsExtras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);
        ButterKnife.inject(this);
        mObservableScrollView.setScrollViewCallbacks(this);
//        setSupportActionBar(mToolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);
        // When we add index transparency, every color in the color table is given a transparency designation
        // in addition to its color data  (i.e., RGB values):
        //zero (o = False in Boolean algebra) means do not display this color, or
        //one (1 = True in Boolean Algebra) means display this color.
//        mAppBarRL.setAlpha(1);
//        mToolbar.setAlpha(1);
        setAppBarOnClickListener();
        mParallaxImageHeight = getResources().getDimensionPixelSize(R.dimen.parallax_image_height);
        mStoryId = getIntent().getLongExtra("StoryId", 404);
        getNews();
        getNewsExtras();
    }

    private void setAppBarOnClickListener() {
        mBackHomeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private void getNews() {
        String newsUrl = ZhihuAPI.API_NEWS_DETAILS + mStoryId;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(newsUrl).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                String jsonData = response.body().string();
                try {
                    mNewsDetails = getNewsDetails(jsonData);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Set Header Image field
                            Picasso.with(StoryActivity.this).load(mNewsDetails.getImageStringUri()).into(mImageView);
                            mTitleTextView.setText(mNewsDetails.getTitle());
                            mImageSourceTextView.setText(mNewsDetails.getImageSource());
                            //Set recommenders field
                            if (mNewsDetails.getRecommenders() == null) {
                                mRecommendersLL.setVisibility(View.GONE);
                            } else {
                                int recommendersNumber = mNewsDetails.getRecommenders().size();
                                for (int i = 0; i < recommendersNumber; i++) {
                                    LayoutInflater inflater = (LayoutInflater) getApplicationContext()
                                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                    View v = inflater.inflate(R.layout.recommender_circle_image_view, null);
                                    CircleImageView mRecommenderCircleImageView =
                                            (CircleImageView) v.findViewById(R.id.recommenderCircleImageView);
                                    Picasso.with(StoryActivity.this)
                                            .load(mNewsDetails.getRecommenders().get(i).getAvatarStringUri())
                                            .resize(64, 64)
                                            .centerCrop()
                                            .into(mRecommenderCircleImageView);
                                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams
                                            (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                    layoutParams.setMargins(20, 10, 0, 10);
                                    mRecommendersLL.addView(v, i + 1, layoutParams);
                                }

                            }
                            //Set Main Content field (Using WebView)
                            String noImagePlaceHolderBody = mNewsDetails.getBody()
                                    .replace("<div class=\"img-place-holder\"></div>","");
                            String customUrl = "<html>" +
                                    "<head><link rel=\"stylesheet\" type=\"text/css\" " +
                                    "href=" +   mNewsDetails.getCssStringUri()  + "></head>" +
                                    "<body>"+   noImagePlaceHolderBody + "</body>" +
                                    "</html>";
                            mWebView.loadData(customUrl, "text/html;charset=utf-8", "utf-8");
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private NewsDetails getNewsDetails(String jsonData) throws JSONException {
        NewsDetails newsDetails = new NewsDetails();
        JSONObject jsonNewsDetails = new JSONObject(jsonData);
        newsDetails.setStoryId(jsonNewsDetails.getLong("id"));
        newsDetails.setImageStringUri(jsonNewsDetails.getString("image"));
        newsDetails.setTitle(jsonNewsDetails.getString("title"));
        newsDetails.setImageSource(jsonNewsDetails.getString("image_source"));
        newsDetails.setBody(jsonNewsDetails.getString("body"));
        newsDetails.setCssStringUri(jsonNewsDetails.getJSONArray("css").getString(0));
        if(jsonNewsDetails.has("recommenders")){
            JSONArray jsonArrayRecommenders = jsonNewsDetails.getJSONArray("recommenders");
            newsDetails.setRecommenders(getRecommenders(jsonArrayRecommenders));
        }
        return newsDetails;
    }

    private ArrayList<Recommender> getRecommenders(JSONArray jsonArrayRecommenders) throws JSONException {
        ArrayList<Recommender> recommenderArrayList = new ArrayList<>();
        for(int i=0; i<jsonArrayRecommenders.length();i++){
            JSONObject jsonRecommender = jsonArrayRecommenders.getJSONObject(i);
            Recommender recommender = new Recommender();
            recommender.setAvatarStringUri(jsonRecommender.getString("avatar"));
            recommenderArrayList.add(recommender);
        }
         return recommenderArrayList;
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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mCommentsTextView.setText(String.valueOf(mNewsExtras.getComments()));
                            mZanTextView.setText(String.valueOf(mNewsExtras.getPopularity()));
//                            MenuView.ItemView mCommentsText = (MenuView.ItemView) findViewById(R.id.text_comments);
//                            MenuView.ItemView mZanText = (MenuView.ItemView) findViewById(R.id.text_zan);
//                            mCommentsText.setTitle(String.valueOf(mNewsExtras.getComments()));
//                            mZanText.setTitle(String.valueOf(mNewsExtras.getPopularity()));
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


//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_story, menu);
//        getNewsExtras();
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                //Perform Back instead of Up
//                finish();
//                return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }


    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        //  If the ObservableScrollView is not scrolled, alpha of the Toolbar is 1.
        //      (If scrollY equals to 0, alpha of the Toolbar is 1.)
        //  If the ObservableScrollView is scrolled, it becomes opaque gradually, and when it's scrolled to a
        //  certain point, Toolbar is completely transparent.
        //      (If scrollY equals to mParallaxImageHeight, Toolbar is transparent.)
        // NOTE: scrollY is an absolute value
        float alpha = Math.max(0, 1 - (float) scrollY / mParallaxImageHeight);
        mAppBarRL.setAlpha(alpha);
//        mToolbar.setAlpha(alpha);
        mImageView.setTranslationY(scrollY / 2);
        mTitleTextView.setTranslationY(scrollY / 2);
        mImageSourceTextView.setTranslationY(scrollY / 2);
        if(scrollY >= mParallaxImageHeight){
            mAppBarRL.setVisibility(View.INVISIBLE);
//            getSupportActionBar().hide();
        }else {
            mAppBarRL.setVisibility(View.VISIBLE);
//            getSupportActionBar().show();
        }
    }


    //  We need to handle one more thing: restoring scroll state when the Activity is restored.
    // Note : onSaveInstanceState() is not supposed to be called when the user presses BACK.
//    @Override
//    public void onSaveInstanceState(Bundle savedInstanceState) {
//        // Save the user's current scroll position
//        savedInstanceState.putInt("state_scroll_y", mObservableScrollView.getCurrentScrollY());
//        // Always call the superclass so it can save the view hierarchy state
//        super.onSaveInstanceState(savedInstanceState);
//    }

    //  Instead of restoring the state during onCreate() you may choose to implement onRestoreInstanceState(),
    //  which the system calls after the onStart() method. The system calls onRestoreInstanceState() only if there
    //  is a saved state to restore, so you do not need to check whether the Bundle is null:

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);
//        mObservableScrollView.setScrollY(savedInstanceState.getInt("state_scroll_y"));
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
