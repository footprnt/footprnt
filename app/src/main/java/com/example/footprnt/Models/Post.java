package com.example.footprnt.Models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

@ParseClassName("Post")
public class Post extends ParseObject implements Serializable {
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_IMAGE = "image";
    private static final String KEY_USER = "user";
    private static final String KEY_LOCATION = "location";
    private static final String KEY_TITLE = "title";
    private static final String KEY_COUNTRY = "country";
    private static final String KEY_CITY = "city";
    private static final String KEY_CONTINENT = "continent";
    private static final String KEY_TAGS = "tags";

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

    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }

    public void setImage(ParseFile image) {
        put(KEY_IMAGE, image);
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public ParseGeoPoint getLocation() {
        return getParseGeoPoint(KEY_LOCATION);
    }

    public void setLocation(ParseGeoPoint point) {
        put(KEY_LOCATION, point);
    }

    public String getCountry() {
        return getString(KEY_COUNTRY);
    }

    public void setCountry(String country) {
        put(KEY_COUNTRY, country);
    }

    public String getContinent() {
        return getString(KEY_CONTINENT);
    }

    public void setContinent(String continent) {
        put(KEY_CONTINENT, continent);
    }

    public String getCity() {
        return getString(KEY_CITY);
    }

    public void setCity(String city) {
        put(KEY_CITY, city);
    }

    public JSONArray getTags() {
        return getJSONArray(KEY_TAGS);
    }

    public void setTags(ArrayList<String> tags) {
        put(KEY_TAGS, tags);
    }

    public static class Query extends ParseQuery<Post> {
        public Query() {
            super(Post.class);
        }

        public Query getTop() {
            setLimit(20);
            return this;
        }

        public Query withUser() {
            include("user");
            return this;
        }

        public Query withinPoint(ParseGeoPoint pg, int distance) {
            whereWithinMiles("location", pg, distance);
            return this;
        }
    }

    @Override
    public Date getCreatedAt() {
        return super.getCreatedAt();
    }
}
