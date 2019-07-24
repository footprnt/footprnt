/*
 * MarkerDetails.java
 * v1.0
 * July 2019
 * Copyright ©2019 Footprnt Inc.
 */
package com.example.footprnt.Models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.Serializable;

/**
 * @author Jocelyn Shen
 */
@ParseClassName("MarkerDetails")
public class MarkerDetails extends ParseObject implements Serializable {

    private static final String KEY_USER = "user";
    private static final String KEY_POST = "post";

    public ParseObject getPost() {
        return getParseObject(KEY_POST);
    }

    public void setPost(Post post) {
        put(KEY_POST, post);
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
            include(KEY_USER);
            return this;
        }
    }
}
