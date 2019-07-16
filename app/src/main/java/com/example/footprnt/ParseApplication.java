package com.example.footprnt;

import android.app.Application;

import com.example.footprnt.Models.Post;
import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Register Classes:
        ParseObject.registerSubclass(Post.class);

        final Parse.Configuration configuration = new Parse.Configuration.Builder(this)
                .applicationId("explor")
                .clientKey("System.out.footprnt")
                .server("http://footprnt.herokuapp.com/parse")
                .build();

        Parse.initialize(configuration);
    }
}
