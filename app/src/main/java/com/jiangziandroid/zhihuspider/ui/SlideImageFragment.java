package com.jiangziandroid.zhihuspider.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jiangziandroid.zhihuspider.R;
import com.squareup.picasso.Picasso;

// Create a Fragment class that returns the layout that you just created in the onCreateView() method.
// You can then create instances of this fragment in the parent activity whenever you need a new page
// to display to the user:

public class SlideImageFragment extends Fragment{

    protected ImageView mImageView;
    protected TextView mTextView;
    protected long mStoryId;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.slide_image, container, false);
        mImageView = (ImageView) rootView.findViewById(R.id.fragmentImageView);
        mTextView = (TextView) rootView.findViewById(R.id.fragmentTextView);
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), StoryActivity.class);
                intent.putExtra("StoryId", mStoryId);
                startActivity(intent);
            }
        });
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Bundle bundle = this.getArguments();
        mStoryId = bundle.getLong("StoryId");
        String imageStringUri = bundle.getString("ImageStringUri");
        String imageTitle = bundle.getString("ImageTitle");
        Picasso.with(getActivity()).load(imageStringUri).into(mImageView);
        mTextView.setText(imageTitle);
    }

}