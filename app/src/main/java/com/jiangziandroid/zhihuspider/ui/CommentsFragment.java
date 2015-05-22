package com.jiangziandroid.zhihuspider.ui;


import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jiangziandroid.zhihuspider.R;
import com.jiangziandroid.zhihuspider.adapter.CommentsAdapter;
import com.jiangziandroid.zhihuspider.model.Comments;
import com.jiangziandroid.zhihuspider.model.CommentsDetails;
import com.jiangziandroid.zhihuspider.model.NewsExtras;
import com.jiangziandroid.zhihuspider.model.ReplyToComments;
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
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class CommentsFragment extends android.support.v4.app.Fragment {
    protected long mStoryId;
    protected String mLongOrShort;
    protected TextView mPlaceHolderTextView;
    protected CommentsDetails mCommentsDetails;
    protected RecyclerView mCommentsRecyclerView;
    protected RecyclerView.LayoutManager mCommentsLayoutManager;
    protected CommentsAdapter mCommentsAdapter;
//    protected SwipeRefreshLayout mSwipeRefreshLayout;
    protected NewsExtras mNewsExtras;
    protected String[] mViewPagerTabTitles;
//    protected SlidingTabLayout mSlidingTabs;
    protected TextView mTotalCommentsTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_comments, container, false);
        mCommentsRecyclerView = (RecyclerView) rootView.findViewById(R.id.commentsRecyclerView);
        mCommentsLayoutManager = new LinearLayoutManager(getActivity());
        mCommentsRecyclerView.setLayoutManager(mCommentsLayoutManager);
        mPlaceHolderTextView = (TextView) rootView.findViewById(R.id.placeHolderTextView);
//        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
//        mSwipeRefreshLayout.setOnRefreshListener(this);
//        mSwipeRefreshLayout.setColorSchemeResources(
//                R.color.swipeRefresh1, R.color.swipeRefresh2, R.color.swipeRefresh3, R.color.swipeRefresh4);
//        mTotalCommentsTextView = (TextView) getActivity().findViewById(R.id.TotalCommentsTextView);
//        mSlidingTabs = (SlidingTabLayout) getActivity().findViewById(R.id.SlidingTabs);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Bundle bundle = this.getArguments();
        mStoryId = bundle.getLong("StoryId");
        mLongOrShort = bundle.getString("LongOrShort");
        getComments();
    }

    private void getComments() {
        String commentsUrl = "http://news-at.zhihu.com/api/4/story/"+ mStoryId + "/" + mLongOrShort +"-comments";
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(commentsUrl).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                String jsonData = response.body().string();
                try {
                    mCommentsDetails = getCommentsDetails(jsonData);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            if (mSwipeRefreshLayout.isRefreshing()) {
//                                mSwipeRefreshLayout.setRefreshing(false);
//                                Log.e(getActivity().getPackageName(), "Get comments successfully! ^.^");
//                            }
                            if (mCommentsDetails.getCommentsArrayList().size() == 0) {
                                mPlaceHolderTextView.setVisibility(View.VISIBLE);
                            } else {
                                mPlaceHolderTextView.setVisibility(View.INVISIBLE);
                                mCommentsAdapter = new CommentsAdapter(getActivity(), mCommentsDetails);
                                if (mCommentsRecyclerView.getAdapter() == null) {
                                    //initial the adapter
                                    mCommentsRecyclerView.setAdapter(mCommentsAdapter);
                                    mCommentsRecyclerView.setHasFixedSize(true);
                                } else {
                                    //refill the adapter
                                    ((CommentsAdapter) mCommentsRecyclerView.getAdapter()).refill(mCommentsDetails);
                                }
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private CommentsDetails getCommentsDetails(String jsonData) throws JSONException {
        CommentsDetails commentsDetails = new CommentsDetails();
        commentsDetails.setCommentsArrayList(getCommentDetails(jsonData));
        return commentsDetails;
    }

    private ArrayList<Comments> getCommentDetails(String jsonData) throws JSONException {
        JSONObject jsonComments = new JSONObject(jsonData);
        JSONArray jsonCommentsArray = jsonComments.getJSONArray("comments");
        ArrayList<Comments> commentsArrayList = new ArrayList<>();
        for(int i = 0; i<jsonCommentsArray.length(); i++){
            JSONObject jsonComment = jsonCommentsArray.getJSONObject(i);
            Comments comments = new Comments();
            comments.setAuthor(jsonComment.getString("author"));
            comments.setContent(jsonComment.getString("content"));
            comments.setAuthorAvatarUrl(jsonComment.getString("avatar"));
            comments.setUnixTimestamp(jsonComment.getLong("time"));
            comments.setLikes(jsonComment.getInt("likes"));
            if(jsonComment.has("reply_to")){
                comments.setReplyToComments(getReplayToCommentsDetails(jsonComment));
            }
            commentsArrayList.add(comments);
        }
        return commentsArrayList;
    }

    private ReplyToComments getReplayToCommentsDetails(JSONObject jsonComment) throws JSONException {
        JSONObject jsonReplyToCommentsObject = jsonComment.getJSONObject("reply_to");
        ReplyToComments replyToComments =new ReplyToComments();
        replyToComments.setAuthorRepliedTo(jsonReplyToCommentsObject.getString("author"));
        replyToComments.setRepliedContent(jsonReplyToCommentsObject.getString("content"));
        return replyToComments;
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
                    mViewPagerTabTitles = new String[2];
                    mViewPagerTabTitles[0] = mNewsExtras.getLongComments()+"条长评";
                    mViewPagerTabTitles[1] = mNewsExtras.getShortComments()+"条短评";
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mTotalCommentsTextView.setText(String.valueOf(mNewsExtras.getComments()));
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


//    @Override
//    public void onRefresh() {
////        getNewsExtras();
//        getComments();
//    }
}
