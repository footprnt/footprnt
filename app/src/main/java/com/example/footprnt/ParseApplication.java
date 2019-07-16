package com.example.footprnt;

import android.app.Application;

import com.parse.Parse;

// TODO: Javadoc
public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

//        ParseObject.registerSubclass(Post.class); // TODO: no commented out code in master

        final Parse.Configuration configuration = new Parse.Configuration.Builder(this)
                .applicationId("explor")
                .clientKey("System.out.footprnt")
                .server("http://footprnt.herokuapp.com/parse")
                .build();

        Parse.initialize(configuration);
    }
}
