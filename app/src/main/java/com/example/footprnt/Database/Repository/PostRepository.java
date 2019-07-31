/*
 * PostRepository.java
 * v1.0
 * July 2019
 * Copyright ©2019 Footprnt Inc.
 */
package com.example.footprnt.Database.Repository;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import com.example.footprnt.Database.Models.PostWrapper;
import com.example.footprnt.Database.PostDatabase;

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
    public List<PostWrapper> mPostWrappers;

    public PostRepository(Context context) {
        postDatabase = PostDatabase.getPostDatabase(context);
    }


    public PostDatabase getPostDatabase() {
        return postDatabase;
    }

    /**
     * Insert post into database
     *
     * @param post
     */
    @SuppressLint("StaticFieldLeak")
    public void insertPost(final PostWrapper post) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                if (postDatabase.daoAccess().getPost(post.getObjectId()) == null) {
                    postDatabase.daoAccess().insertPost(post);
                }
                return null;
            }
        }.execute();
    }

    /**
     * Get all posts in database
     *
     * @return all posts
     */
    public List<PostWrapper> getPosts() {
        if(mPostWrappers==null){
            mPostWrappers = postDatabase.daoAccess().fetchAllPosts();
        }
        return mPostWrappers;
    }



    /**
     * Get post with objectId
     *
     * @param objectId
     * @return post with objectId
     */
    public PostWrapper getPost(String objectId) {
        return postDatabase.daoAccess().getPost(objectId);
    }
}
