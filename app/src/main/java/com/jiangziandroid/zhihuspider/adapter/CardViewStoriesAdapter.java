package com.jiangziandroid.zhihuspider.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jiangziandroid.zhihuspider.R;
import com.jiangziandroid.zhihuspider.model.LatestNews;
import com.jiangziandroid.zhihuspider.ui.StoryActivity;
import com.squareup.picasso.Picasso;

/**
 * Created by JeremyYCJiang on 2015/5/26.
 */
public class CardViewStoriesAdapter extends RecyclerView.Adapter<CardViewStoriesAdapter.CardViewHolder> {
    protected Context mContext;
    protected LatestNews mStories;

    public CardViewStoriesAdapter(Context context, LatestNews receivedStories){
        mContext = context;
        mStories = receivedStories;
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stories_item_grid_view , parent, false);
        return new CardViewHolder(view);
    }

    //views(View)
    public class CardViewHolder extends RecyclerView.ViewHolder{
        public ImageView mImageView;
        public TextView mTextView;
        protected long mStoryId;

        public CardViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.imageView);
            mTextView = (TextView) itemView.findViewById(R.id.textView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mStoryId = mStories.getStories().get(getPosition()).getStoryId();
                    Intent intent = new Intent(v.getContext(), StoryActivity.class);
                    intent.putExtra("StoryId", mStoryId);
                    v.getContext().startActivity(intent);
                }
            });
        }

    }


    //Bridge(Controller) and data(Model) mapping code
    @Override
    public void onBindViewHolder(CardViewHolder holder, int position) {
        Picasso.with(mContext)
                .load(mStories.getStories().get(position).getImageStringUri())
                .into(holder.mImageView);
        holder.mTextView.setText(mStories.getStories().get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return mStories.getStories().size();
    }


}
