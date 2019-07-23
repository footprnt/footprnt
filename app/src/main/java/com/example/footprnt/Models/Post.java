/*
 * Post.java
 * v1.0
 * July 2019
 * Copyright ©2019 Footprnt Inc.
 */
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

/**
 * Custom Post Model Class
 *
 * @author Clarisa Leu, Jocelyn Shen
 */
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

    /**
     * Getter for the description of post
     *
     * @return description of post
     */
    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }

    /**
     * Setter for the description of post
     *
     * @param description of post to set
     */
    public void setDescription(String description) {
        put(KEY_DESCRIPTION, description);
    }

    /**
     * Getter for the title of post
     *
     * @return title of post
     */
    public String getTitle() {
        return getString(KEY_TITLE);
    }

    /**
     * Setter for the title of post
     *
     * @param title of post to set
     */
    public void setTitle(String title) {
        put(KEY_TITLE, title);
    }

    /**
     * Getter for the image of post
     *
     * @return ParseFile image of post
     */
    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }

    /**
     * Setter for the image of post
     *
     * @param image of post to set
     */
    public void setImage(ParseFile image) {
        put(KEY_IMAGE, image);
    }

    /**
     * Getter for the user who made post
     *
     * @return ParseUser who made post
     */
    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    /**
     * Setter for the user of post
     *
     * @param user ParseUser to assign to post
     */
    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    /**
     * Getter for the location of post
     *
     * @return ParseGeoPoint with the latitude of longitude of where post was made
     */
    public ParseGeoPoint getLocation() {
        return getParseGeoPoint(KEY_LOCATION);
    }

    /**
     * Setter for the location of post
     *
     * @param point with the latitude of longitude of where post was made
     */
    public void setLocation(ParseGeoPoint point) {
        put(KEY_LOCATION, point);
    }

    /**
     * Getter for the country of where post was made
     *
     * @return country where post was made
     */
    public String getCountry() {
        return getString(KEY_COUNTRY);
    }

    /**
     * Setter for the country of where post was made
     *
     * @param country where post was made
     */
    public void setCountry(String country) {
        put(KEY_COUNTRY, country);
    }

    /**
     * Getter for the continent of where post was made
     *
     * @return continent where post was made
     */
    public String getContinent() {
        return getString(KEY_CONTINENT);
    }

    /**
     * Setter for the continent of where post was made
     *
     * @param continent where post was made
     */
    public void setContinent(String continent) {
        put(KEY_CONTINENT, continent);
    }

    /**
     * Getter for the city of where post was made
     *
     * @return city where post was made
     */
    public String getCity() {
        return getString(KEY_CITY);
    }

    /**
     * Setter for the city of where post was made
     *
     * @param city where post was made
     */
    public void setCity(String city) {
        put(KEY_CITY, city);
    }

    /**
     * Getter for associated hash tags of post
     *
     * @return JSONArray with tags of post
     */
    public JSONArray getTags() {
        return getJSONArray(KEY_TAGS);
    }

    /**
     * Setter for associated hash tags of post
     *
     * @param tags with tags of post
     */
    public void setTags(ArrayList<String> tags) {
        put(KEY_TAGS, tags);
    }

    /**
     * Getter for date the post was created at
     *
     * @return date post was created at
     */
    @Override
    public Date getCreatedAt() {
        return super.getCreatedAt();
    }

    /**
     * Inner query class to query posts in database
     */
    public static class Query extends ParseQuery<Post> {
        public Query() {
            super(Post.class);
        }

        public Query getTop() {
            setLimit(20);
            return this;
        }

        public Query withUser() {
            include(KEY_USER);
            return this;
        }

        public Query withinPoint(ParseGeoPoint pg, int distance) {
            whereWithinMiles(KEY_LOCATION, pg, distance);
            return this;
        }
    }
}
