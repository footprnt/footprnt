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
import com.example.footprnt.Util.Constants;
import com.parse.Parse;
import com.parse.ParseObject;

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

        final Parse.Configuration configuration = new Parse.Configuration.Builder(this)
                .applicationId(Constants.applicationId)
                .clientKey(Constants.clientKey)
                .server(Constants.server)
                .build();

        Parse.initialize(configuration);
    }
}
