package com.jiangziandroid.zhihuspider.model;

/**
 * Created by JeremyYCJiang on 2015/5/21.
 */
public class ReplyToComments {
    private String mAuthorRepliedTo;
    private String mRepliedContent;
    private long mAuthorIdRepliedTo;

    public String getAuthorRepliedTo() {
        return mAuthorRepliedTo;
    }

    public void setAuthorRepliedTo(String authorRepliedTo) {
        mAuthorRepliedTo = authorRepliedTo;
    }

    public String getRepliedContent() {
        return mRepliedContent;
    }

    public void setRepliedContent(String repliedContent) {
        mRepliedContent = repliedContent;
    }

    public long getAuthorIdRepliedTo() {
        return mAuthorIdRepliedTo;
    }

    public void setAuthorIdRepliedTo(long authorIdRepliedTo) {
        mAuthorIdRepliedTo = authorIdRepliedTo;
    }
}
