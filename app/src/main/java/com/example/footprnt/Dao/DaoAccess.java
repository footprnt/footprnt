package com.example.footprnt.Dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.example.footprnt.Models.Post;

import java.util.List;

/**
 * @author Clarisa Leu-Rodriguez
 */
@Dao
public interface DaoAccess {
    @Insert
    Long insertPost(Post post);

    @Query("SELECT * FROM Post ORDER BY created_at desc")
    LiveData<List<Post>> fetchAllPosts();

    @Query("SELECT * FROM Post WHERE id =:taskId")
    LiveData<Post> getPost(int taskId);

    @Delete
    void deletePost(Post post);
}
