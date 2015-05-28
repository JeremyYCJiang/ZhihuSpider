package com.jiangziandroid.zhihuspider.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jiangziandroid.zhihuspider.R;
import com.jiangziandroid.zhihuspider.model.ThemeContent;
import com.jiangziandroid.zhihuspider.ui.StoryActivity;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by JeremyYCJiang on 2015/5/11.
 */

public class HomepageThemesRecyclerViewAdapter extends
        RecyclerView.Adapter<HomepageThemesRecyclerViewAdapter.HomepageThemesRecyclerViewViewHolder> {

    // Declaring Variable to Understand which View is being worked on
    // IF the view under inflation and population is header or Item
    private static final int TYPE_IMAGE_TITLE = 0;
    private static final int TYPE_EDITORS = 1;
    private static final int TYPE_CONTENT = 2;

    private Context mContext;
    private ThemeContent mThemeContent;

    public HomepageThemesRecyclerViewAdapter(Context context, ThemeContent themeContent){
        mContext = context;
        mThemeContent = themeContent;
    }

    @Override
    public HomepageThemesRecyclerViewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == 0 ){
            View view0 = LayoutInflater.from(parent.getContext()).inflate(R.layout.homepage_theme_image_title_row,
                    parent, false);
            return new HomepageThemesRecyclerViewViewHolder(view0, viewType);
        }else if(viewType == 1){
            View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.homepage_theme_editors_item_row,
                    parent, false);
            return new HomepageThemesRecyclerViewViewHolder(view1, viewType);
        }else {
            View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.homepage_theme_content_item_row,
                    parent, false);
            return new HomepageThemesRecyclerViewViewHolder(view2, viewType);
        }
    }


    //views(View)
    public class HomepageThemesRecyclerViewViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public int HolderId;
        public int index;
        public long mStoryId;
        public ImageView mImageView;
        public TextView mTitleTextView;
        public LinearLayout mEditorsLL;
        public ImageView mThemeContentImage;
        public TextView mThemeContentText;

        public HomepageThemesRecyclerViewViewHolder(View itemView, int viewType) {
            super(itemView);
            if(viewType == 0){
                mImageView = (ImageView) itemView.findViewById(R.id.image);
                mTitleTextView = (TextView) itemView.findViewById(R.id.titleText);
                HolderId = 0;
            }else if(viewType == 1){
                HolderId = 1;
                mEditorsLL = (LinearLayout) itemView.findViewById(R.id.editorsLL);
            }else {
                itemView.setOnClickListener(this);
                mThemeContentImage = (ImageView) itemView.findViewById(R.id.rowHomepageIcon);
                mThemeContentText = (TextView) itemView.findViewById(R.id.rowHomepageText);
                HolderId = 2;
            }
        }

        @Override
        public void onClick(View v) {
            mStoryId = mThemeContent.getThemeStoriesArrayList().get(getPosition()-2).getThemeContentStoryId();
            Intent intent = new Intent(v.getContext(), StoryActivity.class);
            intent.putExtra("StoryId", mStoryId);
            v.getContext().startActivity(intent);
        }
    }


    //Bridge(Controller) and data(Model) mapping code
    @Override
    public void onBindViewHolder(HomepageThemesRecyclerViewViewHolder holder, int position) {
        if(holder.HolderId == 0){
            Picasso.with(mContext)
                    .load(mThemeContent.getThemeBackgroundUrl())
                    .into(holder.mImageView);
            holder.mTitleTextView.setText(mThemeContent.getThemeDescription());
        }
        else if(holder.HolderId == 1){
            int editorsNumber = mThemeContent.getEditorsArrayList().size();
            if(holder.index != editorsNumber){
                for(holder.index = 0; holder.index<editorsNumber; holder.index++){
                    LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View v = inflater.inflate(R.layout.editors_circle_image_view, null);
                    CircleImageView mEditorCircleImageView =(CircleImageView) v.findViewById(R.id.editorCircleImageView);
                    Picasso.with(mContext)
                            .load(mThemeContent.getEditorsArrayList().get(holder.index).getEditorAvatarUrl())
                            .resize(64, 64)
                            .centerCrop()
                            .into(mEditorCircleImageView);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams
                            (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMargins(20, 10, 0, 10);
                    holder.mEditorsLL.addView(v, holder.index + 1, layoutParams);
                }
            }
        }
        else {
            holder.mThemeContentText.setText(mThemeContent.getThemeStoriesArrayList().get(position-2)
                                            .getThemeContentStoryTitle());
                Picasso.with(mContext).load(mThemeContent.getThemeStoriesArrayList().get(position-2)
                    .getImagesUrl()).into(holder.mThemeContentImage);
        }

    }


    //  use position to check which ViewType should ViewHolder create
    //  position = 0 ,          ViewType = TYPE_IMAGE_TITLE;      HolderId = 0
    //  position = 1 ,          ViewType = TYPE_EDITORS;          HolderId = 1
    //  position = 2,3,4...     ViewType = TYPE_CONTENT;          HolderId = 2
    //  and so on ...
    //  use HolderId as condition to bind corresponding data
    @Override
    public int getItemViewType(int position) {
        if(isPositionImageAndTitle(position))
        {
            return TYPE_IMAGE_TITLE; //0
        }else if(isPositionEditors(position)){
            return TYPE_EDITORS;//1
        }else {
            return TYPE_CONTENT;//2
        }
    }
    private boolean isPositionImageAndTitle(int position) {
        return position == 0;
    }
    private boolean isPositionEditors(int position) {
        return position == 1;
    }

    @Override
    public int getItemCount() {
        return mThemeContent.getThemeStoriesArrayList().size()+2;
    }


//    public void refill(ArrayList<Story> receivedStories){
//        mLatestNews.getStories().clear();
//        mLatestNews.getStories().addAll(receivedStories);
//        notifyDataSetChanged();
//    }

}