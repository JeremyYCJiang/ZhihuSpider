package com.jiangziandroid.zhihuspider.ui;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.jiangziandroid.zhihuspider.R;
import com.jiangziandroid.zhihuspider.model.NewsDetails;
import com.jiangziandroid.zhihuspider.model.NewsExtras;
import com.jiangziandroid.zhihuspider.model.Recommender;
import com.jiangziandroid.zhihuspider.utils.DpConvertPx;
import com.jiangziandroid.zhihuspider.utils.ParseConstants;
import com.jiangziandroid.zhihuspider.utils.ZhihuAPI;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import de.hdodenhof.circleimageview.CircleImageView;

//import com.parse.ParseException;

public class StoryActivity extends AppCompatActivity implements ObservableScrollViewCallbacks{
    public static final int LOGIN_REQUEST = 0;
    public ParseUser mCurrentUser;
    public ParseRelation<ParseObject> mFavouriteRelation;
    public ParseObject mFavouriteStories;

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
    @InjectView(R.id.image_mask) View mImageMaskImageView;
    @InjectView(R.id.anchor) View mAnchorView;
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
        mParallaxImageHeight = getResources().getDimensionPixelSize(R.dimen.parallax_image_height);
        mStoryId = getIntent().getLongExtra("StoryId", 404);
        mCurrentUser = ParseUser.getCurrentUser();
        getNews();
        getNewsExtras();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getFavouriteIcon();
        setAppBarOnClickListener();
    }



    private void getFavouriteIcon() {
        if(mCurrentUser != null){
            mFavouriteRelation = mCurrentUser.getRelation(ParseConstants.KEY_FAVOURITE_STORY_RELATION);
            ParseQuery<ParseObject> query = mFavouriteRelation.getQuery();
            query.whereEqualTo("storyId", String.valueOf(mStoryId));
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> list, ParseException e) {
                    if (e == null) {
                        if(list.size() == 0){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mCollectImageView.setImageResource(R.drawable.ic_action_collect);
                                }
                            });
                        }else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mCollectImageView.setImageResource(R.drawable.ic_action_collected);
                                }
                            });
                        }
                    } else {
                        Log.e("FindWhenGet", "Error: " + e.getMessage());
                    }
                }
            });
        }
    }


    private void setAppBarOnClickListener() {
        mBackHomeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mCommentsImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCommentsActivity();
            }
        });
        mCommentsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCommentsActivity();
            }
        });
        mCollectImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check if user has logged in
                if (mCurrentUser != null) {
                    //check if this story has saved to favourite
                    //if saved, remove from favourite; if not saved, save to favourite
                    toggleFavouriteStatus();
                } else {
                    // show the login screen
                    Intent intent = new Intent(StoryActivity.this, LoginActivity.class);
                    intent.putExtra("fromStory", "fromStory");
                    startActivityForResult(intent, LOGIN_REQUEST);
                }
            }
        });
        //use ShareSDK to share, just for test
        mShareImageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showShare();
                return true;
            }
        });
        //use app client to share
        mShareImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(StoryActivity.this);
                // Get the layout inflater
                LayoutInflater inflater = getLayoutInflater();
                // Inflate and set the layout for the dialog
                // Pass null as the parent view because its going in the dialog layout
                View dialogView = inflater.inflate(R.layout.share_dialog, null);
                dialogBuilder.setTitle(R.string.share_dialog_title)
                        .setView(dialogView);
                final AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();
                //1.share to sina weibo
                dialogView.findViewById(R.id.weiboImage).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        shareToSinaWeibo();
                        alertDialog.dismiss();
                    }
                });
                //2.share to weixin friends or collect
                dialogView.findViewById(R.id.wechatFriendsOrCollectImage).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        shareToWeixin();
                        alertDialog.dismiss();
                    }
                });
                //3.share to weixin circle
                dialogView.findViewById(R.id.wechatCircleImage).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        shareToWeixinCircle();
                        alertDialog.dismiss();
                    }
                });
                //4.share to email
                dialogView.findViewById(R.id.emailImage).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        shareToEmail();
                        alertDialog.dismiss();
                    }
                });
                //5.share to Pocket
                dialogView.findViewById(R.id.pocketImage).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        shareToPocket();
                        alertDialog.dismiss();
                    }
                });
                //6.share to other platform
                dialogView.findViewById(R.id.otherPlatformImage).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        shareToOtherPlatform();
                        alertDialog.dismiss();
                    }
                });
            }
        });
    }

    private void shareToOtherPlatform() {
        /**
         * NOTE:
         * Basically, it's creating an ACTION_SEND intent for the native email client ONLY, then
         * tacking other intents onto the chooser.
         * Making the original intent email-specific gets rid of all the extra junk like wifi and
         * bluetooth, then I grab the other intents I want from a generic ACTION_SEND of type plaintext,
         * and tack them on before showing the chooser.
         *
         * When I grab the additional intents, I set custom text for each one.
         */
        //create email intent
        Intent emailIntent = new Intent();
        emailIntent.setAction(Intent.ACTION_SEND);
        // Native email client doesn't currently support HTML, but it doesn't hurt to try in case they fix it
        emailIntent.putExtra(Intent.EXTRA_TEXT, ZhihuAPI.API_SHARE_LINK + mStoryId);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, mNewsDetails.getTitle());
        emailIntent.setType("message/rfc822");

        //create an intent chooser
        Intent openInChooser = Intent.createChooser(emailIntent, "Share to :");

        PackageManager packageManager = getPackageManager();
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        //query all the apps that ACTION_SEND can use
        List<ResolveInfo> resolveInfoList = packageManager.queryIntentActivities(sendIntent, 0);
        List<LabeledIntent> labeledIntentList = new ArrayList<>();
        for (int i = 0; i<resolveInfoList.size(); i++){
            ResolveInfo resolveInfo = resolveInfoList.get(i);
            String packageName = resolveInfo.activityInfo.packageName;
            if(packageName.contains("android.gm")){
                emailIntent.setPackage(packageName);
            }
            /**
             * Use Google Play [https://play.google.com/store/apps] to check package name of an app
             */
            //gmail  : com.google.android.gm

            //weibo  : com.sina.weibo
            //weixin : com.tencent.mm
            //QQ     : com.tencent.mobileqq
            //Pocket : com.ideashower.readitlater.pro
            //Mail Master : com.netease.mail

            if(packageName.contains("weibo")||packageName.contains("tencent.mm")||
                    packageName.contains("readitlater")||packageName.contains("mail")){
                Intent intent = new Intent();
                intent.setComponent(new ComponentName(packageName, resolveInfo.activityInfo.name));
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                if(packageName.contains("weibo")){
                    //Add data to the intent, the receiving app will decide what to do with it.
                    intent.putExtra(Intent.EXTRA_TEXT, mNewsDetails.getTitle()+" (share from @知乎小报) "
                            + ZhihuAPI.API_SHARE_LINK + mStoryId);
                }else if(packageName.contains("tencent.mm")){
                    intent.putExtra(Intent.EXTRA_TEXT, mNewsDetails.getTitle()+" (share from @知乎小报) "
                            + ZhihuAPI.API_SHARE_LINK + mStoryId);
                }else if(packageName.contains("readitlater")){
                    intent.putExtra(Intent.EXTRA_TEXT, mNewsDetails.getTitle()+" (share from @知乎小报) "
                            + ZhihuAPI.API_SHARE_LINK + mStoryId);
                }else if(packageName.contains("mail")){
                    intent.putExtra(Intent.EXTRA_TEXT, ZhihuAPI.API_SHARE_LINK + mStoryId);
                    intent.putExtra(Intent.EXTRA_SUBJECT, mNewsDetails.getTitle());
                }
                labeledIntentList.add(new LabeledIntent(intent, packageName,
                        resolveInfo.loadLabel(packageManager), resolveInfo.icon));
            }
        }
        // convert labeledIntentList to array
        LabeledIntent[] labeledIntentArray = labeledIntentList
                .toArray(new LabeledIntent[labeledIntentList.size()]);
        openInChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, labeledIntentArray);
        startActivity(openInChooser);
    }

    public void shareToSinaWeibo(){
        boolean found = false;
        PackageManager packageManager = getPackageManager();
        Intent sinaWeiboIntent = new Intent(Intent.ACTION_SEND);
        sinaWeiboIntent.setType("text/plain");
        //query all the apps that ACTION_SEND can use
        List<ResolveInfo> resolveInfoList = packageManager.queryIntentActivities(sinaWeiboIntent, 0);
        for(ResolveInfo resolveInfo : resolveInfoList){
            String packageName = resolveInfo.activityInfo.packageName;
            if(packageName.contains("sina.weibo")){
                found = true;
                sinaWeiboIntent.setPackage(packageName);
                sinaWeiboIntent.putExtra(Intent.EXTRA_TEXT, mNewsDetails.getTitle() + " (share from @知乎小报) "
                        + ZhihuAPI.API_SHARE_LINK + mStoryId);
                startActivity(sinaWeiboIntent);
                break;
            }
        }
        if(!found){
            Toast.makeText(this, "Please install sina Weibo first!", Toast.LENGTH_SHORT).show();
        }
    }

    public void shareToWeixin(){
        boolean found = false;
        PackageManager packageManager = getPackageManager();
        Intent weixinIntent = new Intent(Intent.ACTION_SEND);
        weixinIntent.setType("text/plain");
        //query all the apps that ACTION_SEND can use
        List<ResolveInfo> resolveInfoList = packageManager.queryIntentActivities(weixinIntent, 0);
        for(ResolveInfo resolveInfo : resolveInfoList){
            String packageName = resolveInfo.activityInfo.packageName;
            if(packageName.contains("tencent.mm")){
                found = true;
                weixinIntent.setPackage(packageName);
                weixinIntent.putExtra(Intent.EXTRA_TEXT, mNewsDetails.getTitle() + " (share from @知乎小报) "
                        + ZhihuAPI.API_SHARE_LINK + mStoryId);
                startActivity(weixinIntent);
                break;
            }
        }
        if(!found){
            Toast.makeText(this, "Please install weixin first!", Toast.LENGTH_SHORT).show();
        }
    }

    public void shareToWeixinCircle(){
        boolean found = false;
        PackageManager packageManager = getPackageManager();
        Intent weixinIntent = new Intent(Intent.ACTION_SEND);
        weixinIntent.setType("image/*");
        //query all the apps that ACTION_SEND can use
        List<ResolveInfo> resolveInfoList = packageManager.queryIntentActivities(weixinIntent, 0);
        for(ResolveInfo resolveInfo : resolveInfoList){
            String packageName = resolveInfo.activityInfo.packageName;
            if(packageName.contains("tencent.mm")){
                found = true;
                weixinIntent.setComponent(new ComponentName(packageName, "com.tencent.mm.ui.tools.ShareToTimeLineUI"));
                weixinIntent.putExtra("Kdescription", mNewsDetails.getTitle() + " (share from @知乎小报) "
                        + ZhihuAPI.API_SHARE_LINK + mStoryId);
                String fileName = "img_0053.png";
                String completePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                                        + "/ZhihuSpider/" + fileName;
                File file = new File(completePath);
                Uri imageUri = Uri.fromFile(file);
                weixinIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                startActivity(weixinIntent);
                break;
            }
        }
        if(!found){
            Toast.makeText(this, "Please install weixin first!", Toast.LENGTH_SHORT).show();
        }
    }

    public void shareToEmail(){
        boolean found = false;
        PackageManager packageManager = getPackageManager();
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("text/plain");
        //query all the apps that ACTION_SEND can use
        List<ResolveInfo> resolveInfoList = packageManager.queryIntentActivities(emailIntent, 0);
        for(ResolveInfo resolveInfo : resolveInfoList){
            String packageName = resolveInfo.activityInfo.packageName;
            if(packageName.contains("mail")){
                found = true;
                emailIntent.setPackage(packageName);
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, mNewsDetails.getTitle());
                emailIntent.putExtra(Intent.EXTRA_TEXT, mNewsDetails.getTitle() + " (share from @知乎小报) "
                        + ZhihuAPI.API_SHARE_LINK + mStoryId);
                startActivity(emailIntent);
                break;
            }
        }
        if(!found){
            Toast.makeText(this, "Please install email client first!", Toast.LENGTH_SHORT).show();
        }
    }

    public void shareToPocket() {
        boolean found = false;
        PackageManager packageManager = getPackageManager();
        Intent pocketIntent = new Intent(Intent.ACTION_SEND);
        pocketIntent.setType("text/plain");
        //query all the apps that ACTION_SEND can use
        List<ResolveInfo> resolveInfoList = packageManager.queryIntentActivities(pocketIntent, 0);
        for(ResolveInfo resolveInfo : resolveInfoList){
            String packageName = resolveInfo.activityInfo.packageName;
            if(packageName.contains("readitlater")){
                found = true;
                pocketIntent.setPackage(packageName);
                pocketIntent.putExtra(Intent.EXTRA_TEXT, mNewsDetails.getTitle() + " (share from @知乎小报) "
                        + ZhihuAPI.API_SHARE_LINK + mStoryId);
                startActivity(pocketIntent);
                break;
            }
        }
        if(!found){
            Toast.makeText(this, "Please install Pocket first!", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            //set the status of the story(if has been collected?)
            mCurrentUser = ParseUser.getCurrentUser();
            getFavouriteIcon();
        }
        else if(resultCode == RESULT_CANCELED){
            Toast.makeText(this, "User canceled login!", Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(this, "Other Error!", Toast.LENGTH_LONG).show();
        }
    }


    private void toggleFavouriteStatus() {
        //query if this story has been collected
        mFavouriteRelation = mCurrentUser.getRelation(ParseConstants.KEY_FAVOURITE_STORY_RELATION);
        ParseQuery<ParseObject> query = mFavouriteRelation.getQuery();
        query.whereEqualTo("storyId", String.valueOf(mStoryId));
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    if (list.size() == 0) {
                        mFavouriteStories = new ParseObject("FavouriteStories");
                        mFavouriteStories.put("storyId", String.valueOf(mStoryId));
                        mFavouriteStories.put("storyTitle", mNewsDetails.getTitle());
                        mFavouriteStories.put("storyImageUrl", mNewsDetails.getImageStringUri());
                        mFavouriteStories.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    mFavouriteRelation.add(mFavouriteStories);
                                    mCurrentUser.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e == null) {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        mCollectImageView.setImageResource(R.drawable.ic_action_collected);
                                                        Toast.makeText(StoryActivity.this, "Saved to my Favourite!", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            } else {
                                                Log.e("SaveRelation", "Error: " + e.getMessage());
                                            }
                                        }
                                    });
                                } else {
                                    Log.e("SaveParseObject", "Error: " + e.getMessage());
                                }
                            }
                        });
                    } else {
                        mFavouriteStories = list.get(0);
                        mFavouriteRelation.remove(mFavouriteStories);
                        mFavouriteStories.deleteInBackground(new DeleteCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    mCurrentUser.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e == null) {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        mCollectImageView.setImageResource(R.drawable.ic_action_collect);
                                                        Toast.makeText(StoryActivity.this, "Removed from my Favourite!", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            } else {
                                                Log.e("RemoveRelation", "Error: " + e.getMessage());
                                            }
                                        }
                                    });
                                } else {
                                    Log.e("DeleteParseObject", "Error: " + e.getMessage());
                                }
                            }
                        });

                    }
                } else {
                    Log.e("FindWhenToggle", "Error: " + e.getMessage());
                }
            }
        });

    }



    private void startCommentsActivity() {
        Intent intent = new Intent(StoryActivity.this, CommentsActivity.class);
        intent.putExtra("StoryId", mStoryId);
        startActivity(intent);
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
                            if (mNewsDetails.getImageStringUri() != null && mNewsDetails.getImageSource() != null) {
                                mTitleTextView.setText(mNewsDetails.getTitle());
                                mImageSourceTextView.setText(mNewsDetails.getImageSource());
                                Picasso.with(StoryActivity.this).load(mNewsDetails.getImageStringUri()).into(mImageView);
                            }else {
                                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                params.setMargins(0, (int) DpConvertPx.convertDpToPixel(56, StoryActivity.this),0,0);
                                mRecommendersLL.setLayoutParams(params);
                                mImageView.setVisibility(View.GONE);
                                mTitleTextView.setVisibility(View.GONE);
                                mImageSourceTextView.setVisibility(View.GONE);
                                mImageMaskImageView.setVisibility(View.GONE);
                                mAnchorView.setVisibility(View.GONE);
                            }
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
                                            (CircleImageView) v.findViewById(R.id.editorCircleImageView);
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
                                    .replace("<div class=\"img-place-holder\"></div>", "");
                            String customUrl = "<html>" +
                                    "<head><link rel=\"stylesheet\" type=\"text/css\" " +
                                    "href=" + mNewsDetails.getCssStringUri() + "></head>" +
                                    "<body>" + noImagePlaceHolderBody + "</body>" +
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
        if(jsonNewsDetails.has("image")){
            newsDetails.setImageStringUri(jsonNewsDetails.getString("image"));
        }
        newsDetails.setTitle(jsonNewsDetails.getString("title"));
        if(jsonNewsDetails.has("image_source")){
            newsDetails.setImageSource(jsonNewsDetails.getString("image_source"));
        }
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


    private void showShare() {
        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(mNewsDetails.getTitle());
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl(ZhihuAPI.API_SHARE_LINK + mStoryId);
        // text是分享文本，所有平台都需要这个字段
        oks.setText(mNewsDetails.getTitle() + " (share from @知乎小报) " + ZhihuAPI.API_SHARE_LINK + mStoryId);
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
//        oks.setImagePath(Environment.getExternalStorageDirectory().getPath());//确保SDcard下面存在此张图片
        // imageUrl是图片的网络路径，新浪微博、人人网、QQ空间和Linked-In支持此字段
        oks.setImageUrl(mNewsDetails.getImageStringUri());
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(ZhihuAPI.API_SHARE_LINK + mStoryId);
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite("share from @知乎小报");
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl(ZhihuAPI.API_SHARE_LINK + mStoryId);

        // 启动分享GUI
        oks.show(this);
    }


}
