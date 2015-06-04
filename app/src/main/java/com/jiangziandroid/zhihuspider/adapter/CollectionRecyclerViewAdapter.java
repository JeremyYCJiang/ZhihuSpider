package com.jiangziandroid.zhihuspider.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jiangziandroid.zhihuspider.R;
import com.jiangziandroid.zhihuspider.model.Story;
import com.jiangziandroid.zhihuspider.ui.StoryActivity;
import com.jiangziandroid.zhihuspider.utils.ParseConstants;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JeremyYCJiang on 2015/5/11.
 */

public class CollectionRecyclerViewAdapter extends
        RecyclerView.Adapter<CollectionRecyclerViewAdapter.CollectionRecyclerViewViewHolder> {

    private Context mContext;
    private ArrayList<Story> mStoryArrayList;

    public CollectionRecyclerViewAdapter(Context context, ArrayList<Story> storyArrayList){
        mContext = context;
        mStoryArrayList = storyArrayList;
    }


    @Override
    public CollectionRecyclerViewViewHolder onCreateViewHolder(ViewGroup parent, int ViewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.collections_item_row, parent, false);
            return new CollectionRecyclerViewViewHolder(view);
    }


    //views(View)
    public class CollectionRecyclerViewViewHolder extends RecyclerView.ViewHolder{
        public TextView mTextView;
        public ImageView mImageView;
        public ParseUser mCurrentUser;
        public ParseObject mFavouriteStory;
        public ParseRelation<ParseObject> mFavouriteRelation;

        public CollectionRecyclerViewViewHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.rowCollectionText);
            mImageView = (ImageView) itemView.findViewById(R.id.rowCollectionIcon);
            mCurrentUser = ParseUser.getCurrentUser();
            mFavouriteRelation = mCurrentUser.getRelation(ParseConstants.KEY_FAVOURITE_STORY_RELATION);
            //Set short click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, StoryActivity.class);
                    intent.putExtra("StoryId", mStoryArrayList.get(getPosition()).getStoryId());
                    v.getContext().startActivity(intent);
                }
            });
            //Set long click listener
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle(R.string.collection_dialog_title)
                            .setMessage(R.string.collection_dialog_message)
                            .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ParseQuery<ParseObject> query = mFavouriteRelation.getQuery();
                                    query.whereEqualTo("storyId", String.valueOf(mStoryArrayList.get(getPosition())
                                            .getStoryId()));
                                    query.findInBackground(new FindCallback<ParseObject>() {
                                        @Override
                                        public void done(List<ParseObject> list, ParseException e) {
                                            if (e == null) {
                                                mFavouriteStory = list.get(0);
                                                mFavouriteRelation.remove(mFavouriteStory);
                                                mFavouriteStory.deleteInBackground(new DeleteCallback() {
                                                    @Override
                                                    public void done(ParseException e) {
                                                        if (e == null) {
                                                            mCurrentUser.saveInBackground(new SaveCallback() {
                                                                @Override
                                                                public void done(ParseException e) {
                                                                    if (e == null) {
                                                                        Toast.makeText(mContext, "Removed from my Favourite!", Toast.LENGTH_SHORT).show();
                                                                        mStoryArrayList.remove(getPosition());
                                                                        /**
                                                                         * Update recycler View :
                                                                         * The line below is important.
                                                                         */
                                                                        notifyDataSetChanged();
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
                                            } else {
                                                Log.e("FindWhenToggle", "Error: " + e.getMessage());
                                            }
                                        }
                                    });

                                }
                            })
                            .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //do nothing
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return true;
                }
            });
        }
    }


    //Bridge(Controller) and data(Model) mapping code
    @Override
    public void onBindViewHolder(CollectionRecyclerViewAdapter.CollectionRecyclerViewViewHolder holder, int position) {
        holder.mTextView.setText(mStoryArrayList.get(position).getTitle());
        Picasso.with(mContext).load(mStoryArrayList.get(position).getImageStringUri())
                .resize(100,100).centerCrop().into(holder.mImageView);
    }


    @Override
    public int getItemCount() {
        return mStoryArrayList.size();
    }


    public void refill(ArrayList<Story> receivedStories){
        mStoryArrayList.clear();
        mStoryArrayList.addAll(receivedStories);
        notifyDataSetChanged();
    }


}