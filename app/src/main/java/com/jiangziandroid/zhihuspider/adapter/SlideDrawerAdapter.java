package com.jiangziandroid.zhihuspider.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jiangziandroid.zhihuspider.R;
import com.jiangziandroid.zhihuspider.model.Theme;
import com.jiangziandroid.zhihuspider.model.Themes;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by JeremyYCJiang on 2015/5/11.
 */

public class SlideDrawerAdapter extends RecyclerView.Adapter<SlideDrawerAdapter.SlideDrawerViewHolder> {

    private Themes mThemes;
    private Context mContext;

    public SlideDrawerAdapter(Context context, Themes themes){
        mThemes = themes;
        mContext = context;
    }



    @Override
    public SlideDrawerAdapter.SlideDrawerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.slide_drawer_item_row,
                parent, false);
        return new SlideDrawerViewHolder(view);
    }

    //views(View)
    public static class SlideDrawerViewHolder extends RecyclerView.ViewHolder {
        public ImageView mItemIconImageView;
        public TextView mItemTextTextView;

        public SlideDrawerViewHolder(View itemView) {
            super(itemView);
            mItemIconImageView = (ImageView) itemView.findViewById(R.id.rowIcon);
            mItemTextTextView = (TextView) itemView.findViewById(R.id.rowText);
        }
    }

    //Bridge(Controller) and data(Model) mapping code
    @Override
    public void onBindViewHolder(SlideDrawerAdapter.SlideDrawerViewHolder holder, int position) {
        Picasso .with(mContext)
                .load(mThemes.getThemes().get(position).getThumbnailUri())
                .resize(100,100)
                .centerCrop()
                .into(holder.mItemIconImageView);
        holder.mItemTextTextView.setText(mThemes.getThemes().get(position).getThemeName());
    }


    @Override
    public int getItemCount() {
        return mThemes.getThemes().size();
    }


    public void refill(ArrayList<Theme> receivedThemeNames){
        mThemes.getThemes().clear();
        mThemes.getThemes().addAll(receivedThemeNames);
        notifyDataSetChanged();
    }

}