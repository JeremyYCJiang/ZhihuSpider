package com.jiangziandroid.zhihuspider;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by JeremyYCJiang on 2015/5/8.
 */

public class ZhihuSpiderApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Enable Local DataStore.
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "bIli3YTXLtmF84fmzmxY32U2UR3pltE9Jt1lI2tG",
                "VLjFD42uReKO0R99Cr2lN3STeuuueEDFRrOJZJbs");
//        Test Connection:
//        ParseObject testObject = new ParseObject("TestObject");
//        testObject.put("foo", "bar");
//        testObject.saveInBackground();
    }
}
