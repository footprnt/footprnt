package com.example.footprnt.Models;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.Serializable;

@ParseClassName("MarkerDetails")
public class MarkerDetails extends ParseObject implements Serializable {

    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_USER = "user";
    private static final String KEY_LOCATION = "location";
    private static final String KEY_TITLE = "title";

    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }

    public void setDescription(String description) {
        put(KEY_DESCRIPTION, description);
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

    public static class Query extends ParseQuery<MarkerDetails> {
        public Query() {
            super(MarkerDetails.class);
        }

        public Query withUser() {
            include("user");
            return this;
        }
    }
}
