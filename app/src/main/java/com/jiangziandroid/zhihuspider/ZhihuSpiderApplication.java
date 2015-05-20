package com.jiangziandroid.zhihuspider;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp.StethoInterceptor;
import com.jiangziandroid.zhihuspider.utils.ParseConstants;
import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.squareup.okhttp.OkHttpClient;

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

        //Stetho - A debug bridge for Android applications
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(
                                Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(
                                Stetho.defaultInspectorModulesProvider(this))
                        .build());
        OkHttpClient client = new OkHttpClient();
        client.networkInterceptors().add(new StethoInterceptor());
    }

    public static void updateParseInstallation(ParseUser user){
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put(ParseConstants.KEY_USER_ID, user.getObjectId());
        installation.saveInBackground();
    }
}
