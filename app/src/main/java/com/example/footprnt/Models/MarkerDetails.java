/*
 * MarkerDetails.java
 * v1.0
 * July 2019
 * Copyright ©2019 Footprnt Inc.
 */
package com.example.footprnt.Models;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.Serializable;

/**
 * @author Jocelyn Shen
 */
@ParseClassName("MarkerDetails")
public class MarkerDetails extends ParseObject implements Serializable {

    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_USER = "user";
    private static final String KEY_LOCATION = "location";
    private static final String KEY_TITLE = "title";
    private static final String KEY_IMAGE = "image";
    private static final String KEY_POST = "post";

    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }

    public void setDescription(String description) {
        put(KEY_DESCRIPTION, description);
    }

    public String getPost() {
        return getString(KEY_POST);
    }

    public void setPost(Post post) {
        put(KEY_POST, post);
    }

    public String getTitle() {
        return getString(KEY_TITLE);
    }

    public void setTitle(String description) {
        put(KEY_TITLE, description);
    }

    public ParseGeoPoint getLocation() {
        return getParseGeoPoint(KEY_LOCATION);
    }

    public void setLocation(ParseGeoPoint point) {
        put(KEY_LOCATION, point);
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public String getImageUrl() {return getString(KEY_IMAGE);}

    public void setImageUrl(String image) { put(KEY_IMAGE, image);}

    public static class Query extends ParseQuery<MarkerDetails> {
        public Query() {
            super(MarkerDetails.class);
        }

        public Query withUser() {
            include(KEY_USER);
            return this;
        }
    }
}
