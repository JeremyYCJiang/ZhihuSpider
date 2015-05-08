package com.jiangziandroid.zhihuspider.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.jiangziandroid.zhihuspider.R;
import com.jiangziandroid.zhihuspider.model.SplashInfo;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SplashActivity extends Activity {
    public static final String startupImageAPI = "http://news-at.zhihu.com/api/4/start-image/1080*1776";
    protected SplashInfo mSplashInfo;
    protected Animation mAnimationZoomIn;
    protected Animation mAnimationFadeOut;
    @InjectView(R.id.startupImageView) ImageView mStartupImageView;
    @InjectView(R.id.imageSrcTextView) TextView mImageSrcTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.inject(this);
        // load the animation
        mAnimationZoomIn = AnimationUtils.loadAnimation(this, R.anim.zoomin);
        mAnimationFadeOut = AnimationUtils.loadAnimation(this, R.anim.fadeout);
        getStartupInfo();
    }

    private void getStartupInfo(){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(startupImageAPI).build();
        Call call= client.newCall(request);
        //Transfer synchronous to asynchronous
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                String jsonData = response.body().string();
                try {
                    JSONObject splashInfo = new JSONObject(jsonData);
                    mSplashInfo = new SplashInfo();
                    mSplashInfo.setImageUri(splashInfo.getString("img"));
                    mSplashInfo.setImageSrcInfo(splashInfo.getString("text"));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Update Ui with Model
                            Picasso.with(SplashActivity.this).load(mSplashInfo.getImageUri())
                                    .into(mStartupImageView);
                            mImageSrcTextView.setText(mSplashInfo.getImageSrcInfo());
                            // start the animation
                            mStartupImageView.startAnimation(mAnimationZoomIn);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mStartupImageView.startAnimation(mAnimationFadeOut);
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                                            startActivity(intent);
                                        }
                                    }, 500);
                                }
                            }, 3000);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
