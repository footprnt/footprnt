/*
 * PostRepository.java
 * v1.0
 * July 2019
 * Copyright ©2019 Footprnt Inc.
 */
package com.example.footprnt.Repository;

import androidx.lifecycle.LiveData;
import androidx.room.Room;
import android.content.Context;
import android.os.AsyncTask;

import com.example.footprnt.Database.PostDatabase;
import com.example.footprnt.Models.PostWrapper;
import com.example.footprnt.Util.AppConstants;

import java.util.List;

/**
 * Repository to mediate between the domain and data mapping layers, acting like an in-memory domain object
 * collection. We access the database class and the DAO class from the repository and perform list of
 * operations such as insert, update, delete, get, etc.
 *
 * @author Clarisa Leu
 */
public class PostRepository {

    public PostDatabase postDatabase;

    public PostRepository(Context context) {
        postDatabase = Room.databaseBuilder(context, PostDatabase.class, AppConstants.POST_DB_NAME).build();
    }

    /**
     * Insert post into database
     *
     * @param post
     */
    public void insertPost(final PostWrapper post) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                postDatabase.daoAccess().insertPost(post);
                return null;
            }
        }.execute();
    }

    /**
     * Get all posts in database
     *
     * @return all posts
     */
    public LiveData<List<PostWrapper>> getPosts() {
        return postDatabase.daoAccess().fetchAllPosts();
    }
}
