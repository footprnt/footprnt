package com.example.footprnt.Dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.example.footprnt.Models.PostWrapper;

import java.util.List;

/**
 * @author Clarisa Leu-Rodriguez
 */
@Dao
public interface DaoAccess {
    @Insert
    Long insertPost(PostWrapper post);

    @Query("SELECT * FROM posts")
    LiveData<List<PostWrapper>> fetchAllPosts();
}
