package com.jiangziandroid.zhihuspider.ui;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jiangziandroid.zhihuspider.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CommentsFragment extends android.support.v4.app.Fragment {
    protected long mStoryId;
    protected String mLongOrShort;

    protected TextView mTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_comments, container, false);
        mTextView = (TextView) rootView.findViewById(R.id.textView);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Bundle bundle = this.getArguments();
        mStoryId = bundle.getLong("StoryId");
        mLongOrShort = bundle.getString("LongOrShort");
        mTextView.setText(mStoryId+": "+mLongOrShort);
    }
}
