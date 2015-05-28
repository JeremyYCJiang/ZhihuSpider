package com.jiangziandroid.zhihuspider.utils;

/**
 * Created by JeremyYCJiang on 2015/5/14.
 */
public class ZhihuAPI {

    public static final String API_THEMES = "http://news-at.zhihu.com/api/4/themes";
    public static final String API_THEME_CONTENT = "http://news-at.zhihu.com/api/4/theme/";//need to plus themeId
    public static final String API_LATEST_NEWS = "http://news-at.zhihu.com/api/4/news/latest";
    public static final String API_HISTORY_NEWS = "http://news.at.zhihu.com/api/4/news/before/";//need to plus date+1
    public static final String API_NEWS_DETAILS = "http://news-at.zhihu.com/api/4/news/";//need to plus StoryId
    public static final String API_NEWS_EXTRAS = "http://news-at.zhihu.com/api/4/story-extra/";//need to plus StoryId
}
