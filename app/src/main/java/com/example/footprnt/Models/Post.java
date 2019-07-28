/*
 * Post.java
 * v1.0
 * July 2019
 * Copyright ©2019 Footprnt Inc.
 */
package com.example.footprnt.Models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

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
@Entity(tableName = "posts")
public class Post extends ParseObject implements Serializable {

    // Keys for getting from database:
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_USER = "user";
    public static final String KEY_LOCATION = "location";
    public static final String KEY_TITLE = "title";
    public static final String KEY_COUNTRY = "country";
    public static final String KEY_CITY = "city";
    public static final String KEY_CONTINENT = "continent";
    public static final String KEY_TAGS = "tags";
    public static final String KEY_OBJECT_ID = "objectId";
    public static final int postLimit = 20;


    // Attributes of post:
    @PrimaryKey
    @ColumnInfo(name = "rowid")
    public static String objectId;
    @ColumnInfo(name = "user")
    public static ParseUser user;
    @ColumnInfo(name = "description")
    public static String description;
    @ColumnInfo(name = "title")
    public static String title;
    @ColumnInfo(name = "city")
    public static String city;
    @ColumnInfo(name = "country")
    public static String country;
    @ColumnInfo(name = "continent")
    public static String continent;
    @ColumnInfo(name = "location")
    public static ParseGeoPoint location;
    @ColumnInfo(name = "tags")
    public static JSONArray tags;
    @ColumnInfo(name = "image")
    public static ParseFile image;


    /**
     * Constructor for Post
     */
    public Post() {
        description = getString(KEY_DESCRIPTION);
        title = getString(KEY_TITLE);
        image = getParseFile(KEY_IMAGE);
        user = getParseUser(KEY_USER);
        location = getParseGeoPoint(KEY_LOCATION);
        city = getString(KEY_CITY);
        country = getString(KEY_COUNTRY);
        continent = getString(KEY_CONTINENT);
        tags = getJSONArray(KEY_TAGS);
    }


    /**
     * Getter for the description of post
     *
     * @return description of post
     */
    public String getDescription() {
        return description;
    }

    /**
     * Setter for the description of post
     *
     * @param description of post to set
     */
    public void setDescription(String description) {
        Post.description = description;
        put(KEY_DESCRIPTION, Post.description);
    }

    /**
     * Getter for the title of post
     *
     * @return title of post
     */
    public String getTitle() {
        return title;
    }

    /**
     * Setter for the title of post
     *
     * @param title of post to set
     */
    public void setTitle(String title) {
        Post.title = title;
        put(KEY_TITLE, Post.title);
    }

    /**
     * Getter for the image of post
     *
     * @return ParseFile image of post
     */
    public ParseFile getImage() {
        return image;
    }

    /**
     * Setter for the image of post
     *
     * @param image of post to set
     */
    public void setImage(ParseFile image) {
        Post.image = image;
        put(KEY_IMAGE, Post.image);
    }

    /**
     * Getter for the user who made post
     *
     * @return ParseUser who made post
     */
    public ParseUser getUser() {
        return user;
    }

    /**
     * Setter for the user of post
     *
     * @param user ParseUser to assign to post
     */
    public void setUser(ParseUser user) {
        Post.user = user;
        put(KEY_USER, Post.user);
    }

    /**
     * Getter for the location of post
     *
     * @return ParseGeoPoint with the latitude of longitude of where post was made
     */
    public ParseGeoPoint getLocation() {
        return location;
    }

    /**
     * Setter for the location of post
     *
     * @param point with the latitude of longitude of where post was made
     */
    public void setLocation(ParseGeoPoint point) {
        location = point;
        put(KEY_LOCATION, Post.location);
    }

    /**
     * Getter for the country of where post was made
     *
     * @return country where post was made
     */
    public String getCountry() {
        return country;
    }

    /**
     * Setter for the country of where post was made
     *
     * @param country where post was made
     */
    public void setCountry(String country) {
        Post.country = country;
        put(KEY_COUNTRY, Post.country);
    }

    /**
     * Getter for the continent of where post was made
     *
     * @return continent where post was made
     */
    public String getContinent() {
        return continent;
    }

    /**
     * Setter for the continent of where post was made
     *
     * @param continent where post was made
     */
    public void setContinent(String continent) {
        Post.continent = continent;
        put(KEY_CONTINENT, Post.continent);
    }

    /**
     * Getter for the city of where post was made
     *
     * @return city where post was made
     */
    public String getCity() {
        return city;
    }

    /**
     * Setter for the city of where post was made
     *
     * @param city where post was made
     */
    public void setCity(String city) {
        Post.city = city;
        put(KEY_CITY, Post.city);
    }

    /**
     * Getter for associated hash tags of post
     *
     * @return JSONArray with tags of post
     */
    public JSONArray getTags() {
        return tags;
    }

    /**
     * Setter for associated hash tags of post
     *
     * @param tags with tags of post
     */
    public void setTags(ArrayList<String> tags) {
        //Post.tags = tags;
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
            setLimit(postLimit);
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

        public Query withTag(String tag, ArrayList<String> tags) {
            whereContainedIn(tag, tags);
            return this;
        }
    }
}
