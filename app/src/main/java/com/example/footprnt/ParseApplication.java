/*
 * ParseApplication.java
 * v1.0
 * July 2019
 * Copyright ©2019 Footprnt Inc.
 */
package com.example.footprnt;

import android.app.Application;

import com.example.footprnt.Models.MarkerDetails;
import com.example.footprnt.Models.Post;
import com.example.footprnt.Models.SavedActivity;
import com.example.footprnt.Models.SavedPost;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.facebook.ParseFacebookUtils;

/**
 * Handles parse server application
 *
 * @author Jocelyn Shen, Clarisa Leu
 * @version 1.0
 * @since 2019-07-22
 */
public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // Register Classes:
        ParseObject.registerSubclass(Post.class);
        ParseObject.registerSubclass(MarkerDetails.class);
        ParseObject.registerSubclass(SavedPost.class);
        ParseObject.registerSubclass(SavedActivity.class);

        final Parse.Configuration configuration = new Parse.Configuration.Builder(this)
                .applicationId(getResources().getString(R.string.application_id))
                .clientKey(getResources().getString(R.string.client_key))
                .server(getResources().getString(R.string.server))
                .build();

        Parse.initialize(configuration);

        // For connecting with Facebook:
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        ParseFacebookUtils.initialize(this);
    }
}
