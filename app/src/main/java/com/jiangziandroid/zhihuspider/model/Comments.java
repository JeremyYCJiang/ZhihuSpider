package com.jiangziandroid.zhihuspider.model;

/**
 * Created by JeremyYCJiang on 2015/5/21.
 */
public class Comments {
    private String mAuthorAvatarUrl;
    private String mAuthor;
    private int mLikes;
    private String mContent;
    private ReplyToComments mReplyToComments;
    private long mUnixTimestamp;
    private long mAuthorId;

    public String getAuthorAvatarUrl() {
        return mAuthorAvatarUrl;
    }

    public void setAuthorAvatarUrl(String authorAvatarUrl) {
        mAuthorAvatarUrl = authorAvatarUrl;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public void setAuthor(String author) {
        mAuthor = author;
    }

    public int getLikes() {
        return mLikes;
    }

    public void setLikes(int likes) {
        mLikes = likes;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }

    public ReplyToComments getReplyToComments() {
        return mReplyToComments;
    }

    public void setReplyToComments(ReplyToComments replyToComments) {
        mReplyToComments = replyToComments;
    }

    public long getUnixTimestamp() {
        return mUnixTimestamp;
    }

    public void setUnixTimestamp(long unixTimestamp) {
        mUnixTimestamp = unixTimestamp;
    }

    public long getAuthorId() {
        return mAuthorId;
    }

    public void setAuthorId(long authorId) {
        mAuthorId = authorId;
    }
}
