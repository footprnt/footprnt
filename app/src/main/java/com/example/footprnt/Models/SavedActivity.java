/*
 * SavedActivity.java
 * v1.0
 * July 2019
 * Copyright ©2019 Footprnt Inc.
 */
package com.example.footprnt.Models;

import com.example.footprnt.Util.AppConstants;
import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * SavedActivity class for saving activities from discover page functionality
 *
 * @author Clarisa Leu
 */
@ParseClassName("SavedActivity")
public class SavedActivity extends ParseObject implements Serializable {

    public ParseUser getUser() {
        return getParseUser(AppConstants.user);
    }

    public void setUser(ParseUser user) {
        put(AppConstants.user, user);
    }

    public String getName() {
        return getString(AppConstants.name);
    }

    public void setName(String name) {
        put(AppConstants.name, name);
    }

    public String getPhoneNumber() {
        return getString(AppConstants.phone);
    }

    public void setPhoneNumber(String phone) {
        put(AppConstants.phone, phone);
    }

    public String getWebsite() {
        return getString(AppConstants.website);
    }

    public void setWebsite(String website) {
        put(AppConstants.website, website);
    }

    public double getRating() {
        return getNumber(AppConstants.rating).doubleValue();
    }

    public void setRating(Double rating) {
        put(AppConstants.rating, rating);
    }

    public String getImageUrl() {
        return getString(AppConstants.imageUrl);
    }

    public void setImageUrl(String imageUrl) {
        put(AppConstants.imageUrl, imageUrl);
    }

    public ParseGeoPoint getLocation() {
        return getParseGeoPoint(AppConstants.location);
    }

    public void setLocation(ParseGeoPoint location) {
        put(AppConstants.location, location);
    }

    public void setCategories(ArrayList<String> categories) {
        put(AppConstants.categories, categories);
    }

    public JSONArray getCategories() {
        return getJSONArray(AppConstants.categories);
    }

    public void setAddress(ArrayList<String> address) {
        put(AppConstants.address, address);
    }

    public JSONArray getAddress() {
        return getJSONArray(AppConstants.address);
    }

    public static class Query extends ParseQuery<SavedActivity> {
        public Query() {
            super(SavedActivity.class);
        }

        public Query getTop() {
            setLimit(AppConstants.postLimit);
            return this;
        }

        public Query withUser() {
            include(AppConstants.user);
            return this;
        }
    }
}
