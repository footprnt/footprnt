/*
 * this.java
 * v1.0
 * July 2019
 * Copyright ©2019 Footprnt Inc.
 */
package com.example.footprnt.Models;

import com.example.footprnt.Util.Constants;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Custom Post Model Class
 *
 * @author Clarisa Leu, Jocelyn Shen
 */
@ParseClassName("Post")
public class Post extends ParseObject implements Serializable {

    /**
     * Getter for the description of post
     *
     * @return description of post
     */
    public String getDescription() {
        return getString(Constants.description);
    }

    /**
     * Setter for the description of post
     *
     * @param description of post to set
     */
    public void setDescription(String description) {
        put(Constants.description, description);
    }

    /**
     * Getter for the title of post
     *
     * @return title of post
     */
    public String getTitle() {
        return getString(Constants.title);
    }

    /**
     * Setter for the title of post
     *
     * @param title of post to set
     */
    public void setTitle(String title) {
        //  this.title = title;
        put(Constants.title, title);
    }

    /**
     * Getter for the image of post
     *
     * @return ParseFile image of post
     */
    public ParseFile getImage() {
        return getParseFile(Constants.image);
    }


    /**
     * Setter for the image of post
     *
     * @param image of post to set
     */
    public void setImage(ParseFile image) {
        put(Constants.image, image);
    }

    /**
     * Getter for the user who made post
     *
     * @return ParseUser who made post
     */
    public ParseUser getUser() {
        return getParseUser(Constants.user);
    }

    /**
     * Setter for the user of post
     *
     * @param user ParseUser to assign to post
     */
    public void setUser(ParseUser user) {
        put(Constants.user, user);
    }

    /**
     * Getter for the location of post
     *
     * @return ParseGeoPoint with the latitude of longitude of where post was made
     */
    public ParseGeoPoint getLocation() {
        return getParseGeoPoint(Constants.location);
    }

    /**
     * Setter for the location of post
     *
     * @param point with the latitude of longitude of where post was made
     */
    public void setLocation(ParseGeoPoint point) {
        put(Constants.location, point);
    }

    /**
     * Getter for the country of where post was made
     *
     * @return country where post was made
     */
    public String getCountry() {
        return getString(Constants.country);
    }

    /**
     * Setter for the country of where post was made
     *
     * @param country where post was made
     */
    public void setCountry(String country) {
        put(Constants.country, country);
    }

    /**
     * Getter for the continent of where post was made
     *
     * @return continent where post was made
     */
    public String getContinent() {
        return getString(Constants.continent);
    }

    /**
     * Setter for the continent of where post was made
     *
     * @param continent where post was made
     */
    public void setContinent(String continent) {
        put(Constants.continent, continent);
    }

    /**
     * Getter for the city of where post was made
     *
     * @return city where post was made
     */
    public String getCity() {
        return getString(Constants.city);
    }

    /**
     * Setter for the city of where post was made
     *
     * @param city where post was made
     */
    public void setCity(String city) {
        put(Constants.city, city);
    }

    /**
     * Getter for associated hash tags of post
     *
     * @return JSONArray with tags of post
     */
    public JSONArray getTags() {
        return getJSONArray(Constants.tags);
    }

    /**
     * Setter for associated hash tags of post
     *
     * @param tags with tags of post
     */
    public void setTags(ArrayList<String> tags) {
        put(Constants.tags, tags);
    }

    /**
     * Inner query class to query posts in database
     */
    public static class Query extends ParseQuery<Post> {
        public Query() {
            super(Post.class);
        }

        public Query getTop() {
            setLimit(Constants.postLimit);
            return this;
        }

        public Query withUser() {
            include(Constants.user);
            return this;
        }

        public Query withinPoint(ParseGeoPoint pg, int distance) {
            whereWithinMiles(Constants.location, pg, distance);
            return this;
        }

        public Query withTag(String tag, ArrayList<String> tags) {
            whereContainedIn(tag, tags);
            return this;
        }
    }
}
