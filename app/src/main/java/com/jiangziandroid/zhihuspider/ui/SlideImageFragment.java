package com.jiangziandroid.zhihuspider.ui;

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

public class SlideImageFragment extends Fragment {

    protected ImageView mImageView;
    protected TextView mTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.slide_image, container, false);
        mImageView = (ImageView) rootView.findViewById(R.id.fragmentImageView);
        mTextView = (TextView) rootView.findViewById(R.id.fragmentTextView);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Bundle bundle = this.getArguments();
        String imageStringUri = bundle.getString("ImageStringUri");
        String imageTitle = bundle.getString("ImageTitle");
        Picasso.with(getActivity()).load(imageStringUri).into(mImageView);
        mTextView.setText(imageTitle);
    }
}