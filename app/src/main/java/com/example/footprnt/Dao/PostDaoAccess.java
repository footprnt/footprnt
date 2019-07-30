/*
 * PostDaoAccess.java
 * v1.0
 * July 2019
 * Copyright ©2019 Footprnt Inc.
 */
package com.example.footprnt.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

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
    Long insertPost(PostWrapper post);

    @Query("SELECT * FROM posts")
    LiveData<List<PostWrapper>> fetchAllPosts();
}
