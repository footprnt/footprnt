/*
 * SavedActivity.java
 * v1.0
 * July 2019
 * Copyright ©2019 Footprnt Inc.
 */
package com.example.footprnt.Models;

import com.example.footprnt.Util.AppConstants;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.Serializable;

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
