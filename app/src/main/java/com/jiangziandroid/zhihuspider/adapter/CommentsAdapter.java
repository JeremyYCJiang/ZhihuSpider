package com.jiangziandroid.zhihuspider.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jiangziandroid.zhihuspider.R;
import com.jiangziandroid.zhihuspider.model.CommentsDetails;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by JeremyYCJiang on 2015/5/21.
 */
public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentsViewHolder> {

    private Context mContext;
    private CommentsDetails mCommentsDetails;

    public CommentsAdapter(Context context, CommentsDetails commentsDetails){
        mContext = context;
        mCommentsDetails = commentsDetails;
    }


    @Override
    public CommentsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comments_item , parent, false);
        return new CommentsViewHolder(view);
    }

    //views(View)
    public class CommentsViewHolder extends RecyclerView.ViewHolder{
        public de.hdodenhof.circleimageview.CircleImageView mAuthorImage;
        public TextView mAuthorName;
        public TextView mLikeText;
        public TextView mCommentText;
        public TextView mReplyText;
        public TextView mTimeText;

        public CommentsViewHolder(View itemView) {
            super(itemView);
            mAuthorImage = (CircleImageView) itemView.findViewById(R.id.authorImage);
            mAuthorName = (TextView) itemView.findViewById(R.id.authorName);
            mLikeText = (TextView) itemView.findViewById(R.id.likeText);
            mCommentText = (TextView) itemView.findViewById(R.id.commentText);
            mReplyText = (TextView) itemView.findViewById(R.id.replyText);
            mTimeText = (TextView) itemView.findViewById(R.id.timeText);
        }
    }

    //Bridge(Controller) and data(Model) mapping code
    @Override
    public void onBindViewHolder(CommentsViewHolder holder, int position) {
        Picasso.with(mContext)
                .load(mCommentsDetails.getCommentsArrayList().get(position).getAuthorAvatarUrl())
                .into(holder.mAuthorImage);
        holder.mAuthorName.setText(mCommentsDetails.getCommentsArrayList().get(position).getAuthor());
        holder.mLikeText.setText(String.valueOf(mCommentsDetails.getCommentsArrayList().get(position).getLikes()));
        holder.mCommentText.setText(mCommentsDetails.getCommentsArrayList().get(position).getContent());
        if(mCommentsDetails.getCommentsArrayList().get(position).getReplyToComments() == null){
            holder.mReplyText.setVisibility(View.GONE);
        }
        else {
            holder.mReplyText.setText("@"+mCommentsDetails.getCommentsArrayList().get(position).getReplyToComments()
                    .getAuthorRepliedTo()+": "+mCommentsDetails.getCommentsArrayList().get(position).
                    getReplyToComments().getRepliedContent());
        }
        holder.mTimeText.setText(mCommentsDetails.getCommentsArrayList().get(position).getStringTime());
    }


    @Override
    public int getItemCount() {
        return mCommentsDetails.getCommentsArrayList().size();
    }


    public void refill(CommentsDetails commentsDetails){
        mCommentsDetails.getCommentsArrayList().clear();
        mCommentsDetails.getCommentsArrayList().addAll(commentsDetails.getCommentsArrayList());
        notifyDataSetChanged();
    }


}
