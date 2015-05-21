//package com.jiangziandroid.zhihuspider.adapter;
//
//import android.content.Context;
//import android.support.v7.widget.RecyclerView;
//import android.view.ViewGroup;
//
//import com.jiangziandroid.zhihuspider.model.CommentsDetails;
//import com.jiangziandroid.zhihuspider.model.NewsExtras;
//
///**
// * Created by JeremyYCJiang on 2015/5/21.
// */
//public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentsViewHolder> {
//
//    // Declaring Variable to Understand which View is being worked on
//    // IF the view under inflation and population is header or Item
//    private static final int TYPE_LONG_COMMENTS_HEADER = 0;
//    private static final int TYPE_LONG_COMMENTS = 1;
//    private static final int TYPE_SHORT_COMMENTS_HEADER = 2;
//    private static final int TYPE_SHORT_COMMENTS = 3;
//
//    private Context mContext;
//    private NewsExtras mNewsExtras;
//    private CommentsDetails mCommentsDetails;
//
//    public CommentsAdapter(Context context, NewsExtras newsExtras, CommentsDetails commentsDetails){
//        mContext = context;
//        mNewsExtras = newsExtras;
//        mCommentsDetails = commentsDetails;
//    }
//
//
//    @Override
//    public CommentsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        return null;
//    }
//
//    @Override
//    public void onBindViewHolder(CommentsViewHolder holder, int position) {
//
//    }
//
//    @Override
//    public int getItemCount() {
//        return 0;
//    }
//
//
//    public class CommentsViewHolder {
//    }
//}
