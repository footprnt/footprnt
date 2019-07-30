/*
 * PostDaoAccess.java
 * v1.0
 * July 2019
 * Copyright ©2019 Footprnt Inc.
 */
package com.example.footprnt.Database.Dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.example.footprnt.Models.PostWrapper;

import java.util.List;

/**
 * This is an interface which acts is an intermediary between the user and the database.
 * All the operation to be performed on the posts table is defined here.
 *
 * @author Clarisa Leu-Rodriguez
 */
@Dao
public interface PostDaoAccess {
    @Insert
    void insertPost(PostWrapper post);
// ORDER BY createdAt desc
    @Query("SELECT * FROM posts")
    LiveData<List<PostWrapper>> fetchAllPosts();

    @Query("SELECT * FROM posts WHERE objectId=:objectId")
    LiveData<PostWrapper> getPost(String objectId);
}
