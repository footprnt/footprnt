/*
 * SavedPost.java
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
 * SavePost class for saving posts functionality
 *
 * @author Clarisa Leu
 */
@ParseClassName("SavedPost")
public class SavedPost extends ParseObject implements Serializable {

    public ParseObject getPost() {
        return getParseObject(AppConstants.post);
    }

    public void setPost(Post post) {
        put(AppConstants.post, post);
    }

    public ParseUser getUser() {
        return getParseUser(AppConstants.user);
    }

    public void setUser(ParseUser user) {
        put(AppConstants.user, user);
    }

    public static class Query extends ParseQuery<SavedPost> {
        public Query() {
            super(SavedPost.class);
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
